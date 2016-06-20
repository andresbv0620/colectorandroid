package colector.co.com.collector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.adapters.OptionAdapter;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;
import colector.co.com.collector.model.QuestionVisibilityRules;
import colector.co.com.collector.model.ResponseAttribute;
import colector.co.com.collector.model.ResponseComplex;
import colector.co.com.collector.model.ResponseItem;
import colector.co.com.collector.model.Section;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.SurveySave;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.FindGPSLocation;
import colector.co.com.collector.utils.ImageUtils;
import colector.co.com.collector.views.EditTextItemView;
import colector.co.com.collector.views.MultipleItemViewContainer;
import colector.co.com.collector.views.SectionItemView;
import colector.co.com.collector.views.SpinnerItemView;

import static android.graphics.Color.parseColor;

public class SurveyActivity extends AppCompatActivity implements OnDataBaseSave {
    private FindGPSLocation gps;

    private ArrayList<LinearLayout> pictureLayouts = new ArrayList<>();
    private Survey surveys = AppSession.getInstance().getCurrentSurvey();

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_TAKE_MAPSGPS = 2;
    private static final int REQUEST_TAKE_SIGNATURE = 3;

    @BindView(R.id.fab)
    FloatingActionButton FABGPS;
    @BindView(R.id.fabsave)
    FloatingActionButton FABSAVE;
    @BindView(R.id.survey_container)
    LinearLayout container;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;

