package colector.co.com.collector.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 17/10/15.
 */
public class SurveySave extends RealmObject {

    private Long instanceId; //Survey Id
    @PrimaryKey
    private Long id; // Id of saved survey
    private String latitude;
    private String longitude;
    private String HoraIni;
    private String HoraFin;
    private String Status;
    private RealmList<IdValue> responses;

    public SurveySave() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getHoraIni() {
        return HoraIni;
    }

    public void setHoraIni(String HoraIni) {
        this.HoraIni = HoraIni;
    }

    public String getHoraFin() {
        return HoraFin;
    }

    public void setHoraFin(String HoraFin) {
        this.HoraFin = HoraFin;
    }


    public RealmList<IdValue> getResponses() {
        if (responses == null) {
            responses = new RealmList<>();
        }
        return responses;
    }

    public void setResponses(RealmList<IdValue> responses) {
        this.responses = responses;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
