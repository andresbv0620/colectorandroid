package co.colector;

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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import co.colector.adapters.CustomAlertAdapter;
import co.colector.database.DatabaseHelper;
import co.colector.fragments.DialogList;
import co.colector.helpers.PreferencesManager;
import co.colector.listeners.CallDialogListener;
import co.colector.listeners.OnAddFileListener;
import co.colector.listeners.OnAddPhotoListener;
import co.colector.listeners.OnAddSignatureListener;
import co.colector.listeners.OnDataBaseSave;
import co.colector.listeners.OnEditTextClickedOrFocused;
import co.colector.model.Autollenar;
import co.colector.model.IdOptionValue;
import co.colector.model.Question;
import co.colector.model.QuestionVisibilityRules;
import co.colector.model.ResponseComplex;
import co.colector.model.ResponseItem;
import co.colector.model.Section;
import co.colector.model.Survey;
import co.colector.model.SurveySave;
import co.colector.session.AppSession;
import co.colector.utils.GPSTracker;
import co.colector.utils.PathUtils;
import co.colector.views.EditTextDatePickerItemView;
import co.colector.views.EditTextItemView;
import co.colector.views.FileItemViewContainer;
import co.colector.views.MultipleItemViewContainer;
import co.colector.views.PhotoItemView;
import co.colector.views.PhotoItemViewContainer;
import co.colector.views.SectionItemView;
import co.colector.views.SignatureItemViewContainer;
import io.realm.RealmList;

