package colector.co.com.collector.model.request;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.model.IdInputValue;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Survey;
import colector.co.com.collector.session.AppSession;

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
            if (item.getmType() != 6)
                responses.add(new IdInputValue(String.valueOf(item.getId()), item.getValue()));
        }
    }
}

