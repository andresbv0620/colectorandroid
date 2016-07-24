package co.colector.model.request;

import java.util.ArrayList;
import java.util.List;

import co.colector.ColectorApplication;
import co.colector.R;
import co.colector.model.IdInputValue;
import co.colector.model.IdValue;
import co.colector.model.Survey;
import co.colector.model.AnswerValue;
import co.colector.session.AppSession;
import co.colector.utils.NetworkUtils;

/**
 * Created by dherrera on 11/10/15.
 */
public class SendSurveyRequest {

    private String colector_id;
    private String form_id;
    private String longitud;
    private String latitud;
    private String horaini;
    private String horafin;
    private List<IdInputValue> responses;


    public SendSurveyRequest(Survey survey) {
        this.colector_id = String.valueOf(AppSession.getInstance().getUser().getColector_id());
        this.form_id = String.valueOf(survey.getForm_id());
        this.longitud = survey.getInstanceLongitude();
        this.latitud = survey.getInstanceLatitude();
        this.horaini = survey.getInstanceHoraIni();
        this.horafin = survey.getInstanceHoraFin();
        this.setResponsesData(survey.getInstanceAnswers());
    }

    public List<IdInputValue> getResponses() {
        return responses;
    }

    public void setResponses(List<IdInputValue> responses) {
        this.responses = responses;
    }

    private void setResponsesData(List<IdValue> responsesData) {
        responses = new ArrayList<>();
        for (IdValue item : responsesData) {
            switch (item.getmType()) {
                case 6:
                case 14:
                case 16:
                    for (AnswerValue answerValue : item.getValue())
                    if (!answerValue.getValue().equals("")) {
                        int lastIndex = answerValue.getValue().length();
                        int slashIndex = answerValue.getValue().lastIndexOf("/");
                        responses.add(new IdInputValue(String.valueOf(item.getId()), ColectorApplication.getInstance().getString(R.string.image_name_format,
                                NetworkUtils.getAndroidID(ColectorApplication.getInstance()),
                                answerValue.getValue().substring((slashIndex + 1), lastIndex))));
                    }
                    break;
                default:
                    for (AnswerValue answerValue : item.getValue())
                        responses.add(new IdInputValue(String.valueOf(item.getId()), answerValue.getValue()));
            }

        }
    }
}

