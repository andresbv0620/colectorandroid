package colector.co.com.collector.listeners;

import colector.co.com.collector.adapters.SurveyAdapter;
import colector.co.com.collector.model.Survey;

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
