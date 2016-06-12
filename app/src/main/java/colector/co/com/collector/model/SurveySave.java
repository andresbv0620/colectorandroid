package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dherrera on 17/10/15.
 */
public class SurveySave {

    private Long instanceId;
    private Long id;
    private String latitude;
    private String longitude;
    private String HoraIni;
    private String HoraFin;
    private String Status;


    private List<IdValue> responses;

    public SurveySave(){
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
        this.HoraFin= HoraFin;
    }


    public List<IdValue> getResponses() {
        if(responses == null){
            responses = new ArrayList<IdValue>();
        }
        return responses;
    }

    public void setResponses(List<IdValue> responses) {
        this.responses = responses;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
