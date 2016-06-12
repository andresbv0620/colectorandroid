package colector.co.com.collector.model;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdOptionValue {
    private Long response_id;
    private String value;
    private boolean status;

    public IdOptionValue(){
        super();
    }

    public IdOptionValue(Long response_id, String value) {
        super();
        this.response_id = response_id;
        this.value = value;
    }

    public Long getId() {
        return response_id;
    }

    public void setId(Long id) {
        this.response_id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
