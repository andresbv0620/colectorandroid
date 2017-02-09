package co.colector.session;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import co.colector.database.DatabaseHelper;
import co.colector.helpers.PreferencesManager;
import co.colector.model.Survey;
import co.colector.model.response.ResponseData;

/**
 * Created by dherrera on 11/10/15.
 */
public class AppSession {

    private static AppSession singletonObject;
    private ResponseData user;
    private List<Survey> surveyAvailable;
    private Survey currentSurvey;
    private static int typeSurveySelected;
    private String currentPhotoPath;
    private Long currentPhotoID;

    public AppSession() {
    }

    /**
     * Singleton instance
     *
     * @return
     */
    public static synchronized AppSession getInstance()
    {
        if (singletonObject == null) {
            singletonObject = new AppSession();
        }
        return singletonObject;
    }

    public static synchronized void resetSingletonObject() {
        singletonObject = null;
    }


    public List<Survey> getSurveyAvailable()
    {
        // Patch to fix issue when there is not Surveys Available
        if(surveyAvailable == null)
        {
            surveyAvailable = new ArrayList<Survey>();
            List<Survey> allSurveys = DatabaseHelper.getInstance().getSurveys();

            String surveyIds = PreferencesManager.getInstance().getPrefs().getString(
                    PreferencesManager.AVAILABLE_SURVEYS,
                    ""
            );

            StringTokenizer st = new StringTokenizer(surveyIds, ",");
            while(st.hasMoreTokens())
            {
                String stringSurveyId = st.nextToken();
                try{
                    long surveyId = Long.parseLong(stringSurveyId);
                    boolean added = false;
                    for(Survey s: allSurveys)
                    {
                        if(s.getForm_id() == surveyId)
                        {
                            surveyAvailable.add(s);
                            added = true;
                            break;
                        }
                    }
                    if(!added)
                    {
                        Log.i("SurveyID Not added", "Survey id "+ stringSurveyId+ "Does not exists");
                    }
                }catch (NumberFormatException nfe)
                {
                    Log.i("converting Survey Id", stringSurveyId+" Is not a Number");
                }
            }
        }
        return surveyAvailable;
//        return surveyAvailable == null ? DatabaseHelper.getInstance().getSurveys() : surveyAvailable;
    }

    public void setSurveyAvailable(List<Survey> surveyAvailable)
    {
        this.surveyAvailable = surveyAvailable;

        // Store IDs Survey
        String surveysAvailableIds = "";
        int i=0;
        for (Survey s: surveyAvailable)
        {
            surveysAvailableIds += s.getForm_id()+",";
            i++;
        }
        PreferencesManager.getInstance().setAvailableSurveys(surveysAvailableIds);
    }

    public ResponseData getUser() {
        return PreferencesManager.getInstance().getUserData();
    }

    public void setUser(ResponseData user) {
        this.user = user;
    }

    public Survey getCurrentSurvey() {
        return currentSurvey;
    }

    public static int getTypeSurveySelected() {
        return typeSurveySelected;
    }

    public void setCurrentSurvey(Survey currentSurvey, int typeSurveySelected) {
        this.typeSurveySelected = typeSurveySelected;
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

    public void cleanSurveyAvailable() {
        if (surveyAvailable != null && !surveyAvailable.isEmpty())
        {
            try {
                for (Survey survey : surveyAvailable) {

                    survey.setInstanceId(null);
                    survey.setInstanceAnswer(null);
                }
            } catch (IllegalStateException ise)
            {
                Log.i("Realm Exception", "Changing Realm data can only be done from inside a transaction.");
            }
        }
    }

}