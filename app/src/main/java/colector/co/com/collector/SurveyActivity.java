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
import android.graphics.drawable.BitmapDrawable;
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
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.adapters.OptionAdapter;
import colector.co.com.collector.adapters.SurveyAdapterMultipleType;
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
import colector.co.com.collector.persistence.dao.SurveyDAO;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.FindGPSLocation;
import colector.co.com.collector.utils.ImageUtils;
import colector.co.com.collector.views.EditTextItemView;
import colector.co.com.collector.views.MultipleItemViewContainer;
import colector.co.com.collector.views.SectionItemView;
import colector.co.com.collector.views.SpinnerItemView;

import static android.graphics.Color.parseColor;

public class SurveyActivity extends AppCompatActivity {
    FindGPSLocation gps;
    private ArrayList<LinearLayout> pictureLayouts = new ArrayList<>();
    private Survey surveys = AppSession.getInstance().getCurrentSurvey();
    private boolean isModify = false;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_TAKE_MAPSGPS = 2;
    public static final int REQUEST_TAKE_SIGNATURE = 3;

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

    Long timeStandIni;

    public PopupWindow popupWindow;
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
        loading.setVisibility(View.VISIBLE);
        setTitle(surveys.getForm_name());
        setupGPS();
        configureGPSButton();
        configureSaveButton();
        configureInitTime();
        isModifiedSurvey();
        buildSurvey();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void isModifiedSurvey() {
        if (surveys.getInstanceId() != null) isModify = true;
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
                        loading.setVisibility(View.VISIBLE);
                        if (validateFields()) {
                            processSave();
                        } else {
                            showSnackNotification();
                        }
                        loading.setVisibility(View.GONE);

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
        loading.setVisibility(View.GONE);
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
                spinnerItemView.bind(new ArrayList<>(response), question);
                linear.addView(spinnerItemView);
                break;
            //Multiple opcion
            case 5:
                MultipleItemViewContainer multipleItemViewContainer = new MultipleItemViewContainer(this);
                multipleItemViewContainer.bind(new ArrayList<>(response), question);
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
                    isModify = true;

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
            isModify = true;
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

    /**
     * Get position of the selected option
     *
     * @param id      of answer
     * @param answers lsit of answer
     * @return position of option selected
     */
    public int getIndexToSpinner(Long id, List<IdOptionValue> answers) {
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).getId().equals(id)) {
                return i;
            }
        }
        return 0;
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

    /**
     * Convert Bitmap to String to save information in database
     *
     * @param bitmap
     * @return
     */
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }


    String msgSaved;

    private void processSave() {
        msgSaved = "Inicia proceos de guardado";
        boolean isValid = true;
        boolean validationCases = true;
        SurveySave toInsert = new SurveySave();

        for (int i = 0; i < container.getChildCount(); i++) {

            if (container.getChildAt(i) instanceof LinearLayout && container.getChildAt(i).getTag() == null) {
                LinearLayout toFind = (LinearLayout) container.getChildAt(i);

                for (int j = 0; j < toFind.getChildCount(); j++) {

                    if (toFind.getChildAt(j) instanceof LinearLayout && toFind.getChildAt(j).getTag() != null && toFind.getChildAt(j).getTag() instanceof IdValue) {

                        IdValue toInsertValue = (IdValue) toFind.getChildAt(j).getTag();
                        toInsert.getResponses().add(toInsertValue);

                    } else if (toFind.getChildAt(j) instanceof LinearLayout && toFind.getChildAt(j).getTag() != null && toFind.getChildAt(j).getTag() instanceof ResponseComplex) {

                        LinearLayout toFindDynamic = (LinearLayout) toFind.getChildAt(j);
                        for (int k = 0; k < toFindDynamic.getChildCount(); k++) {
                            boolean isResponseOK = buildObjectToSave(toFindDynamic.getChildAt(k), toInsert.getResponses());
                            if (!isResponseOK) {
                                isValid = false;
                            }
                        }

                    } else {

                        boolean isResponseOK = buildObjectToSave(toFind.getChildAt(j), toInsert.getResponses());
                        if (!isResponseOK) {
                            isValid = false;
                        }
                    }
                }
            }
        }//15664202

        msgSaved = "valida respuestas";
        for (int vv = 0; vv < toInsert.getResponses().size(); vv++) {
            try {
                double sums = 0;
                boolean operaFlag = false;
                if (!toInsert.getResponses().get(vv).getValidation().equalsIgnoreCase("")) {
                    if (toInsert.getResponses().get(vv).getValidation().contains("(")) {
                        String[] partsums = null;
                        String sumatoria = toInsert.getResponses().get(vv).getValidation().substring(toInsert.getResponses().get(vv).getValidation().indexOf("(") + 1,
                                toInsert.getResponses().get(vv).getValidation().indexOf(")"));

                        partsums = sumatoria.split("\\+");
                        sums = 0;
                        for (int pp = 0; pp < partsums.length; pp++) {
                            sums += Double.valueOf(mapValidation.get(partsums[0]));
                        }
                        operaFlag = true;
                    }
                    String[] parts = null;
                    int casoValidation = -1;
                    if (toInsert.getResponses().get(vv).getValidation().contains(">=")) {
                        String temPart = toInsert.getResponses().get(vv).getValidation().replaceAll("=", "");
                        temPart = temPart.replaceAll("\\(", "");
                        temPart = temPart.replaceAll("\\)", "");
                        parts = temPart.split(">");
                        casoValidation = 3;
                    } else if (toInsert.getResponses().get(vv).getValidation().contains("<=")) {
                        String temPart = toInsert.getResponses().get(vv).getValidation().replaceAll("=", "");
                        temPart = temPart.replaceAll("\\(", "");
                        temPart = temPart.replaceAll("\\)", "");
                        parts = temPart.split("<");
                        casoValidation = 4;
                    } else if (toInsert.getResponses().get(vv).getValidation().contains("<")) {
                        parts = toInsert.getResponses().get(vv).getValidation().split("<");
                        casoValidation = 1;
                    } else if (toInsert.getResponses().get(vv).getValidation().contains(">")) {
                        parts = toInsert.getResponses().get(vv).getValidation().split(">");
                        casoValidation = 2;
                    } else if (toInsert.getResponses().get(vv).getValidation().contains("=")) {
                        parts = toInsert.getResponses().get(vv).getValidation().split("=");
                        casoValidation = 0;
                    }

                    Double part1 = null;
                    Double part2 = null;
                    if (parts != null) {
                        part1 = Double.valueOf(mapValidation.get(parts[0]));
                        if (operaFlag == false)
                            part2 = Double.valueOf(mapValidation.get(parts[1]));
                        else
                            part2 = sums;
                    }
                    switch (casoValidation) {
                        case 0: //igual que
                            if (part1 == part2)
                                ;
                            else {
                                //Toast.makeText(getApplicationContext(), "Valores Invalidos " + toInsert.getResponses().get(vv).getValidation(), Toast.LENGTH_LONG).show();
                                validationCases = false;
                                isValid = false;
                            }
                            break;
                        case 1: //menor que
                            if (part1 < part2)
                                ;
                            else {
                                //Toast.makeText(getApplicationContext(), "Valores Invalidos " + toInsert.getResponses().get(vv).getValidation(), Toast.LENGTH_LONG).show();
                                validationCases = false;
                                isValid = false;
                            }
                            break;
                        case 2: //mayor que
                            if (part1 > part2)
                                ;
                            else {
                                //Toast.makeText(getApplicationContext(), "Valores Invalidos " + toInsert.getResponses().get(vv).getValidation(), Toast.LENGTH_LONG).show();
                                validationCases = false;
                                isValid = false;
                            }
                            break;
                        case 3: //mayor igual que
                            if (part1 >= part2)
                                ;
                            else {
                                //Toast.makeText(getApplicationContext(), "Valores Invalidos " + toInsert.getResponses().get(vv).getValidation(), Toast.LENGTH_LONG).show();
                                validationCases = false;
                                isValid = false;
                            }
                            break;
                        case 4: //menor igual que
                            if (part1 <= part2)
                                ;
                            else {
                                //Toast.makeText(getApplicationContext(), "Valores Invalidos " + toInsert.getResponses().get(vv).getValidation(), Toast.LENGTH_LONG).show();
                                validationCases = false;
                                isValid = false;
                            }
                            break;
                        default:
                            validationCases = false;
                            isValid = false;
                            break;
                    }
                }
            } catch (Exception e) {
                isValid = false;
                //validacion o valores erroneos
            }
        }


        if (isValid) {
            toInsert.setId(surveys.getForm_id());
            // TODO implemnetar el api geografica
            /**
             toInsert.setLatitude("-76.52566394999997");
             toInsert.setLongitude("3.3950707201144508");

             toInsert.setHoraIni("448698600");
             toInsert.setHoraFin("448741800");

             /************************************************/

            String latitude = "0.0";
            String longitude = "0.0";

            toInsert.setId(surveys.getForm_id());

            Long result;
            if (isModify) {
                toInsert.setStatus("FALSE");//Sin enviar
                //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                toInsert.setInstanceId(surveys.getInstanceId());

                result = new SurveyDAO(SurveyActivity.this).modifySurveyInstance(toInsert);
            } else {
                // TODO implemnetar el api geografica

                //gps = new FindGPSLocation(getBaseContext());
                if (gps.canGetLocation()) {
                    latitude = "" + gps.getLatitude();
                    longitude = "" + gps.getLongitude();

                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                    toInsert.setLatitude(latitude);
                    toInsert.setLongitude(longitude);

                } else {
                    toInsert.setLatitude(latitude);
                    toInsert.setLongitude(longitude);
                    //gps.showSettingsAlert();
                }

                //envia el timeStamp de inicio de survery
                toInsert.setHoraIni("" + timeStandIni);

                //envia el timeStamp de fin de survery
                toInsert.setHoraFin("" + System.currentTimeMillis() / 1000);

                result = new SurveyDAO(SurveyActivity.this).saveSurveyInstance(toInsert);
            }
            // Validate result to print message
            if (result != -1) {
                if (!isModify) {
                    //Toast.makeText(SurveyActivity.this, getString(R.string.survey_save_ok, String.valueOf(result)), Toast.LENGTH_LONG).show();
                    msgSaved = getString(R.string.survey_save_ok, String.valueOf(result));
                } else {
                    //Toast.makeText(SurveyActivity.this, getString(R.string.survey_modify_ok, String.valueOf(result)), Toast.LENGTH_LONG).show();
                    msgSaved = getString(R.string.survey_modify_ok, String.valueOf(result));
                }
                finish();
            } else {
                //Toast.makeText(SurveyActivity.this, getString(R.string.survey_save_error), Toast.LENGTH_LONG).show();
                msgSaved = getString(R.string.survey_save_error);
            }
        } else {
            //Toast.makeText(SurveyActivity.this, getString(R.string.survey_save_error_field), Toast.LENGTH_LONG).show();
            if (!validationCases)
                msgSaved = "Validation Field Case Error - ";
            msgSaved += getString(R.string.survey_save_error_field);
        }
    }

    /**
     * Validation an object to save survey
     */
    /**
     * Build an object to save survey
     */
    Map<String, String> mapValidation = new HashMap<String, String>();

    private boolean buildObjectToSave(View view, List<IdValue> arrayResponse) {
        Long id_Questions = (Long) view.getTag();
        int tipo = -1;
        String min = "";
        String max = "";
        Boolean defecto_previo;
        Boolean requerido = false;
        String validacion = "";
        String defecto = "";
        String solo_lectura = "";
        String oculto = "";
        //String orden = "";

        //Se recuperan las caracteristicas del tipo de Entrada
        if (id_Questions != null) {
            for (Section section : surveys.getSections()) {
                for (Question question : section.getInputs()) {
                    if (id_Questions == question.getId()) {
                        tipo = question.getType();
                        min = question.getMin();
                        max = question.getMax();
                        defecto_previo = question.getDefectoPrevio();
                        requerido = question.getRequerido();
                        if (requerido == null)
                            requerido = false;
                        validacion = question.getValidacion();
                        if (validacion == null)
                            validacion = "";
                    }
                }
            }
        }

        //requerido =false;/**********************************////////////////////****************************/

        if (view instanceof EditText) {
            EditText toProcess = (EditText) view;
            if (toProcess != null && !toProcess.getText().toString().isEmpty() && toProcess.getVisibility() == View.VISIBLE) {
                mapValidation.put(toProcess.getTag().toString(), toProcess.getText().toString());

                arrayResponse.add(new IdValue((Long) toProcess.getTag(), toProcess.getText().toString(), validacion));
            } else {
                if (requerido && toProcess.getVisibility() == View.VISIBLE)
                    return false;
                else if (requerido && toProcess.getVisibility() == View.INVISIBLE) {
                    toProcess.setText("");
                    mapValidation.put(toProcess.getTag().toString(), toProcess.getText().toString());
                    arrayResponse.add(new IdValue((Long) toProcess.getTag(), toProcess.getText().toString(), validacion));
                }
            }

        } else if (view instanceof ListView) {
            ListView toProcess = (ListView) view;

            if (toProcess != null) {

                SurveyAdapterMultipleType keyValues = (SurveyAdapterMultipleType) toProcess.getAdapter();
                List<IdOptionValue> lstSelectedValues = keyValues.getTrueStatusItems();

                if (lstSelectedValues.size() > 0) {

                    for (IdOptionValue item : lstSelectedValues) {
                        arrayResponse.add(new IdValue((Long) toProcess.getTag(), String.valueOf(item.getId()), validacion));
                    }
                } else {
                    if (requerido)
                        return false;
                }

            } else {
                return false;
            }
        } else if (view instanceof Spinner) {
            Spinner toProcess = (Spinner) view;

            if (toProcess != null && toProcess.getVisibility() == View.VISIBLE) {
                try {
                    arrayResponse.add(new IdValue((Long) toProcess.getTag(), String.valueOf(((IdOptionValue) toProcess.getSelectedItem()).getId()), validacion));
                } catch (Exception e) {
                    msgSaved += " Input empty - ID " + id_Questions;
                    if (requerido && toProcess.getVisibility() == View.VISIBLE)
                        return false;
                }
            } else {
                if (requerido && toProcess.getVisibility() == View.VISIBLE)
                    return false;
                else if (requerido && toProcess.getVisibility() == View.INVISIBLE) {
                    arrayResponse.add(new IdValue((Long) toProcess.getTag(), "", validacion));
                }
            }

        } else if (view instanceof LinearLayout && view.getTag() != null) {
            LinearLayout toProcessLinear = (LinearLayout) view;
            Long idQuestion = (Long) toProcessLinear.getTag();

            // Se recorren todos los elementos de linear layout buscando los imageview de las imagenes
            for (int k = 0; k < toProcessLinear.getChildCount(); k++) {

                if (toProcessLinear.getChildAt(k) instanceof ImageView) {
                    ImageView toProcess = (ImageView) toProcessLinear.getChildAt(k);
                    if (toProcess != null && toProcess.getDrawable() != null) {
                        String base64 = getEncoded64ImageStringFromBitmap(((BitmapDrawable) toProcess.getDrawable()).getBitmap());
                        arrayResponse.add(new IdValue(idQuestion, base64, validacion));
                    } else {
                        if (requerido)
                            return false;
                    }
                }
            }
        }
        return true;
    }
}