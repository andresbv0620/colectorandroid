package colector.co.com.collector;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.http.AsyncResponse;
import colector.co.com.collector.http.BackgroundTask;
import colector.co.com.collector.http.ResourceNetwork;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.response.ErrorResponse;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import colector.co.com.collector.persistence.dao.SurveyDAO;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.settings.AppSettings;
import colector.co.com.collector.utils.Utilities;
import io.fabric.sdk.android.Fabric;

import static colector.co.com.collector.settings.AppSettings.HTTP_OK;

public class LoginActivity extends AppCompatActivity implements OnDataBaseSave {

    private EditText etUsername;
    private EditText etPassword;
    private String UUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        UUID = Utilities.getUUID(this);

        AppSettings.URL_BASE = ResourceNetwork.URL_BASE_PROD;
        etUsername = (EditText) findViewById(R.id.editTextEmail);
        etPassword = (EditText) findViewById(R.id.editTextPassword);
        etUsername.setText("");
        etPassword.setText("");
        ((TextView) findViewById(R.id.login_uuid)).setText(UUID);
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
        if (etUsername.getText().toString().trim().equals("") && etPassword.getText().toString().trim().equals("test")) {
            if (AppSettings.URL_BASE.equalsIgnoreCase(ResourceNetwork.URL_BASE_DESA)) {
                Toast.makeText(this, "Modo Produccion", Toast.LENGTH_LONG).show();
                AppSettings.URL_BASE = ResourceNetwork.URL_BASE_PROD;
            } else {
                AppSettings.URL_BASE = ResourceNetwork.URL_BASE_DESA;
                Toast.makeText(this, "Modo desarrollo", Toast.LENGTH_LONG).show();
            }
        } else if (etUsername.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.login_error_empty), Toast.LENGTH_LONG).show();
        } else if (!Utilities.isNetworkConnected(this)) {
            //INIDICAR SI PUEDE TRABAJARA EN MODO OFFFLINE Y SI QUIERE CONTINUAR
            Toast.makeText(this, getString(R.string.common_internet_not_available), Toast.LENGTH_LONG).show();

            //VALIDAR TRABAJO OFFLINE Y CONTINUAR
            List<Survey> offlineSurvey = new SurveyDAO(this).getSurveyAvailable();

            //VALIDAR SI USUARIO, CONTRASEÑA, TABLET ES VALIDA Y SET TOKEN JUNTO CON COD_ID AL PROYECTO.
            boolean usuarioValido = false;

            if (usuarioValido) {
                if (offlineSurvey != null && offlineSurvey.size() > 0) {
                    offlineWorkVal(offlineSurvey);
                } else {
                    offlineWorkDen();
                }
            } else
                Toast.makeText(this, getString(R.string.login_unknown_user_dao), Toast.LENGTH_LONG).show();


        } else {
            LoginRequest toSend = new LoginRequest(etUsername.getText().toString(), etPassword.getText().toString());
            toSend.setTabletId(UUID);

            AsyncResponse callback = new AsyncResponse() {
                @Override
                public void callback(Object output, String Sended) {

                    if (output instanceof LoginResponse) {
                        LoginResponse response = (LoginResponse) output;

                        if (response.getResponseCode().equals(HTTP_OK)) {
                            if (response.getResponseData().get(0) != null) {
                                AppSession.getInstance().setUser(response.getResponseData().get(0));
                                // Invoke the survey synchronize
                                getSurveys();
                            } else if (output instanceof ErrorResponse) {
                                Toast.makeText(LoginActivity.this, ((ErrorResponse) output).getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.survey_save_send_error), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, response.getResponseDescription(), Toast.LENGTH_LONG).show();
                        }
                    } else if (output instanceof ErrorResponse) {
                        ErrorResponse response = (ErrorResponse) output;
                        Toast.makeText(LoginActivity.this, response.getMessage() + "VER TOKEN", Toast.LENGTH_LONG).show();
                    }
                }
            };

            BackgroundTask bt = new BackgroundTask(this, toSend, new LoginResponse(), callback, null, false);
            bt.execute(AppSettings.URL_BASE + ResourceNetwork.URL_LOGIN_DEF);

        }

    }

    private void getSurveys() {
        GetSurveysRequest toSend = new GetSurveysRequest(AppSession.getInstance().getUser().getColector_id());
        AsyncResponse callback = new AsyncResponse() {
            @Override
            public void callback(Object output, String Sended) {

                if (output instanceof GetSurveysResponse) {

                    GetSurveysResponse response = (GetSurveysResponse) output;

                    if (response.getResponseCode().equals(HTTP_OK)) {
                        // to be remove
                        SurveyDAO dao = new SurveyDAO(LoginActivity.this);
                        dao.synchronizeSurveys(response.getResponseData());

                        AppSession.getInstance().setSurveyAvailable(response.getResponseData());
                        DatabaseHelper.getInstance().addSurveyAvailable(response.getResponseData(), LoginActivity.this); //Save on Realm

                    } else {
                        Toast.makeText(LoginActivity.this, response.getResponseDescription(), Toast.LENGTH_LONG).show();
                    }


                } else if (output instanceof ErrorResponse) {
                    ErrorResponse response = (ErrorResponse) output;
                    Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        };

        BackgroundTask bt = new BackgroundTask(this, toSend, new GetSurveysResponse(), callback, null, false);
        bt.execute(AppSettings.URL_BASE + ResourceNetwork.URL_SURVEY_DEF);
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError() {
        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_LONG).show();
    }
}