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
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.database.DatabaseHelper;
import colector.co.com.collector.http.ResourceNetwork;
import colector.co.com.collector.listeners.OnDataBaseSave;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import colector.co.com.collector.network.BusProvider;
import colector.co.com.collector.persistence.dao.SurveyDAO;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        UUID = Utilities.getUUID(this);

        AppSettings.URL_BASE = ResourceNetwork.URL_BASE_PROD;
        etUsername.setText("");
        etPassword.setText("");
        textViewLoginUiid.setText(UUID);
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
                if (offlineSurvey != null && !offlineSurvey.isEmpty()) {
                    offlineWorkVal(offlineSurvey);
                } else {
                    offlineWorkDen();
                }
            } else
                Toast.makeText(this, getString(R.string.login_unknown_user_dao), Toast.LENGTH_LONG).show();


        } else {
            LoginRequest toSend = new LoginRequest(etUsername.getText().toString(), etPassword.getText().toString());
            toSend.setTabletId(UUID);
            mBus.post(toSend);
        }
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

    @Subscribe
    public void onSuccessLoginResponse(LoginResponse response){
        if (response.getResponseData().get(0) != null) {
            AppSession.getInstance().setUser(response.getResponseData().get(0));
            // Invoke the survey synchronize
            GetSurveysRequest toSend = new GetSurveysRequest(AppSession.getInstance().getUser()
                    .getColector_id());
            mBus.post(toSend);
        } else {
            Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.survey_save_send_error), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onSuccessSurveysResponse(GetSurveysResponse response){
        // to be remove
        SurveyDAO dao = new SurveyDAO(LoginActivity.this);
        dao.synchronizeSurveys(response.getResponseData());

        AppSession.getInstance().setSurveyAvailable(response.getResponseData());
        DatabaseHelper.getInstance().addSurveyAvailable(response.getResponseData(),
                LoginActivity.this); //Save on Realm
    }
}