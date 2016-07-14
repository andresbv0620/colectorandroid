package co.colector.network;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import co.colector.model.ImageRequest;
import co.colector.model.ImageResponse;
import co.colector.model.request.GetSurveysRequest;
import co.colector.model.request.LoginRequest;
import co.colector.model.request.SendSurveyRequest;
import co.colector.model.response.GetSurveysResponse;
import co.colector.model.response.LoginResponse;
import co.colector.model.response.SendSurveyResponse;
import co.colector.session.AppSession;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jose Rodriguez on 11/06/2016.
 */
public class ApiCallsManager {

    private static final String TAG = "ApiCalls";

    private Context mContext;
    private Bus mBus;
    private ApiClient mApiClient;

    public ApiCallsManager(Context mContext, Bus mBus) {
        this.mContext = mContext;
        this.mBus = mBus;
        mApiClient = ApiClient.getInstance(mContext);
    }

    @Subscribe
    public void doUploadImage(ImageRequest imageRequest){
        mApiClient.doUploadImage(imageRequest).enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response.isSuccessful())
                    mBus.post(response.body());
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {

            }
        });
    }

    @Subscribe
    public void doLogin(LoginRequest loginRequest) {
        mApiClient.doLogin(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful())
                    mBus.post(response.body());
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(TAG, "Failure do Login");
            }
        });
    }

    @Subscribe
    public void getSurveys(GetSurveysRequest surveysRequest) {
        if (AppSession.getInstance().getUser() != null) {
            mApiClient.getSurveys(surveysRequest, AppSession.getInstance().getUser()
                    .getToken()).enqueue(new Callback<GetSurveysResponse>() {
                @Override
                public void onResponse(Call<GetSurveysResponse> call,
                                       Response<GetSurveysResponse> response) {
                    if (response.isSuccessful())
                        mBus.post(response.body());
                }

                @Override
                public void onFailure(Call<GetSurveysResponse> call, Throwable t) {
                    Log.d(TAG, "Failure do surveys");
                }
            });
        }
    }

    @Subscribe
    public void uploadSurveys(SendSurveyRequest uploadSurvey) {
        if (AppSession.getInstance().getUser() != null) {
            mApiClient.uploadSurveys(uploadSurvey, AppSession.getInstance().getUser()
                    .getToken()).enqueue(new Callback<SendSurveyResponse>() {
                @Override
                public void onResponse(Call<SendSurveyResponse> call, Response<SendSurveyResponse> response) {
                    if (response.isSuccessful()) mBus.post(response.body());
                }

                @Override
                public void onFailure(Call<SendSurveyResponse> call, Throwable t) {
                    Log.d(TAG, "Failure upload survey");
                }
            });
        }
    }
}
