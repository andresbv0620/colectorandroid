package colector.co.com.collector.network;

import android.content.Context;
import android.util.Log;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import colector.co.com.collector.model.request.GetSurveysRequest;
import colector.co.com.collector.model.request.LoginRequest;
import colector.co.com.collector.model.request.SendSurveyRequest;
import colector.co.com.collector.model.response.GetSurveysResponse;
import colector.co.com.collector.model.response.LoginResponse;
import colector.co.com.collector.model.response.SendSurveyResponse;
import colector.co.com.collector.session.AppSession;
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
