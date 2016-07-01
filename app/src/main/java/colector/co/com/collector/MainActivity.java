package colector.co.com.collector;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.adapters.SurveyAdapter;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.fragments.SurveyAvailable;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.ImageRequest;
import colector.co.com.collector.model.ImageResponse;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.request.SendSurveyRequest;
import colector.co.com.collector.model.response.SendSurveyResponse;
import colector.co.com.collector.network.BusProvider;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.syncService;

public class MainActivity extends AppCompatActivity implements OnDataBaseSave{

    private FragmentTabHost mTabHost;

    @BindView(R.id.fab_sync_surveys)
    FloatingActionButton FABSync;
    @BindView(R.id.fab_uploadall_urveys)
    FloatingActionButton FABuploadAll;
    @BindView(R.id.fab_eraseall_donesurveys)
    FloatingActionButton FABdeleteAll;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private ArrayList<Survey> toUnion;
    private int surveysToUpload = 0;
    private int surveysUploaded = 0;
    private Bus mBus = BusProvider.getBus();
    private ProgressDialog progressDialog;
    private Survey surveyToUpload;
    private SurveyAdapter adapter;
    private ArrayList<IdValue> answersWithImages = new ArrayList<>();
    private int generalIndex = 0;
    private int indexToLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.sync_data));

        FABSync.setVisibility(View.INVISIBLE);//ESTE
        FABuploadAll.setVisibility(View.INVISIBLE);
        FABdeleteAll.setVisibility(View.INVISIBLE);

        FABSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.survey_reload)
                        .setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getBaseContext(), "Actualizar Nuevos Forms.", Toast.LENGTH_LONG).show();
                            }

                        })
                        .setNegativeButton(getString(R.string.common_cancel), null)
                        .show();
            }
        });

        FABuploadAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog show = new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.survey_allupload)
                        .setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!isServiceRunning()) {
                                    Toast.makeText(getBaseContext(), "Tranquilo, el sistema enviara todos los formularios automaticamente", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getBaseContext(), syncService.class);
                                    intent.setFlags(AppSettings.SERVICE_FLAG_UPLOAD);
                                    startService(intent);
                                } else
                                    Toast.makeText(getBaseContext(), "El sistema esta trabajando. Por favor intentar en un momento.", Toast.LENGTH_LONG).show();

                            }
                        })
                        .setNegativeButton(getString(R.string.common_cancel), null)
                        .show();

            }
        });

        FABdeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.survey_alldelete)
                        .setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if (!isServiceRunning()) {
                                Toast.makeText(getBaseContext(), "Tranquilo, el sistema enviara todos los formularios automaticamente", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), syncService.class);
                                intent.setFlags(AppSettings.SERVICE_FLAG_DELETE);
                                startService(intent);
                                //}else
                                //  Toast.makeText(getBaseContext(), "El sistema esta trabajando. Por favor intentar en un momento.", Toast.LENGTH_LONG).show();

                            }

                        })
                        .setNegativeButton(getString(R.string.common_cancel), null)
                        .show();
            }
        });

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        setUpToolbar();
        buildTabs();
    }

    private boolean isServiceRunning() {
        /*try {
            ActivityManager manager = (ActivityManager) getBaseContext().getSystemService(getBaseContext().ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if ("colector.co.com.collector.utils.syncService".equals(service.service.getClassName())) {
                    return true;
                }
            }
        }catch(Exception e){
            Toast.makeText(getBaseContext(), "Service Err " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return false;*/
        return true;
    }

    private void setUpToolbar(){
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
        switch (item.getItemId()){
            case R.id.mSyncronize: AlertDialog show = new AlertDialog.Builder(MainActivity.this)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setMessage(R.string.sync_all_data)
                                    .setPositiveButton(getString(R.string.common_ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            toUnion = DatabaseHelper.getInstance().getSurveysDone(
                                                    new ArrayList<>(AppSession.getInstance().getSurveyAvailable()));
                                            surveysToUpload = toUnion.size();
                                            indexToLoad = 0;
                                            //onUploadClicked(toUnion.get(0));
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.common_cancel), null)
                                    .show();

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

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String selectedTab) {
                if (selectedTab.equalsIgnoreCase(AppSettings.TAB_ID_AVAILABLE_SURVEY)) {
                    //Nothing to do here.
                } else if (selectedTab.equalsIgnoreCase(AppSettings.TAB_ID_DONE_SURVEY)) {
                    //Nothing to do here.
                }
            }
        });
    }

    public void onUploadClicked(Survey survey) {
        surveyToUpload = survey;
        progressDialog.show();
        SendSurveyRequest uploadSurvey = new SendSurveyRequest(survey);
        mBus.post(uploadSurvey);
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
        else showSnackBarError(response.getResponseDescription());
    }

    /**
     * Get the answer related with Images
     * If there is uploaded to the web service otherwise update local database
     */
    private void getImagesToUpload() {
        generalIndex = 0;
        answersWithImages = ImageRequest.getFileSurveys(surveyToUpload.getInstanceAnswers());
        if (answersWithImages != null && !answersWithImages.isEmpty()) uploadImages();
        else {
            surveysUploaded = surveysUploaded + 1;
            uploadSurveySave();
        }
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
                surveysUploaded = surveysUploaded + 1;
                uploadSurveySave();
            }
            else uploadImages();
        } else showSnackBarError(response.getResponseDescription());

    }

    /**
     * After upload the survey to remote update local database
     */
    private void uploadSurveySave() {

        DatabaseHelper.getInstance().updateRealmSurveySave(surveyToUpload.getInstanceId(), this);
        this.surveyToUpload.setUploaded(true);

        if (surveysToUpload == surveysUploaded) {
/*            Snackbar snack = Snackbar.make(coordinatorLayout, getString(R.string.survey_save_send_ok), Snackbar.LENGTH_LONG);
            ((TextView) (snack.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(Color.WHITE);
            snack.show();*/
            progressDialog.hide();
        }
        else {
            indexToLoad++;
            onUploadClicked(toUnion.get(indexToLoad));
        }
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

    }

    @Override
    public void onError() {

    }

    private void showSnackBarError(String error) {
        /*Snackbar snack = Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_LONG);
        ((TextView) (snack.getView().findViewById(android.support.design.R.id.snackbar_text))).setTextColor(Color.WHITE);
        snack.show();*/
    }
}