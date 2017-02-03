package co.colector.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;



/**
 * Created by dherrera on 11/10/15.
 * Class to Store Key Value objects related to Answers
 * Values is a Array of Strings
 */
public class IdValue extends RealmObject {
    private Long idQuestion;
    private RealmList<AnswerValue> value;
    private String validation;
    private int mType;

    // add parameter to identify section id
    private int sectionId;

    public IdValue() {
        super();
    }

    public IdValue(Long idQuestion, RealmList<AnswerValue> value, String validation) {
        super();
        this.idQuestion = idQuestion;
        this.value = value;
        this.validation = validation;
    }

    public IdValue(Long idQuestion, RealmList<AnswerValue> value, String validation, int type) {
        super();
        this.idQuestion = idQuestion;
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

    public Long getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Long id) {
        this.idQuestion = id;
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

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IdValue
                && this.idQuestion.equals(((IdValue) o).idQuestion)
                && this.value.equals(((IdValue) o).value);
    }

    @Override
    public int hashCode() {
        return idQuestion.hashCode();
    }

    @Override
    public String toString() {
        String values = "";
        for(AnswerValue local_value: value)
        {
            values += local_value.getValue() + ",";
        }
        return "IdValue{" +
                "idQuestion=" + idQuestion +
                ", value='" + values + '\'' +
                ", validation='" + validation + '\'' +
                ", mType=" + mType +
                '}';
    }
}
