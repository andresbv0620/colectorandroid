package co.colector.model;

import java.util.List;

import co.colector.model.response.ResponseData;

/**
 * Created by Jose Rodriguez on 22/08/2016.
 */
public class User {
    private String user;
    private String password;
    private ResponseData responseData;
    private List<Survey> surveyList;

    public User(String user, String password, ResponseData responseData, List<Survey> surveyList) {
        this.user = user;
        this.password = password;
        this.responseData = responseData;
        this.surveyList = surveyList;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public ResponseData getResponseData() {
        return responseData;
    }

    public List<Survey> getSurveyList() {
        return surveyList;
    }
}
