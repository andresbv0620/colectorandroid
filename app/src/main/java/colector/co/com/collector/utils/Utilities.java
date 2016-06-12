package colector.co.com.collector.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by dherrera on 17/10/15.
 */
public class Utilities {


    /**
     * Check if device has access to internet and conectivity service is available
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cm.getActiveNetworkInfo();
        if (cm.getActiveNetworkInfo() != null && nf.isConnected()){
                return true;
        }
        return false;
    }

    /**
     * Check if device has access to internet
     * @return

    public static boolean isInternetAvailable() {
        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }
     */

    /**
     * Get UUID from system
     * @return
     */
    public static String getUUID(Context context){
        TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String mientra="001";
        return mientra;
    }

    /**
     * Add new element to array
     * @param array
     * @param push
     * @return
     */
    public static int[] push(int[] array, int push) {
        int[] longer = new int[array.length + 1];
        for (int i = 0; i < array.length; i++)
            longer[i] = array[i];
        longer[array.length] = push;
        return longer;
    }

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
}