    private Long timeStandIni;
    private PopupWindow popupWindow;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);
        showLoading();
        setTitle(surveys.getForm_name());
        setupGPS();
        configureGPSButton();
        configureSaveButton();
        configureInitTime();
        buildSurvey();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupGPS() {
        gps = new FindGPSLocation(this);
        FABGPS.setVisibility(View.INVISIBLE);

    }

    private void configureGPSButton() {
        FABGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps != null && gps.canGetLocation()) {
                    mapGPSIntent(String.valueOf(gps.getLongitude()), String.valueOf(gps.getLatitude()));
                }
            }
        });
        ;
    }

    private void configureSaveButton() {
        FABSAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyBoard();
                threadSaveModulo();
            }
        });
    }

    /**
     * Time stand to init survey
     */
    private void configureInitTime() {
        timeStandIni = System.currentTimeMillis() / 1000;
    }

    private void threadSaveModulo() {
        new AlertDialog.Builder(SurveyActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.survey_save_alert_msg)
                .setPositiveButton(getString(R.string.survey_save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showLoading();
                        if (validateFields()) {
                            saveSurvey();
                        } else {
                            showSnackNotification();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    private void dismissKeyBoard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showSnackNotification() {
        Snackbar snack = Snackbar.make(coordinatorLayout, R.string.check_required, Snackbar.LENGTH_LONG);
        ((TextView) (snack.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(Color.WHITE);
        snack.show();
        hideLoading();
    }

    private boolean validateFields() {
        boolean fieldsValid = true;
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    View sectionView = sectionItemContainer.getChildAt(sectionItemIndex);
                    if (sectionView instanceof EditTextItemView) {
                        fieldsValid = fieldsValid & ((EditTextItemView) sectionView).validateField();
                    } else if (sectionView instanceof SpinnerItemView) {
                        fieldsValid = fieldsValid & ((SpinnerItemView) sectionView).validateField();
                    } else if (sectionView instanceof MultipleItemViewContainer) {
                        fieldsValid = fieldsValid & ((MultipleItemViewContainer) sectionView).validateFields();
                    }
                }
            }
        }
        return fieldsValid;
    }


    private void buildSurvey() {
        container.removeAllViews();
        for (Section section : surveys.getSections()) {
            SectionItemView sectionItem = new SectionItemView(this);
            sectionItem.bind(section.getName());
            buildSection(section, sectionItem.sectionItemsContainer);
            container.addView(sectionItem);
        }
        hideLoading();
    }

    private void buildSection(Section section, LinearLayout linear) {
        for (Question question : section.getInputs()) {
            buildQuestion(question, question.getName(), question.getId(), question.getType(),
                    question.getMin(), question.getMax(), question.getDefectoPrevio(),
                    question.getRequerido(), question.getValidacion(), question.getDefecto(),
                    question.getResponses(), question.getOptions(), question.getAtributos(),
                    question.getValorVisibility(), question.getoculto(), linear);
        }
    }

    private void buildQuestion(Question question, String label, Long id, int type,
                               String min, String max, Boolean defectoPrevio,
                               Boolean required, String Validacio, String defecto,
                               List<IdOptionValue> response, List<ResponseComplex> options, List<ResponseAttribute> atributos,
                               List<QuestionVisibilityRules> ValorVisibility, Boolean oculto, LinearLayout linear) {
        if (required)
            label += "**";

        Log.i(AppSettings.TAG, ">>>>>>>>>item.getType(): " + type);
        switch (type) {
            // Input Text
            case 1:
            case 2:
            case 8:
            default:
                EditTextItemView editItemView = new EditTextItemView(this);
                editItemView.bind(question, surveys.getAnswer(id), this);
                linear.addView(editItemView);
                break;
            // Option Spinner
            case 3:
            case 4:
                SpinnerItemView spinnerItemView = new SpinnerItemView(this);
                spinnerItemView.bind(new ArrayList<>(response), question, surveys.getAnswer(id));
                linear.addView(spinnerItemView);
                break;
            //Multiple opcion
            case 5:
                MultipleItemViewContainer multipleItemViewContainer = new MultipleItemViewContainer(this);
                multipleItemViewContainer.bind(new ArrayList<>(response), question, surveys.getListAnswers(id));
                linear.addView(multipleItemViewContainer);
                break;
            // picture
            case 6:
                linear.addView(buildTextView(label));
                linear.addView(buildImageLinear(id));
                final Long _id = id;
                linear.addView(buildButton(label, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppSession.getInstance().setCurrentPhotoID(_id);
                        dispatchTakePictureIntent(_id);
                    }
                }));
                linear.addView(buildSeparator());
                break;
            // date
            case 7:
                final TextView tv = buildTextView(label);
                linear.addView(tv);
                linear.addView(buildEditText(new View.OnClickListener() {
                    public void onClick(View v) {
                        DialogFragment newFragment = new TimePickerFragment((EditText) v);
                        newFragment.show(SurveyActivity.this.getFragmentManager(), "timePicker");
                    }
                }, id, defectoPrevio));
                break;

            // dynamic form
            case 10:
                LinearLayout toInsertQuestion = new LinearLayout(this);
                ResponseComplex toModify = null;
                toInsertQuestion.setOrientation(LinearLayout.VERTICAL);
                toInsertQuestion.addView(buildSeparator());
                setLayoutParams(toInsertQuestion);
                linear.addView(buildTextView(label));
                linear.addView(buildButton(getString(R.string.survey_search), showPopupSearch(options, toInsertQuestion, id)));

                if (surveys.getInstanceId() != null) {

                    for (ResponseComplex item : options) {
                        String recordID = item.getRecord_id();

                        if ((defectoPrevio || AppSession.getTypeSurveySelected() == AppSettings.SURVEY_SELECTED_EDIT) &&
                                recordID.equals(surveys.getAnswer(id))) {
                            toModify = item;
                            break;
                        }
                    }
                } else {
                    toInsertQuestion.setTag(new ResponseComplex());
                }

                if (toModify == null) {
                    for (ResponseAttribute item : atributos) {
                        buildQuestion(question, item.getLabel(), item.getInput_id(), item.getType(), null, null, null, null, null, null, item.getResponses(), null, null, null, oculto, toInsertQuestion);
                    }
                } else {
                    fillDynamicForm(toModify, toInsertQuestion, id);
                }

                toInsertQuestion.addView(buildSeparator());
                linear.addView(toInsertQuestion);
                break;

            // GPS
            case 12:
                FABGPS.setVisibility(View.VISIBLE);
                break;

            // signature
            case 14:
                linear.addView(buildTextView(label));
                linear.addView(buildImageLinear(id));
                final Long _idSignature = id;
                linear.addView(buildButton(label, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppSession.getInstance().setCurrentPhotoID(_idSignature);
                        dispatchTakeSignatureIntent(_idSignature);
                    }
                }));
                linear.addView(buildSeparator());
                break;
        }
    }


    // ---------- CREATE COMPONENTS -----------

    /**
     * Create programatically a button
     *
     * @param label
     * @return
     */
    private LinearLayout buildButton(String label, View.OnClickListener listener) {

        LinearLayout toReturn = new LinearLayout(this);

        Button button = new Button(this);
        button.setText(label);
        button.setOnClickListener(listener);
        button.setBackgroundResource(R.drawable.rounded_shape);
        button.setTextColor(parseColor("#FFFFFF"));
        button.setPadding(15, 10, 15, 10);
        button.setMinWidth(300);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 30, 30, 30);

        toReturn.setLayoutParams(layoutParams);
        toReturn.setGravity(Gravity.CENTER);
        toReturn.addView(button);

        return toReturn;
    }


    private EditText buildEditText(View.OnClickListener listener, Long id, Boolean defectoPrevio) {
        EditText toReturn = new EditText(this);
        toReturn.setOnClickListener(listener);
        toReturn.setFocusable(false);
        toReturn.setTag(id);
        // set value if is modified
        if (surveys.getInstanceId() != null) {
            if (defectoPrevio || AppSession.getTypeSurveySelected() == AppSettings.SURVEY_SELECTED_EDIT)
                toReturn.setText(surveys.getAnswer(id));
        }
        return toReturn;
    }

    /**
     * Create programtically a textview
     *
     * @param label
     * @return textview with text @param label
     */
    private TextView buildTextView(String label) {
        TextView toReturn = new TextView(this);
        toReturn.setText(label);
        setLayoutParams(toReturn);
        toReturn.setTextColor(ContextCompat.getColor(this, R.color.text_color));
        return toReturn;
    }

    /**
     * Create programtically a LinearLayout to print ImageView
     *
     * @param id
     * @return ImageView with text @param id
     */
    private LinearLayout buildImageLinear(Long id) {
        LinearLayout toReturn = new LinearLayout(this);
        toReturn.setOrientation(LinearLayout.HORIZONTAL);
        toReturn.setTag(id);
        setLayoutParams(toReturn);
        //TODO pintar la imagen que se puso durante la creación
        pictureLayouts.add(toReturn);
        return toReturn;
    }


    /**
     * FIN VISIBILITY RULES
     * */

    /**
     * Create programtically a ImageView
     *
     * @return ImageView with text @param id
     */
    private ImageView buildImageView() {
        ImageView toReturn = new ImageView(this);
        setLayoutParams(toReturn);
        //TODO pintar la imagen que se puso durante la creación
        return toReturn;
    }


    // --------- UTILITIES --------------

    private void setLayoutParams(View view) {

        LinearLayout.LayoutParams layoutWRAP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutMATCH = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutIMAGE = new LinearLayout.LayoutParams(
                100, 100);

        if (view instanceof TextView) {
            layoutWRAP.setMargins(0, 15, 0, 15);
            view.setLayoutParams(layoutWRAP);
        } else if (view instanceof LinearLayout || view instanceof EditText) {
            view.setLayoutParams(layoutMATCH);
        } else if (view instanceof ImageView) {
            layoutIMAGE.setMargins(15, 15, 15, 15);
            view.setLayoutParams(layoutIMAGE);
        }
    }

    private View buildSeparator() {
        View toReturn = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(10, 5, 10, 5);
        toReturn.setLayoutParams(layoutParams);
        toReturn.setBackgroundColor(Color.rgb(51, 51, 51));
        return toReturn;
    }


    private View.OnClickListener showPopupSearch(final List<ResponseComplex> options, final LinearLayout linear, final Long idQuestion) {

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_search, null);
                ListView listOptions = (ListView) popupView.findViewById(R.id.search_list_option);
                popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                listOptions.setAdapter(new OptionAdapter(popupView.getContext(), options));

                listOptions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        fillDynamicForm(options.get(position), linear, idQuestion);
                        popupWindow.dismiss();
                    }
                });

                ((Button) popupView.findViewById(R.id.search_close)).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        popupWindow.dismiss();
                    }
                });
            }
        };

    }

    /**
     * to fill diferents fields from opcion choose
     */
    private void fillDynamicForm(ResponseComplex option, LinearLayout linear, Long idQuestion) {

        linear.removeAllViews();

        LinearLayout toInsertQuestion = new LinearLayout(this);
        toInsertQuestion.setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(toInsertQuestion);


        linear.setTag(new IdValue(idQuestion, option.getRecord_id(), null));
        for (ResponseItem data : option.getResponses()) {
            TextView toInsert = new TextView(SurveyActivity.this);
            toInsert.setText(data.getLabel() + ": " + data.getValue());
            toInsert.setTextColor(ContextCompat.getColor(SurveyActivity.this, R.color.text_color));
            linear.addView(toInsert);
        }


    }

    /**
     * Back button event
     */
    @Override
    public void onBackPressed() {
        exitFormAlert();
    }

    /**
     * Back button event
     */
    public void exitFormAlert() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.survey_back)
                .setPositiveButton(getString(R.string.survey_back_discard), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Survey Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://colector.co.com.collector/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Survey Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://colector.co.com.collector/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    @SuppressLint("ValidFragment")
    public static class TimePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditText toPrint;

        public TimePickerFragment(EditText toPrint) {
            super();
            this.toPrint = toPrint;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            toPrint.setText(day + "/" + month + "/" + year);
        }
    }

    private void mapGPSIntent(String Longitude, String Latitude) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        intent.putExtra("Longitude", Longitude);
        intent.putExtra("Latitude", Latitude);

        startActivityForResult(intent, REQUEST_TAKE_MAPSGPS);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dispatchTakeSignatureIntent(Long id) {
        Intent intentSignature = new Intent(getApplicationContext(), SignatureActivity.class);

        Bundle extras = getIntent().putExtra("idImageView", id).getExtras();

        startActivityForResult(intentSignature, REQUEST_TAKE_SIGNATURE, extras);
    }


    /**
     * Event to button in question type picture
     *
     * @param id
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dispatchTakePictureIntent(Long id) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                Bundle extras = getIntent().putExtra("idImageView", id).getExtras();

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO, extras);
            }
        }
    }

    /**
     * Create file to take a picture
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String storageDir = Environment.getExternalStorageDirectory() + "/collector";
        File dir = new File(storageDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File image = new File(storageDir + "/" + imageFileName + ".jpg");
        AppSession.getInstance().setCurrentPhotoPath(image.getAbsolutePath());

        return image;
    }

    /**
     * Result of process to take a picture
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //ACTIVIDAD REGRESA FOTOGRAFIA
        try {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
                setPic(AppSession.getInstance().getCurrentPhotoPath());
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ConfigureCamera Err=" + e, Toast.LENGTH_LONG).show();
        }

        //ACTIVIDAD REGRESA GPS
        try {
            if (requestCode == REQUEST_TAKE_MAPSGPS && resultCode == Activity.RESULT_OK) {
                Toast.makeText(getApplicationContext(), "AQUI ACCION GPS", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ConfigureGPS Err=" + e, Toast.LENGTH_LONG).show();
        }

        //ACTIVIDAD REGRESA SIGNATURE
        try {
            if (requestCode == REQUEST_TAKE_SIGNATURE && resultCode == Activity.RESULT_OK) {
                setPic(AppSession.getInstance().getCurrentPhotoPath());
                Toast.makeText(getApplicationContext(), "Firma OK", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "ConfigureSignature Err=" + e, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set the picture in image view
     */
    private void setPic(String mCurrentPhotoPath) {
        final String mCurrentPhotoPathFinal = mCurrentPhotoPath;
        //Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        Bitmap bitmap = ImageUtils.getInstant().getCompressedBitmap(mCurrentPhotoPath);

        final ImageView imageView = buildImageView();
        Long idImageView = getIntent().getExtras().getLong("idImageView", -1);
        if (idImageView > -1) {
            imageView.setImageBitmap(bitmap);
            imageView.getLayoutParams().height = 100;
            imageView.getLayoutParams().width = 100;


            for (final LinearLayout item : pictureLayouts)
                if (item.getTag() != null && AppSession.getInstance().getCurrentPhotoID() != null) {
                    Long tagID = (Long) item.getTag();
                    if (tagID.equals(AppSession.getInstance().getCurrentPhotoID())) {
                        item.addView(imageView);
                        ScrollView scroll = new ScrollView(this);
                        scroll.addView(item);
                        imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getBaseContext(),
                                        "mCurrentPhotoPath=" + mCurrentPhotoPathFinal,
                                        Toast.LENGTH_LONG).show();
                                showDialogPicture(mCurrentPhotoPathFinal, imageView, item);
                            }
                        });

                    }

                }

        }
    }

    private void showDialogPicture(final String mCurrentPhotoPath, final ImageView prevView, final LinearLayout item) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double widthPixels = metrics.widthPixels * 0.90;
        double heightPixels = metrics.heightPixels * 0.80;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LinearLayout layout = new LinearLayout(this);

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);


        final ImageView imageView = buildImageView();
        imageView.setImageBitmap(bitmap);
        imageView.getLayoutParams().height = (int) widthPixels;
        imageView.getLayoutParams().width = (int) heightPixels;

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);

        layout.addView(imageView);
        alert.setTitle(getString(R.string.survey_delete_dialog_title));
        alert.setView(layout);

        alert.setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alert.setPositiveButton(getString(R.string.common_erase), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                item.removeView(prevView);
                File filePicture = new File(mCurrentPhotoPath);
                DeleteRecursive(filePicture);
            }
        });
        alert.show();
    }

    private static void DeleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                DeleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private void saveSurvey() {

        SurveySave surveySave = new SurveySave();
        surveySave.setInstanceId(surveys.getForm_id());
        if (surveys.getInstanceId() == null)
            surveySave.setId(DatabaseHelper.getInstance().getNewSurveyIndex(surveys.getForm_id()));
        else
            surveySave.setId(surveys.getInstanceId());
        surveySave.setLatitude(String.valueOf(gps.getLatitude()));
        surveySave.setLongitude(String.valueOf(gps.getLongitude()));
        surveySave.setHoraIni(String.valueOf(timeStandIni));
        surveySave.setHoraFin(String.valueOf(System.currentTimeMillis() / 1000));
        // Difficult Task
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    View sectionView = sectionItemContainer.getChildAt(sectionItemIndex);

                    if (sectionView instanceof EditTextItemView) {
                        surveySave.getResponses().add(((EditTextItemView) sectionView).getResponse());
                    } else if (sectionView instanceof SpinnerItemView) {
                        surveySave.getResponses().add(((SpinnerItemView) sectionView).getResponse());
                    } else if (sectionView instanceof MultipleItemViewContainer)
                        surveySave.getResponses().addAll(((MultipleItemViewContainer)
                                sectionView).getResponses());

                }
            }
        }
        // Save on DataBase
        DatabaseHelper.getInstance().addSurvey(surveySave, this);
    }

    @Override
    public void onSuccess() {
        hideLoading();
        finish();
    }

    @Override
    public void onError() {
        hideLoading();
        Toast.makeText(this, R.string.survey_save_error, Toast.LENGTH_SHORT).show();
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }
}