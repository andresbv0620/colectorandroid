package colector.co.com.collector;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.fragments.DialogList;
import colector.co.com.collector.helpers.PreferencesManager;
import colector.co.com.collector.listeners.CallDialogListener;
import colector.co.com.collector.listeners.OnAddFileListener;
import colector.co.com.collector.listeners.OnAddPhotoListener;
import colector.co.com.collector.listeners.OnAddSignatureListener;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.listeners.OnEditTextClickedOrFocused;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;
import colector.co.com.collector.model.QuestionVisibilityRules;
import colector.co.com.collector.model.Section;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.SurveySave;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.utils.FindGPSLocation;
import colector.co.com.collector.utils.PathUtils;
import colector.co.com.collector.views.EditTextDatePickerItemView;
import colector.co.com.collector.views.EditTextItemView;
import colector.co.com.collector.views.FileItemViewContainer;
import colector.co.com.collector.views.MultipleItemViewContainer;
import colector.co.com.collector.views.PhotoItemView;
import colector.co.com.collector.views.PhotoItemViewContainer;
import colector.co.com.collector.views.SectionItemView;
import colector.co.com.collector.views.SignatureItemViewContainer;

public class SurveyActivity extends AppCompatActivity implements OnDataBaseSave, OnAddPhotoListener,
        CallDialogListener {
    private FindGPSLocation gps;

    private Survey surveys = AppSession.getInstance().getCurrentSurvey();
    private PhotoItemViewContainer activePhotoContainer;
    private FileItemViewContainer activeFileContainer;
    private SignatureItemViewContainer signatureContainer;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_TAKE_MAPSGPS = 2;
    private static final int REQUEST_TAKE_SIGNATURE = 3;
    private static final int REQUEST_PICKFILE_CODE = 4;
    private boolean isGpsCanBeClicked;

    @BindView(R.id.survey_container)
    LinearLayout container;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.coordinator)
    LinearLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private Long timeStandIni;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private boolean isSectionOfFirstFieldStored = false;
    private View sectionItemViewSelected = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);
        showLoading();
        setUpToolbar(surveys.getForm_name());
        setupGPS();
        configureInitTime();
        buildSurvey();
        PreferencesManager.getInstance().resetCoordinates();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setupGPS() {
        if (isGpsCanBeClicked) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.gps_alert))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void setUpToolbar(String title) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_survey, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.mUpload:
                dismissKeyBoard();
                threadSaveModulo();
                break;

            case R.id.mGps:
                if (isGpsCanBeClicked) {
                    mapGPSIntent();
                } else {
                    Snackbar snack = Snackbar.make(coordinatorLayout, R.string.opcion_no_disponible, Snackbar.LENGTH_LONG);
                    ((TextView) (snack.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(Color.WHITE);
                    snack.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
                            if (isSectionOfFirstFieldStored){
                                sectionItemViewSelected.requestFocus();
                            }
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

    private void validateVisibilityRules(String value){
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    if (sectionItemContainer.getChildAt(sectionItemIndex) instanceof EditTextItemView){
                        EditTextItemView element = (EditTextItemView) sectionItemContainer.getChildAt(sectionItemIndex);
                        if (element.getVisibilityRules() != null && !element.getVisibilityRules().isEmpty()){
                            QuestionVisibilityRules questionVisibilityRules = element.getVisibilityRules().first();
                            if (questionVisibilityRules.getValor().toUpperCase().equals(value.toUpperCase()))
                                element.setVisibilityLabel();
                        }
                    }
                }
            }
        }
    }

    private boolean validateFields() {
        boolean fieldsValid = true;
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    boolean fieldSectionValid = validateFieldsOnView(sectionItemContainer
                            .getChildAt(sectionItemIndex));

                    fieldsValid = fieldsValid && fieldSectionValid;

                    if (!fieldSectionValid && !isSectionOfFirstFieldStored){
                        sectionItemViewSelected = sectionItemContainer
                                .getChildAt(sectionItemIndex);
                        isSectionOfFirstFieldStored = true;
                    }
                }

            }
        }
        return fieldsValid;
    }

    private boolean validateFieldsOnView(View sectionView) {
        if (sectionView instanceof EditTextItemView)
            return ((EditTextItemView) sectionView).validateField();
        if (sectionView instanceof MultipleItemViewContainer)
            return ((MultipleItemViewContainer) sectionView).validateFields();
        if (sectionView instanceof PhotoItemViewContainer)
            return ((PhotoItemViewContainer) sectionView).validateFields();
        if (sectionView instanceof EditTextDatePickerItemView)
            return ((EditTextDatePickerItemView) sectionView).validateField();
        if (sectionView instanceof SignatureItemViewContainer)
            return ((SignatureItemViewContainer) sectionView).validateField();
        if (sectionView instanceof FileItemViewContainer)
            return ((FileItemViewContainer) sectionView).validateFields();
        return true;
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
            buildQuestion(question, linear);
        }
    }



    private void buildQuestion(final Question question, LinearLayout linear) {
        switch (question.getType()) {
            // Input Text
            case 1:
            case 2:
            case 8:
            default:
                EditTextItemView editItemView = new EditTextItemView(this);
                editItemView.bind(question, surveys.getAnswer(question.getId()));
                linear.addView(editItemView);
                break;
            // Option Spinner
            case 3:
            case 4:
                EditTextItemView editTextItemView = new EditTextItemView(this);
                editTextItemView.bind(question, question.getResponses(), surveys.getAnswer(question.getId()));
                linear.addView(editTextItemView);
                break;
            //Multiple opcion
            case 5:
                MultipleItemViewContainer multipleItemViewContainer = new MultipleItemViewContainer(this);
                multipleItemViewContainer.bind(new ArrayList<>(question.getResponses()), question,
                        surveys.getListAnswers(question.getId()));
                linear.addView(multipleItemViewContainer);
                break;
            // picture
            case 6:
                PhotoItemViewContainer photoItemViewContainer = new PhotoItemViewContainer(this);
                photoItemViewContainer.bind(question, this, surveys.getListAnswers(question.getId()));
                linear.addView(photoItemViewContainer);
                break;
            // date
            case 7:
                EditTextDatePickerItemView editTextDatePickerItemView = new EditTextDatePickerItemView(this);
                editTextDatePickerItemView.bind(question, surveys.getAnswer(question.getId()), new OnEditTextClickedOrFocused() {
                    @Override
                    public void onEditTextAction(EditTextDatePickerItemView view) {
                        DialogFragment newFragment = new TimePickerFragment(view.getLabel());
                        newFragment.show(SurveyActivity.this.getFragmentManager(), "timePicker");
                    }
                });
                linear.addView(editTextDatePickerItemView);
                break;

            // dynamic form
            case 10:
                break;

            // GPS
            case 12:
                isGpsCanBeClicked = true;
                break;

            // signature
            case 14:
                SignatureItemViewContainer signatureItemViewContainer = new SignatureItemViewContainer(this);
                signatureItemViewContainer.bind(question, surveys.getAnswer(question.getId()), new OnAddSignatureListener() {
                    @Override
                    public void onAddSignatureClicked(SignatureItemViewContainer container) {
                        signatureContainer = container;
                        AppSession.getInstance().setCurrentPhotoID(question.getId());
                        dispatchTakeSignatureIntent(question.getId());
                    }
                });
                linear.addView(signatureItemViewContainer);
                break;
            // File
            case 16:
                FileItemViewContainer fileItemViewContainer = new FileItemViewContainer(this);
                fileItemViewContainer.bind(question, new OnAddFileListener() {
                    @Override
                    public void onAddFileClicked(FileItemViewContainer container) {
                        activeFileContainer = container;
                        AppSession.getInstance().setCurrentPhotoID(container.id);
                        launchGetFileIntent();
                    }

                    @Override
                    public void onFileClicked(LinearLayout container, PhotoItemView view) {
                        showAlertDialog(container, view);
                    }
                }, surveys.getListAnswers(question.getId()));
                linear.addView(fileItemViewContainer);
                break;

        }
    }

    private void launchGetFileIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_PICKFILE_CODE);
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
    private void exitFormAlert() {
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

    @Override
    public void callDialog(String title, List<IdOptionValue> response, final Object parent, int type) {
        DialogList dialog = DialogList.newInstance(SurveyActivity.this, title,
                new ArrayList<>(response), type);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        if (parent instanceof EditTextItemView) {
            final TextInputEditText input = ((EditTextItemView) parent).getLabel();
            dialog.setListDialogListener(new DialogList.ListSelectorDialogListener() {
                @Override
                public void setItemSelected(String item) {
                    input.setText(item);
                    validateVisibilityRules(item);
                    if (!item.isEmpty())
                        ((EditTextItemView) parent).setIsShow();
                }
            });
        } else if (parent instanceof MultipleItemViewContainer) {
//            final LinearLayout container = ((MultipleItemViewContainer) parent).getContainer();
            final MultipleItemViewContainer view = ((MultipleItemViewContainer) parent);
            dialog.setListener_multiple(new DialogList.ListMultipleSelectorListener() {
                @Override
                public void setItemsSelected(List<String> items, Question question) {
                    view.fillData(items);
                    //view.setEnabled(false);
                }
            });
        }
        dialog.show(getFragmentManager(), title);
    }

    @SuppressLint("ValidFragment")
    private static class TimePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditText toPrint;

        private TimePickerFragment(EditText toPrint) {
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

    private void mapGPSIntent() {
        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dispatchTakeSignatureIntent(Long id) {
        Intent intentSignature = new Intent(getApplicationContext(), SignatureActivity.class);
        Bundle extras = getIntent().putExtra("idImageView", id).getExtras();
        startActivityForResult(intentSignature, REQUEST_TAKE_SIGNATURE, extras);
    }


    /**
     * Event to button in question type picture
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void dispatchTakePictureIntent() {
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
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Create file to take a picture
     *
     * @return New File address
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    // Photo Request
                    try {
                        activePhotoContainer.addImages(AppSession.getInstance().getCurrentPhotoPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_TAKE_MAPSGPS:
                    break;
                case REQUEST_TAKE_SIGNATURE:
                    // Signature Request
                    try {
                        signatureContainer.addSignature(AppSession.getInstance().getCurrentPhotoPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REQUEST_PICKFILE_CODE:
                    // Pick File Request
                    try {
                        String uri = PathUtils.getPath(this, data.getData());
                        if (activeFileContainer.getExtension(uri) != FileItemViewContainer.ERROR_PATH)
                            activeFileContainer.addImagesFile(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }

        }
    }

    private void saveSurvey() {
        SurveySave surveySave = new SurveySave();
        surveySave.setInstanceId(surveys.getForm_id());
        if (surveys.getInstanceId() == null)
            surveySave.setId(DatabaseHelper.getInstance().getNewSurveyIndex());
        else
            surveySave.setId(surveys.getInstanceId());
        try {
            surveySave.setLatitude(PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LATITUDE_SURVEY,""));
        }catch (NullPointerException e){
            surveySave.setLatitude(String.valueOf(0.0f));
        }
        try {
            surveySave.setLongitude(PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LONGITUDE_SURVEY,""));
        }catch (NullPointerException e){
            surveySave.setLongitude(String.valueOf(0.0f));
        }
        surveySave.setHoraIni(String.valueOf(timeStandIni));
        surveySave.setHoraFin(String.valueOf(System.currentTimeMillis() / 1000));
        // Difficult Task
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    View sectionView = sectionItemContainer.getChildAt(sectionItemIndex);

                    if (sectionView instanceof EditTextItemView)
                        surveySave.getResponses().add(((EditTextItemView) sectionView).getResponse());
                    else if (sectionView instanceof MultipleItemViewContainer)
                        surveySave.getResponses().addAll(((MultipleItemViewContainer)
                                sectionView).getResponses());
                    else if (sectionView instanceof PhotoItemViewContainer)
                        surveySave.getResponses().addAll(((PhotoItemViewContainer)
                                sectionView).getResponses());
                    else if (sectionView instanceof EditTextDatePickerItemView)
                        surveySave.getResponses().add(((EditTextDatePickerItemView)
                                sectionView).getResponse());
                    else if (sectionView instanceof SignatureItemViewContainer)
                        surveySave.getResponses().add(((SignatureItemViewContainer)
                                sectionView).getResponse());
                    else if (sectionView instanceof FileItemViewContainer)
                        surveySave.getResponses().addAll(((FileItemViewContainer)
                                sectionView).getResponses());
                }
            }
        }
        // Save on DataBase
        DatabaseHelper.getInstance().addSurvey(surveySave, this);
    }


    @Override
    public void onAddPhotoClicked(PhotoItemViewContainer container) {
        activePhotoContainer = container;
        AppSession.getInstance().setCurrentPhotoID(container.id);
        dispatchTakePictureIntent();
    }

    @Override
    public void onPhotoClicked(LinearLayout container, PhotoItemView view) {
        showAlertDialog(container, view);
    }

    /**
     * Show Alert diaglog to confirm if the picture will be remove
     *
     * @param container of the image
     * @param view      image to be remove
     */
    private void showAlertDialog(final LinearLayout container, final PhotoItemView view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.survey_delete_dialog_title));
        alert.setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.setPositiveButton(getString(R.string.common_erase), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                container.removeView(view);
            }
        });
        alert.show();
    }

    @Override
    public void onSuccess() {
        hideLoading();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
