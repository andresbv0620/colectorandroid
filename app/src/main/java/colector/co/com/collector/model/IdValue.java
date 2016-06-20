package colector.co.com.collector.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdValue extends RealmObject {

    private Long id;
    private String value;
    private String validation;

    public IdValue() {
        super();
    }

    public IdValue(Long id, String value, String validation) {
        super();
        this.id = id;
        this.value = value;
        this.validation = validation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }
}
