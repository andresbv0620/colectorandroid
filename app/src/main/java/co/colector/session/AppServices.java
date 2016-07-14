package co.colector.session;

import java.util.ArrayList;
import java.util.List;

import co.colector.model.Survey;
import co.colector.model.response.ResponseData;

/**
 * Created by dherrera on 11/10/15.
 */
public class AppServices {

    private static AppServices singletonObject;
    private ResponseData user;
    private List<Survey> surveyAvailable;
    private List<Survey> surveyDone;
    private Survey currentSurvey;
    private String currentPhotoPath;
    private Long currentPhotoID;

    public AppServices() {
    }

    /**
     * Singleton instance
      * @return
     */
    public static synchronized AppServices getInstance() {
        if (singletonObject == null) {
            singletonObject = new AppServices();
        }
        return singletonObject;
    }

    public static synchronized void resetSingletonObject() {
        singletonObject = null;
    }


    public List<Survey> getSurveyDone() {
        if(surveyDone == null){
            surveyDone = new ArrayList<Survey>();
        }
        return surveyDone;
    }

    public void setSurveyDone(List<Survey> surveyDone) {
        this.surveyDone = surveyDone;
    }

    public List<Survey> getSurveyAvailable() {
        if(surveyAvailable == null){
            surveyAvailable = new ArrayList<Survey>();
        }
        return surveyAvailable;
    }

    public void setSurveyAvailable(List<Survey> surveyAvailable) {
        this.surveyAvailable = surveyAvailable;
    }

    public ResponseData getUser() {
        return user;
    }

    public void setUser(ResponseData user) {
        this.user = user;
    }

    public Survey getCurrentSurvey() {
        return currentSurvey;
    }

    public void setCurrentSurvey(Survey currentSurvey) {
        this.currentSurvey = currentSurvey;
    }

    public String getCurrentPhotoPath() {
        return currentPhotoPath;
    }

    public void setCurrentPhotoPath(String currentPhotoPath) {
        this.currentPhotoPath = currentPhotoPath;
    }

    public Long getCurrentPhotoID() {
        return currentPhotoID;
    }

    public void setCurrentPhotoID(Long currentPhotoID) {
        this.currentPhotoID = currentPhotoID;
    }
}