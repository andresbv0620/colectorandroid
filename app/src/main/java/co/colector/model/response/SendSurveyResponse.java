package co.colector.model.response;

/**
 * Created by dherrera on 21/10/15.
 */
public class SendSurveyResponse {

    private Long response_code;
    private String response_description;
    private String record_id;

    public SendSurveyResponse() {
        super();
    }

    public Long getResponseCode() {
        return response_code;
    }


    public void setResponse_code(Long response_code) {
        this.response_code = response_code;
    }

    public String getResponseDescription() {
        return response_description;
    }

    public void setResponse_description(String response_description) {
        this.response_description = response_description;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }
}
