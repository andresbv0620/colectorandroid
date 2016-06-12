package colector.co.com.collector.model.response;

/**
 * Created by dherrera on 11/10/15.
 */
public class ResponseData {


    private String token;
    private Long colector_id;
    private String colector_name;

    public ResponseData(String token, Long colector_id, String colector_name) {
        super();
        this.token = token;
        this.colector_id = colector_id;
        this.colector_name = colector_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getColector_id() {
        return colector_id;
    }

    public void setColector_id(Long colector_id) {
        this.colector_id = colector_id;
    }

    public String getColector_name() {
        return colector_name;
    }

    public void setColector_name(String colector_name) {
        this.colector_name = colector_name;
    }
}
