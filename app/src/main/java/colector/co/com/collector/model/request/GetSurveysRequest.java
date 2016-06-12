package colector.co.com.collector.model.request;

/**
 * Created by dherrera on 11/10/15.
 */
public class GetSurveysRequest {

    private Long colector_id;

    public GetSurveysRequest() {
        super();
    }

    public GetSurveysRequest(Long colector_id) {
        super();
        this.colector_id=colector_id;
    }

    public Long getColector_id() {
        return colector_id;
    }

    public void setColector_id(Long colector_id) {
        this.colector_id = colector_id;
    }
}
