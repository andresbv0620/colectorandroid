package co.colector.listeners;

import co.colector.adapters.SurveyAdapter;
import co.colector.model.Survey;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public interface OnUploadSurvey {

    /**
     * On Upload button clicked
     * @param survey clicked
     */
    void onUploadClicked(Survey survey, SurveyAdapter surveyAdapter);
}
