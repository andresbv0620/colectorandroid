package colector.co.com.collector;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.helpers.PreferencesManager;
import colector.co.com.collector.http.ResourceNetwork;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import colector.co.com.collector.network.BusProvider;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.Utilities;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity implements OnDataBaseSave {

    @BindView(R.id.editTextEmail)
    EditText etUsername;
    @BindView(R.id.editTextPassword)
    EditText etPassword;
    @BindView(R.id.login_uuid)
    TextView textViewLoginUiid;
    private String UUID;
    private Bus mBus = BusProvider.getBus();
    private ProgressDialog progressDialogLogin;

    private static final int PERMISSION_EXTERNAL_STORAGE = 0;
    private static final int PERMISSION_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        if (!PreferencesManager.getInstance().isActiveAccount()) {
            ButterKnife.bind(this);
            UUID = Utilities.getUUID(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                checkPermissions();

            AppSettings.URL_BASE = ResourceNetwork.URL_BASE_PROD;
            etUsername.setText("");
            etPassword.setText("");
            textViewLoginUiid.setText(UUID);
            progressDialogLogin = new ProgressDialog(this);
            progressDialogLogin.setCancelable(false);
            progressDialogLogin.setMessage(getString(R.string.generalProgressDialogMessageLogin));
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
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

    public void offlineWorkVal(final List<Survey> offlineSurvey) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.login_offline_message)
                .setPositiveButton(getString(R.string.login_offline_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppSession.getInstance().setSurveyAvailable(offlineSurvey);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    public void offlineWorkDen() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.login_offline_message_den)
                .setNegativeButton(getString(R.string.common_cancel), null)
                .show();
    }

    public void doLogin(View view) {
        if (!etUsername.getText().toString().isEmpty() && !etPassword.getText().toString().isEmpty()) {
            LoginRequest toSend = new LoginRequest(etUsername.getText().toString(), etPassword.getText().toString());
            toSend.setTabletId(UUID);
            progressDialogLogin.show();
            mBus.post(toSend);
        } else {
            Toast.makeText(this, getString(R.string.login_error_empty), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess() {
        progressDialogLogin.dismiss();
        PreferencesManager.getInstance().setActiveAccount();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        progressDialogLogin.dismiss();
        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onSuccessLoginResponse(LoginResponse response){
        if (!response.getResponseData().isEmpty()){
                PreferencesManager.getInstance().storeResponseData(response.getResponseData().get(0));
                AppSession.getInstance().setUser(response.getResponseData().get(0));
                // Invoke the survey synchronize
                GetSurveysRequest toSend = new GetSurveysRequest(AppSession.getInstance().getUser()
                        .getColector_id());
                mBus.post(toSend);
        } else {
            progressDialogLogin.dismiss();
            Toast.makeText(this, response.getResponseDescription(), Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onSuccessSurveysResponse(GetSurveysResponse response){
        AppSession.getInstance().setSurveyAvailable(response.getResponseData());
        DatabaseHelper.getInstance().addSurveyAvailable(response.getResponseData(),
                LoginActivity.this); //Save on Realm
    }

    private void checkPermissions(){
        if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PERMISSION_EXTERNAL_STORAGE);
    }

    private boolean checkPermission(String permission){
        int result = ContextCompat.checkSelfPermission(this, permission);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission, int request_code){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)){
            Toast.makeText(this,getString(R.string.permission_gps_notice),Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, request_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,String.format(getString(R.string.permission_granted),
                            getString(R.string.storage)), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this,String.format(getString(R.string.permission_not_granted),
                            getString(R.string.storage)), Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this,String.format(getString(R.string.permission_granted),
                            getString(R.string.camara)), Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(this, String.format(getString(R.string.permission_not_granted),
                            getString(R.string.camara)), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}