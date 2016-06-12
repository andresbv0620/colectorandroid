package colector.co.com.collector.session;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.model.Survey;
import colector.co.com.collector.model.response.ResponseData;

/**
 * Created by dherrera on 11/10/15.
 */
public class AppSession {

    private static AppSession singletonObject;
    private ResponseData user;
    private List<Survey> surveyAvailable;
    private List<Survey> surveyDone;
    private Survey currentSurvey;
    private static int typeSurveySelected;
    private String currentPhotoPath;
    private Long currentPhotoID;

    public AppSession() {
    }

    /**
     * Singleton instance
      * @return
     */
    public static synchronized AppSession getInstance() {
        if (singletonObject == null) {
            singletonObject = new AppSession();
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
    public static int getTypeSurveySelected() {return typeSurveySelected;}

    public void setCurrentSurvey(Survey currentSurvey, int typeSurveySelected) {
        this.typeSurveySelected =typeSurveySelected;
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