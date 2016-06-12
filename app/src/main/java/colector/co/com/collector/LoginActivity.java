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
import io.fabric.sdk.android.Fabric;
import java.util.List;

import colector.co.com.collector.http.AsyncResponse;
import colector.co.com.collector.http.BackgroundTask;
import colector.co.com.collector.http.ResourceNetwork;
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

import static colector.co.com.collector.settings.AppSettings.HTTP_OK;

public class LoginActivity extends AppCompatActivity {

    /*
KEY STORE
    PATH: D:\Android\AndroidStudioProjects\KEYCOLECTOR\KEYCOLECTOR.jks
    CONTRASEÑA:Colector_2016!

KEY
    Alias:colectorAPP
    Password:Colector2016APP!

MASTER PASSWORD
    Colector_2016!

    Nombre de Alias: colectorAPP
Fecha de Creación: 07-feb-2016
Tipo de Entrada: PrivateKeyEntry
Longitud de la Cadena de Certificado: 1
Certificado[1]:
Propietario: CN=Julian David Castillo S, OU=COLECTOR.COM, O=COLECTOR, L=COLOMBIA
, ST=CALI, C=57
Emisor: CN=Julian David Castillo S, OU=COLECTOR.COM, O=COLECTOR, L=COLOMBIA, ST=
CALI, C=57
Número de serie: 3728fee7
Válido desde: Sun Feb 07 08:43:55 ART 2016 hasta: Thu Jan 31 08:43:55 ART 2041
Huellas digitales del Certificado:
         MD5: 39:98:70:45:FC:81:F1:5B:78:3F:10:67:EF:5C:8E:5D
         SHA1: 29:38:AE:5F:C8:BD:E3:2B:FD:0E:C2:15:EA:7D:66:7C:F3:F8:66:7E
         SHA256: 10:52:75:1D:54:73:9E:39:B8:18:2D:93:A5:CF:C1:2A:C9:ED:51:3F:FC:
34:43:27:8A:5C:1C:83:E9:80:01:41
         Nombre del Algoritmo de Firma: SHA256withRSA
         Versión: 3

Extensiones:

#1: ObjectId: 2.5.29.14 Criticality=false
SubjectKeyIdentifier [
KeyIdentifier [
0000: BE 84 35 19 4B 5F 44 09   03 F2 82 0F 5B 6D B7 3F  ..5.K_D.....[m.?
0010: C0 82 6B 06                                        ..k.
]
]

Clave de API  GOOGLE MAPS
Realizado con PERAST.SAS@GMAIL.COM
https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID&r=86:F7:73:0E:01:C2:F8:EF:19:54:EE:11:06:8E:31:42:99:C3:42:13%3Bcolector.co.com.collector

Esta es tu clave de API
AIzaSyCmkE1o0S3r0V0JtQONo8P7Oz1L9nObAAE


    * */

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

    public void offlineWorkVal(final List<Survey> offlineSurvey ) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.login_offline_message)
                .setPositiveButton(getString(R.string.login_offline_ok), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppSession.getInstance().setSurveyAvailable(offlineSurvey);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
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
        }else if(etUsername.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals("")){
            Toast.makeText(this,getString(R.string.login_error_empty),Toast.LENGTH_LONG).show();
        }else if(!Utilities.isNetworkConnected(this)) {
            //INIDICAR SI PUEDE TRABAJARA EN MODO OFFFLINE Y SI QUIERE CONTINUAR
            Toast.makeText(this, getString(R.string.common_internet_not_available), Toast.LENGTH_LONG).show();

            //VALIDAR TRABAJO OFFLINE Y CONTINUAR
            List<Survey> offlineSurvey = new SurveyDAO(this).getSurveyAvailable();

            //VALIDAR SI USUARIO, CONTRASEÑA, TABLET ES VALIDA Y SET TOKEN JUNTO CON COD_ID AL PROYECTO.
            boolean usuarioValido=false;

            if(usuarioValido){
                if(offlineSurvey != null && offlineSurvey.size() > 0) {
                    offlineWorkVal(offlineSurvey);
                }else{
                    offlineWorkDen();
                }
            }else
                Toast.makeText(this, getString(R.string.login_unknown_user_dao), Toast.LENGTH_LONG).show();


        }else {
            LoginRequest toSend = new LoginRequest(etUsername.getText().toString(), etPassword.getText().toString());
            toSend.setTabletId(UUID);

            AsyncResponse callback = new AsyncResponse() {
                @Override
                public void callback(Object output,String Sended) {

                    if(output instanceof LoginResponse){
                        LoginResponse response = (LoginResponse) output;

                        if(response.getResponseCode().equals(HTTP_OK) ){
                            if(response.getResponseData().get(0) != null) {
                                AppSession.getInstance().setUser(response.getResponseData().get(0));
                                // Invoke the survey synchronize
                                getSurveys();
                            }else if(output instanceof ErrorResponse){
                                Toast.makeText(LoginActivity.this, ((ErrorResponse) output).getMessage(), Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(LoginActivity.this, LoginActivity.this.getString(R.string.survey_save_send_error), Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(LoginActivity.this,response.getResponseDescription(),Toast.LENGTH_LONG).show();
                        }
                    }else if(output instanceof ErrorResponse){
                        ErrorResponse response =(ErrorResponse) output;
                        Toast.makeText(LoginActivity.this,response.getMessage() + "VER TOKEN",Toast.LENGTH_LONG).show();
                    }
                }
            };

                BackgroundTask bt = new BackgroundTask(this, toSend, new LoginResponse(), callback, null,false);
                bt.execute(AppSettings.URL_BASE+ResourceNetwork.URL_LOGIN_DEF);

        }

    }

    private void getSurveys(){

        GetSurveysRequest toSend = new GetSurveysRequest(AppSession.getInstance().getUser().getColector_id());
        AsyncResponse callback = new AsyncResponse() {
            @Override
            public void callback(Object output,String Sended) {

                if(output instanceof GetSurveysResponse){

                    GetSurveysResponse response = (GetSurveysResponse) output;

                    if(response.getResponseCode().equals(HTTP_OK)){
                        SurveyDAO dao = new SurveyDAO(LoginActivity.this);
                        dao.synchronizeSurveys(response.getResponseData());

                        AppSession.getInstance().setSurveyAvailable(response.getResponseData());

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(LoginActivity.this,response.getResponseDescription(),Toast.LENGTH_LONG).show();
                    }


                }else if(output instanceof ErrorResponse){
                    ErrorResponse response =(ErrorResponse) output;
                    Toast.makeText(LoginActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        };

        BackgroundTask bt = new BackgroundTask(this, toSend, new GetSurveysResponse(), callback, null, false);
        bt.execute(AppSettings.URL_BASE+ResourceNetwork.URL_SURVEY_DEF);
    }

}
