package colector.co.com.collector;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.adapters.SurveyAdapter;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.fragments.SurveyAvailable;
import colector.co.com.collector.helpers.PreferencesManager;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.listeners.OnUploadSurvey;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.ImageRequest;
import colector.co.com.collector.model.ImageResponse;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.request.SendSurveyRequest;
import colector.co.com.collector.model.response.SendSurveyResponse;
import colector.co.com.collector.network.BusProvider;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;

public class MainActivity extends AppCompatActivity implements OnDataBaseSave, OnUploadSurvey {

    private FragmentTabHost mTabHost;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ArrayList<Survey> surveysDone = new ArrayList<>();
    private Bus mBus = BusProvider.getBus();
    private ProgressDialog progressDialog;
    private Survey surveyToUpload;
    private SurveyAdapter adapter;
    private ArrayList<IdValue> answersWithImages = new ArrayList<>();
    private int generalIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.sync_data));
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        setUpToolbar();
        buildTabs();
    }

    private void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("Colector");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mSyncronize:
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.sync_all_data)
                        .setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                uploadSurveyDone();
                            }
                        })
                        .setNegativeButton(getString(R.string.common_cancel), null)
                        .show();

                break;

            case R.id.logout:   DatabaseHelper databaseHelper = new DatabaseHelper();
                                databaseHelper.deleteDatabase();
                                PreferencesManager.getInstance().logoutAccount();
                                startActivity(new Intent(this, LoginActivity.class));
                                finish();
                                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void buildTabs() {
        mTabHost.addTab(
                mTabHost.newTabSpec(AppSettings.TAB_ID_AVAILABLE_SURVEY).setIndicator(getResources().getString(R.string.survey_surveys), null),
                SurveyAvailable.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(AppSettings.TAB_ID_DONE_SURVEY).setIndicator(getResources().getString(R.string.survey_survyes_done), null),
                SurveyAvailable.class, null);
    }

    @Override
    public void onUploadClicked(Survey survey, SurveyAdapter adapter) {
        this.adapter = adapter;
        uploadSurveyRemote(survey);
    }

    /**
     * Method subscribed to change on SendSurveyResponses
     * If the upload if ok and there are images to upload, upload the images
     *
     * @param response to watch change
     */
    @Subscribe
    public void onSuccessUploadSurvey(SendSurveyResponse response) {
        if (response.getResponseCode().equals(200l)) getImagesToUpload();
        else showToastError(response.getResponseDescription());
    }

    /**
     * Get the answer related with Images
     * If there is uploaded to the web service otherwise update local database
     */
    private void getImagesToUpload() {
        generalIndex = 0;
        answersWithImages = ImageRequest.getFileSurveys(surveyToUpload.getInstanceAnswers());
        if (!answersWithImages.isEmpty()) uploadImages();
        else uploadSurveySave();
    }

    /**
     * Upload Images with web service
     */
    private void uploadImages() {
        ImageRequest fileToUpload = new ImageRequest(surveyToUpload, answersWithImages.get(generalIndex), this);
        mBus.post(fileToUpload);
    }

    /**
     * Method subscribed to the ImageResponse change
     * If there are no more images update data base
     *
     * @param response to watch change
     */
    @Subscribe
    public void onImageUploaded(ImageResponse response) {
        if (response.getResponseCode().equals("200")) {
            generalIndex++;
            if (generalIndex >= answersWithImages.size()) {
                uploadSurveySave();
            } else uploadImages();
        } else showToastError(response.getResponseDescription());

    }

    /**
     * After upload the survey to remote update local database
     */
    private void uploadSurveySave() {
        DatabaseHelper.getInstance().updateRealmSurveySave(surveyToUpload.getInstanceId(), this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBus.unregister(this);
    }

    @Override
    public void onSuccess() {
        progressDialog.hide();
        if (adapter != null) {
            surveyToUpload.setUploaded(true);
            adapter.notifyDataSetChanged();
        }
        if (!surveysDone.isEmpty()) {
            uploadSurveyDone();
        }
    }

    @Override
    public void onError() {
        progressDialog.hide();
        //Notify Error
    }

    private void showToastError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void uploadSurveyDone() {
        surveysDone = DatabaseHelper.getInstance().getSurveysDone(
                new ArrayList<>(AppSession.getInstance().getSurveyAvailable()));
        if (!surveysDone.isEmpty()) uploadSurveyRemote(surveysDone.get(0));
        else {
            Toast.makeText(this, R.string.survey_save_send_ok, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void uploadSurveyRemote(Survey survey) {
        surveyToUpload = survey;
        progressDialog.show();
        SendSurveyRequest uploadSurvey = new SendSurveyRequest(survey);
        mBus.post(uploadSurvey);
    }

}