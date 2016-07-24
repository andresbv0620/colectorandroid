package co.colector.model;

import io.realm.RealmObject;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class AnswerValue extends RealmObject {

    private String value;

    public AnswerValue() {
    }

    public AnswerValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "AnswerValue{" +
                "value='" + value + '\'' +
                '}';
    }
}
