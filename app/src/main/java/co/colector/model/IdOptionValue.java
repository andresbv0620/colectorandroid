package co.colector.model;

import io.realm.RealmObject;

/**
 * Created by dherrera on 11/10/15.
 */
public class IdOptionValue extends RealmObject {

    private Long response_id;
    private String value;
    private long question_id;
    private String answer;
    private boolean status = false;

    public IdOptionValue() {
        super();
    }

    public IdOptionValue(Long response_id, String value, Long question_id, String answer) {
        super();
        this.response_id = response_id;
        this.value = value;
        this.question_id = question_id;
        this.answer = answer;
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

    public long getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(long question_id) {
        this.question_id = question_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}