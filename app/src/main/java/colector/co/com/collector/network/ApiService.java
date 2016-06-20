package colector.co.com.collector.network;

import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
}
