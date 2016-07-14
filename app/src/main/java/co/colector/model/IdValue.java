package co.colector.model;

import io.realm.RealmObject;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdValue extends RealmObject {

    private Long id;
    private String value;
    private String validation;
    private int mType;

    public IdValue() {
        super();
    }

    public IdValue(Long id, String value, String validation) {
        super();
        this.id = id;
        this.value = value;
        this.validation = validation;
    }

    public IdValue(Long id, String value, String validation, int type) {
        super();
        this.id = id;
        this.value = value;
        this.validation = validation;
        this.mType = type;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
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

    @Override
    public boolean equals(Object o) {
        return o instanceof IdValue && this.id.equals(((IdValue) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
