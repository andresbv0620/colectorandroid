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
        this.horaini  = survey.getInstanceHoraIni();
        this.horafin = survey.getInstanceHoraFin();
        this.setResponsesData(survey.getInstanceAnswers());
    }

    public String getColector_id() {
        return colector_id;
    }

    public void setColector_id(String colector_id) {
        this.colector_id = colector_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public void setForm_longitude(String longitude) {
        this.longitud =longitude;
    }

    public String getForm_longitude() {
        return longitud;
    }

    public void setForm_latitude(String latitude) {
        this.latitud =latitude;
    }

    public String getForm_latitude() {
        return latitud;
    }

    public void setForm_horaini(String horaini) {
        this.horaini =horaini;
    }

    public String getForm_horaini() {
        return horaini;
    }

    public void setForm_horafin(String horafin) {
        this.horafin =horafin;
    }

    public String getForm_horafin() {
        return horafin;
    }

    public List<IdInputValue> getResponses() {
        return responses;
    }

    public void setResponses(List<IdInputValue> responses) {
        this.responses = responses;
    }

    public void setResponsesData(List<IdValue> responsesData) {
        responses = new ArrayList<>();
        for (IdValue item: responsesData) {
            responses.add(new IdInputValue(String.valueOf(item.getId()),item.getValue()));
        }
    }
}

