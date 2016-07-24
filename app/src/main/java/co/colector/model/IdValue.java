package co.colector.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdValue extends RealmObject {

    private Long id;
    private RealmList<AnswerValue> value;
    private String validation;
    private int mType;

    public IdValue() {
        super();
    }

    public IdValue(Long id, RealmList<AnswerValue> value, String validation) {
        super();
        this.id = id;
        this.value = value;
        this.validation = validation;
    }

    public IdValue(Long id, RealmList<AnswerValue> value, String validation, int type) {
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

    public RealmList<AnswerValue> getValue() {
        return value;
    }

    public void setValue(RealmList<AnswerValue> value) {
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

    @Override
    public String toString() {
        return "IdValue{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", validation='" + validation + '\'' +
                ", mType=" + mType +
                '}';
    }
}
