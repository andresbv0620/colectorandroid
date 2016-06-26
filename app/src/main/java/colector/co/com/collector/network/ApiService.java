package colector.co.com.collector.network;

import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public interface ApiService {
    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("authentication/onestep/")
    Call<LoginResponse> doLogin(@Body LoginRequest loginRequest);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("service/form/all/")
    Call<GetSurveysResponse> getSurveys(@Body GetSurveysRequest surveysRequest,
                                        @Header("token") String token);

    //Waiting for url and type of response.
    @Multipart
    @POST("<waiting for url>")
    Call<Object> doStoreImage(@Part MultipartBody.Part image,
                              @Part("extension") String extension,
                              @Part("question_id") long question_id,
                              @Part("survey_id") long survey_id,
                              @Part("name") String name);
}
