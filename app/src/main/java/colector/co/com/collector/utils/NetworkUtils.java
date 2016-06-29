package colector.co.com.collector.utils;

import android.content.Context;
import android.provider.Settings;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Jose Rodriguez on 26/06/2016.
 */
public class NetworkUtils {

    /**
     * Converting File into Part data to be sended into retrofit service.
     * @param imgFile file stored attached to answer.
     * @return MultipartBody file
     */

    public static MultipartBody.Part obtainPartImageData(File imgFile){
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), imgFile);
        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("document", imgFile.getName(), requestFile);

        return imagePart;
    }

    public static String getAndroidID(Context mContext){
        return Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.ANDROID_ID);
    }
}
