package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 11/10/15.
 */
public class ResponseComplex extends RealmObject {

    @PrimaryKey
    private String record_id;
    private RealmList<ResponseItem> responses;



    public ResponseComplex(String record_id, RealmList<ResponseItem> responses) {
        this.record_id = record_id;
        this.responses = responses;
    }

    public ResponseComplex() {
        super();
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public RealmList<ResponseItem> getResponses() {
        if(responses == null){
            responses = new RealmList<>();
        }
        return responses;
    }

    public void setResponses(RealmList<ResponseItem> responses) {
        this.responses = responses;
    }


}
