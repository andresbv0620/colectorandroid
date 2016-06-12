package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Attributes into the dynamic form
 * Created by dherrera on 13/11/15.
 */
public class ResponseAttribute {

    private Long input_id;
    private int type;
    private String label;
    private List<IdOptionValue> responses;

    public ResponseAttribute(Long input_id, int type, String label) {
        super();
        this.input_id = input_id;
        this.type = type;
        this.label = label;
    }

    public ResponseAttribute() {
        super();
    }


    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<IdOptionValue> getResponses() {
        if(responses == null){
            responses=new ArrayList<IdOptionValue>();
        }
        return responses;
    }

    public void setResponses(List<IdOptionValue> responses) {
        this.responses = responses;
    }
}
