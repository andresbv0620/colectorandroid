package co.colector.model;

import java.util.ArrayList;

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
    private boolean uploaded = false;
    private Long titulo_reporte;
    private Long titulo_reporte2;

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

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
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

    public Long getTitulo_reporte() {
        return titulo_reporte;
    }

    public void setTitulo_reporte(Long titulo_reporte) {
        this.titulo_reporte = titulo_reporte;
    }

    public Long getTitulo_reporte2() {
        return titulo_reporte2;
    }

    public void setTitulo_reporte2(Long titulo_reporte2) {
        this.titulo_reporte2 = titulo_reporte2;
    }

    @Override
    public String toString() {
        return "SurveySave{" +
                "instanceId=" + instanceId +
                ", id=" + id +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", HoraIni='" + HoraIni + '\'' +
                ", HoraFin='" + HoraFin + '\'' +
                ", Status='" + Status + '\'' +
                ", responses=" + new ArrayList<>(responses).toString() +
                ", uploaded=" + uploaded +
                ", titulo_reporte=" + titulo_reporte +
                '}';
    }
}