public class SurveyActivity extends AppCompatActivity implements OnDataBaseSave, OnAddPhotoListener,
        CallDialogListener {

    private Survey surveys = AppSession.getInstance().getCurrentSurvey();
    private PhotoItemViewContainer activePhotoContainer;
    private FileItemViewContainer activeFileContainer;
    private SignatureItemViewContainer signatureContainer;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_TAKE_MAPSGPS = 2;
    private static final int REQUEST_TAKE_SIGNATURE = 3;
    private static final int REQUEST_PICKFILE_CODE = 4;
    private int selectedOption = 0;
    private boolean isGpsCanBeClicked;

    @BindView(R.id.survey_container)
    LinearLayout container;
    @BindView(R.id.loading)
    View loading;
    @BindView(R.id.coordinator)
    LinearLayout coordinatorLayout;
    Toolbar mToolbar;

    private Long timeStandIni;
    private ArrayList<EditTextItemView> elementsVisibility = new ArrayList<EditTextItemView>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private boolean isSectionOfFirstFieldStored = false;
    private View sectionItemViewSelected = null;
    private AlertDialog mAlertDialog;

    private RealmList<ResponseComplex> options;

    private ArrayList<String> copyArrayAdapter = new ArrayList<String>();
    private ArrayList<String> pivotAdapter = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);

        if (surveys.getInstanceId() == null)
            PreferencesManager.getInstance().setCoordinates("", "");
        else
            PreferencesManager.getInstance().setCoordinates(surveys.getInstanceLatitude(), surveys.getInstanceLongitude());

        fillLocalOptions();
        showLoading();
        setUpToolbar(surveys.getForm_name());
        setupGPS();
        configureInitTime();
        buildSurvey();
    }

    private void setupGPS() {
        //if (isGpsCanBeClicked) {
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
        //}
    }

    public ArrayList<String> getCopyArrayAdapter(Question question) {
        if (!copyArrayAdapter.isEmpty())
            return copyArrayAdapter;
        else {
            copyArrayAdapter = fillAdapter(question, new ArrayList<String>(), true);
            return copyArrayAdapter;
        }
    }

    public ArrayList<String> getPivotAdapter(Question question) {
        if (!pivotAdapter.isEmpty())
            return pivotAdapter;
        else {
            pivotAdapter = fillAdapter(question, new ArrayList<String>(), false);
            return pivotAdapter;
        }
    }

    private void setUpToolbar(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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
                            saveLocation();
                            saveSurvey();
                        } else {
                            if (isSectionOfFirstFieldStored) {
                                sectionItemViewSelected.requestFocus();
                            }
                            showSnackNotification();
                        }

                    }
                })
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    private void saveLocation() {
        if (PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LATITUDE_SURVEY, "").isEmpty() &&
                PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LONGITUDE_SURVEY, "").isEmpty()) {
            GPSTracker gps = new GPSTracker(SurveyActivity.this);
            if (gps.canGetLocation()) {
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                PreferencesManager.getInstance().setCoordinates(String.valueOf(latitude), String.valueOf(longitude));
            }
        }
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

    private void validateVisibilityRules(String value, Long idParentRule, SectionItemView sectionItemView) {
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                if (sectionItem.equals(sectionItemView)) {
                    ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                    for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                        if (sectionItemContainer.getChildAt(sectionItemIndex) instanceof EditTextItemView) {
                            EditTextItemView element = (EditTextItemView) sectionItemContainer.getChildAt(sectionItemIndex);
                            if (element.getVisibilityRules() != null && !element.getVisibilityRules().isEmpty()) {
                                QuestionVisibilityRules questionVisibilityRules = element.getVisibilityRules().first();
                                if (questionVisibilityRules.getElemento().equals(idParentRule)) {
                                    if (questionVisibilityRules.getValor().toUpperCase().equals(value.toUpperCase())) {
                                        element.setVisibilityLabel(true);
                                    } else {
                                        element.setVisibilityLabel(false);
                                    }
                                }
                            }
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

                    if (!fieldSectionValid && !isSectionOfFirstFieldStored) {
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
            buildSection(section, sectionItem.sectionItemsContainer, sectionItem);
            container.addView(sectionItem);
        }
        hideLoading();
    }

    private void buildSection(Section section, LinearLayout linear, SectionItemView sectionItemView) {
        for (Question question : section.getInputs()) {
            buildQuestion(question, linear, sectionItemView);
        }
    }


    private void buildQuestion(final Question question, LinearLayout linear, SectionItemView sectionItemView) {
        switch (question.getType()) {
            // Input Text
            case 1:
            case 2:
            case 8:
            default:
                EditTextItemView editItemView = new EditTextItemView(this, sectionItemView);
                editItemView.bind(question, surveys.getAnswer(question.getId()));
                linear.addView(editItemView);
                break;
            // Option Spinner
            case 3:
            case 4:
                EditTextItemView editTextItemView = new EditTextItemView(this, sectionItemView);
                editTextItemView.bind(question, question.getResponses(), surveys.getAnswer(question.getId()));
                linear.addView(editTextItemView);
                break;
            //Multiple opcion
            case 5:
                MultipleItemViewContainer multipleItemViewContainer = new MultipleItemViewContainer(this, sectionItemView);
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
            case 10: EditTextItemView editTextDynamicView = new EditTextItemView(this, sectionItemView);
                     editTextDynamicView.bind(question);
                     linear.addView(editTextDynamicView);
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
                        PreferencesManager.getInstance().storeOptionsSelecteds("");
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void callDialog(String title, List<IdOptionValue> response, final Object parent, int type, final SectionItemView sectionItemView) {
        DialogList dialog = DialogList.newInstance(SurveyActivity.this, title,
                new ArrayList<>(response), type);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);
        if (parent instanceof EditTextItemView) {
            final TextInputEditText input = ((EditTextItemView) parent).getLabel();
            dialog.setListDialogListener(new DialogList.ListSelectorDialogListener() {
                @Override
                public void setItemSelected(String item) {
                    input.setText(item);
                    EditTextItemView parentRule = ((EditTextItemView) parent);
                    validateVisibilityRules(item, parentRule.getIdentifier(), sectionItemView);
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

    @Override
    public void callDynamicDialog(String title, final Question question, final Object parent) {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(SurveyActivity.this);
        myDialog.setTitle(title);

        options = question.getOptions();

        final ArrayList<String> arrayAdapter = new ArrayList<String>();
        copyArrayAdapter = getCopyArrayAdapter(question);
        pivotAdapter = getPivotAdapter(question);

        View toplist = getLayoutInflater().inflate(R.layout.listdialog, null);
        SearchView searchBar = (SearchView) toplist.findViewById(R.id.search_bar);
        final ListView listitem = (ListView) toplist.findViewById(R.id.list_item_dialog);
        listitem.setAdapter(null);

        searchBar.setQueryHint(getString(R.string.buscar));
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.clear();
                for (int i = 0; i < copyArrayAdapter.size(); i++) {
                    if(copyArrayAdapter.get(i).toLowerCase().contains(newText.toLowerCase().trim())) {
                        arrayAdapter.add(copyArrayAdapter.get(i));
                    }
                }
                listitem.setAdapter(new CustomAlertAdapter(SurveyActivity.this, arrayAdapter));
                return false;
            }
        });

        listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> inputParent, View view, int position, long id) {
                final TextInputEditText input = ((EditTextItemView) parent).getLabel();
                input.setText(arrayAdapter.get(position));
                selectedOption = findPositionInOptions(arrayAdapter.get(position), copyArrayAdapter);
                setLocationForMaps(selectedOption, pivotAdapter);
                evaluateAnswers(question, selectedOption);
                mAlertDialog.dismiss();
            }
        });

        myDialog.setView(toplist);
        mAlertDialog = myDialog.show();
    }

    private void setLocationForMaps(int selectedOption, ArrayList<String> pivotAdapter){
        String[] selection = pivotAdapter.get(selectedOption).split(";");
        try {
            PreferencesManager.getInstance().setCoordinates(selection[5], selection[4]);
        } catch (ArrayIndexOutOfBoundsException e){

        }
    }

    private int findPositionInOptions(String value, ArrayList<String> copyArrayAdapter){
        int counter = 0;
        for (String s: copyArrayAdapter){
            if (s.equals(value)){
                return counter;
            }
            counter++;
        }
        return 0;
    }

    private void evaluateAnswers(Question question, int which){
        RealmList<ResponseItem> responseItems = options.get(which).getResponses();
        RealmList<Autollenar> autollenarRealmList = question.getAsociate_form().get(0).getAutollenar();
        for (Autollenar autollenar: autollenarRealmList){
            for (ResponseItem responseItem: responseItems){
                if (String.valueOf(responseItem.getInput_id()).equals(String.valueOf(autollenar.getEntrada_fuente()))){
                    autoFillValues(autollenar.getEntrada_destino(), responseItem.getValue());
                    break;
                }
            }
        }
    }

    private void autoFillValues(Long inputId, String value){
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                    ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                    for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                        if (sectionItemContainer.getChildAt(sectionItemIndex) instanceof EditTextItemView) {
                            EditTextItemView element = (EditTextItemView) sectionItemContainer.getChildAt(sectionItemIndex);
                            if (element.getQuestion().getId().equals(inputId)){
                                element.setValue(value);
                            }
                        }
                    }
            }
        }
    }

    private void fillLocalOptions(){
        options = new RealmList<ResponseComplex>();
    }

    private ArrayList<String> fillAdapter(Question question, ArrayList<String> adapter, boolean needCorrectValue){
        for (ResponseComplex responseComplex: options){
            RealmList<ResponseItem> responseItems = responseComplex.getResponses();
            String tag = "";
            for (ResponseItem responseItem: responseItems)
                tag = tag.isEmpty() ? responseItem.getValue() : tag +"; "+responseItem.getValue();
            if (needCorrectValue)
                adapter.add(getCorrectValue(tag));
            else
                adapter.add(tag);
        }

        return adapter;
    }

    private String getCorrectValue(String tagValue){
        String[] tagValueArray = tagValue.split(";");
        if (tagValueArray.length == 7){
            return tagValueArray[2]+tagValueArray[3];
        }
        else if (tagValueArray.length == 6) {
            return tagValueArray[2];
        }
        return tagValue;
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
        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText edittext = new EditText(this);
                edittext.setSingleLine(true);
                edittext.setMaxLines(1);
                alert.setMessage(getString(R.string.informacion_photo_body));
                alert.setTitle(getString(R.string.informacion_title));
                alert.setView(edittext);
                alert.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String result = edittext.getText().toString();
                        if (!result.isEmpty()) {
                            try {
                                final File photoFile = createImageFile(result);
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                            Uri.fromFile(photoFile));
                                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                                }
                            } catch (IOException e) {

                            }
                        } else {
                            Toast.makeText(SurveyActivity.this, getString(R.string.must_be_select_name), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                alert.setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
        }
    }

    /**
     * Create file to take a picture
     *
     * @return New File address
     * @param photo Photo name
     * @throws IOException
     */
    private File createImageFile(String photo) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = photo + "_JPEG_" + timeStamp + "_";
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
        boolean isFromEdit = false;
        if (surveys.getInstanceId() == null)
            surveySave.setId(DatabaseHelper.getInstance().getNewSurveyIndex());
        else {
            isFromEdit = true;
            surveySave.setId(surveys.getInstanceId());
        }

        try {
            surveySave.setLatitude(PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LATITUDE_SURVEY, ""));
        } catch (NullPointerException e) {
            surveySave.setLatitude(String.valueOf(0.0f));
        }
        try {
            surveySave.setLongitude(PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.LONGITUDE_SURVEY, ""));
        } catch (NullPointerException e) {
            surveySave.setLongitude(String.valueOf(0.0f));
        }

        surveySave.setHoraIni(String.valueOf(timeStandIni));
        surveySave.setHoraFin(String.valueOf(System.currentTimeMillis() / 1000));
        surveySave.setTitulo_reporte(surveys.getTitulo_reporte());
        surveySave.setTitulo_reporte2(surveys.getTitulo_reporte2());
        // Difficult Task
        for (int child = 0; child < container.getChildCount(); child++) {
            View sectionItem = container.getChildAt(child);
            if (sectionItem instanceof SectionItemView) {
                ViewGroup sectionItemContainer = ((SectionItemView) sectionItem).sectionItemsContainer;
                for (int sectionItemIndex = 0; sectionItemIndex < sectionItemContainer.getChildCount(); sectionItemIndex++) {
                    View sectionView = sectionItemContainer.getChildAt(sectionItemIndex);

                    if (sectionView instanceof EditTextItemView) {
                        if (((EditTextItemView) sectionView).getType() != 10) {
                            surveySave.getResponses().add(((EditTextItemView) sectionView).getResponse());
                        }
                        else {
                            surveySave.getResponses().add(((EditTextItemView) sectionView).getResponse(options,selectedOption));
                        }
                    }
                    else if (sectionView instanceof MultipleItemViewContainer)
                        surveySave.getResponses().add(((MultipleItemViewContainer)
                                sectionView).getResponses());
                    else if (sectionView instanceof PhotoItemViewContainer)
                        surveySave.getResponses().add(((PhotoItemViewContainer)
                                sectionView).getResponses());
                    else if (sectionView instanceof EditTextDatePickerItemView)
                        surveySave.getResponses().add(((EditTextDatePickerItemView)
                                sectionView).getResponse());
                    else if (sectionView instanceof SignatureItemViewContainer)
                        surveySave.getResponses().add(((SignatureItemViewContainer)
                                sectionView).getResponse());
                    else if (sectionView instanceof FileItemViewContainer)
                        surveySave.getResponses().add(((FileItemViewContainer)
                                sectionView).getResponses());
                }
            }
        }
        // Save on DataBase
        PreferencesManager.getInstance().storeOptionsSelecteds("");
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
        alert.setNeutralButton(getString(R.string.ver_photo), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 Intent intent = new Intent(SurveyActivity.this, SeeImageActivity.class);
                 intent.putExtra("uriImage",view.url);
                 startActivity(intent);
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
