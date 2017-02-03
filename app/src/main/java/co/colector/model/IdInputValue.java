package co.colector.model;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdInputValue {
    private String input_id;
    private String value;
    private String section;

    public IdInputValue(){
        super();
    }

    public IdInputValue(String input_id, String value, int sectionId) {
        super();
        this.input_id = input_id;
        this.value = value;
;        this.section = sectionId+"";
    }

    public String getId() {
        return input_id;
    }

    public void setId(String id) {
        this.input_id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IdInputValue{" +
                "input_id=" + input_id +
                ", value='" + value + '\''+
                '}';
    }
}
