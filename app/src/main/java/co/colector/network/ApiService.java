package co.colector.network;

import co.colector.model.ImageResponse;
import co.colector.model.request.GetSurveysRequest;
import co.colector.model.request.LoginRequest;
import co.colector.model.request.SendSurveyRequest;
import co.colector.model.response.GetSurveysResponse;
import co.colector.model.response.LoginResponse;
import co.colector.model.response.SendSurveyResponse;
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

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("service/fill/responses/")
    Call<SendSurveyResponse> uploadSurveys(@Body SendSurveyRequest surveysRequest,
                                           @Header("token") String token);

    @Multipart
    @POST("service/fill/img/")
    Call<ImageResponse> doStoreImage(@Part MultipartBody.Part image,
                                     @Part("extension") String extension,
                                     @Part("question_id") long question_id,
                                     @Part("survey_id") long survey_id,
                                     @Part("colector_id") long colector_id,
                                     @Part("name") String name);
}
