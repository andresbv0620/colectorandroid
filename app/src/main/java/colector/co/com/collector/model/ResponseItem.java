package colector.co.com.collector.model;

/**
 * Created by dherrera on 11/10/15.
 */
public class ResponseItem {
    private Long input_id;
    private String value;
    private Long tipo;
    private String label;

    public ResponseItem(){
        super();
    }

    public ResponseItem(Long input_id, String value, Long tipo, String label) {
        this.input_id = input_id;
        this.value = value;
        this.tipo = tipo;
        this.label = label;
    }

    public Long getInput_id() {
        return input_id;
    }

    public void setInput_id(Long input_id) {
        this.input_id = input_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getTipo() {
        return tipo;
    }

    public void setTipo(Long tipo) {
        this.tipo = tipo;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
