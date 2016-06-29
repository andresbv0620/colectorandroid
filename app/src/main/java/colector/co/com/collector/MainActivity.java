package colector.co.com.collector;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.fragments.SurveyAvailable;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.syncService;

public class MainActivity extends FragmentActivity {
    private FragmentTabHost mTabHost;

    @BindView(R.id.fab_sync_surveys)
    FloatingActionButton FABSync;
    @BindView(R.id.fab_uploadall_urveys)
    FloatingActionButton FABuploadAll;
    @BindView(R.id.fab_eraseall_donesurveys)
    FloatingActionButton FABdeleteAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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


    private void buildTabs() {
        mTabHost.addTab(
                mTabHost.newTabSpec(AppSettings.TAB_ID_AVAILABLE_SURVEY).setIndicator(getResources().getString(R.string.survey_surveys), null),
                SurveyAvailable.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(AppSettings.TAB_ID_DONE_SURVEY).setIndicator(getResources().getString(R.string.survey_survyes_done), null),
                SurveyAvailable.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec(AppSettings.TAB_ID_UPLOADED_SURVEY).setIndicator(getResources().getString(R.string.survey_survyes_uploaded), null),
                SurveyAvailable.class, null);

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String selectedTab) {
                if (selectedTab.equalsIgnoreCase(AppSettings.TAB_ID_AVAILABLE_SURVEY)) {
                    FABSync.setVisibility(View.INVISIBLE);//este
                    FABuploadAll.setVisibility(View.INVISIBLE);
                    FABdeleteAll.setVisibility(View.INVISIBLE);
                } else if (selectedTab.equalsIgnoreCase(AppSettings.TAB_ID_DONE_SURVEY)) {
                    FABSync.setVisibility(View.INVISIBLE);
                    FABuploadAll.setVisibility(View.VISIBLE);//ESTE
                    FABuploadAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_upload));
                    FABdeleteAll.setVisibility(View.INVISIBLE);
                } else if (selectedTab.equalsIgnoreCase(AppSettings.TAB_ID_UPLOADED_SURVEY)) {
                    FABSync.setVisibility(View.INVISIBLE);
                    FABuploadAll.setVisibility(View.INVISIBLE);
                    FABuploadAll.setImageDrawable(getResources().getDrawable(R.drawable.ic_reupload));
                    FABdeleteAll.setVisibility(View.VISIBLE);//SETE
                }
            }
        });
    }
}
