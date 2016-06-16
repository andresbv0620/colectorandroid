package colector.co.com.collector.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dherrera on 11/10/15.
 */
public class Survey {
    private Long form_id;
    private String form_name;
    private String form_description;
    public Boolean precargado;

    private String longitud;
    private String latitud;
    private String horaini;
    private String horafin;

    private List<Section> sections;
    private Long instanceId;
    private String instanceDate;
    private List<IdValue> instanceAnswers;

    public Survey() {
        super();
    }

    public Survey(Long form_id, String form_name, String form_description,//String precargado,
                  List<Section> sections) {
        this.form_id = form_id;
        this.form_name = form_name;
        this.form_description = form_description;
        //this.sections = sections;
        //this.precargado = precargado;
    }

    public String getSurveyDoneDescription() {
        return instanceDate;
    }

    public Long getForm_id() {
        return form_id;
    }

    public void setForm_id(Long form_id) {
        this.form_id = form_id;
    }

    public String getForm_name() {
        return form_name;
    }

    public void setForm_names(String form_name) {
        this.form_name = form_name;
    }

    public String getForm_description() {
        return form_description;
    }

    public void setForm_description(String form_description) {
        this.form_description = form_description;
    }

    public Boolean getForm_precargados() {
        return precargado;
    }

    public void setForm_precargados(Boolean precargado) {
        this.precargado = precargado;
    }

    public List<Section> getSections() {
        if (sections == null) {
            sections = new ArrayList<Section>();
            for (Section section : sections) {
                for (Question question : section.getInputs()) {
                    this.precargado = question.getDefectoPrevio();

                }
            }

        }
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceDate() {
        return instanceDate;
    }

    public void setInstanceDate(String instanceDate) {
        this.instanceDate = instanceDate;
    }

    public String getInstanceLongitude() {
        return longitud;
    }

    public void setInstanceLongitude(String form_longitud) {
        this.longitud = form_longitud;
    }

    public String getInstanceLatitude() {
        return latitud;
    }

    public void setInstanceLatitude(String form_latitud) {
        this.latitud = form_latitud;
    }

    public String getInstanceHoraIni() {
        return horaini;
    }

    public void setInstanceHoraIni(String form_horaini) {
        this.horaini = form_horaini;
    }


    public String getInstanceHoraFin() {
        return horafin;
    }

    public void setInstanceHoraFin(String form_horafin) {
        this.horafin = form_horafin;
    }

    public List<IdValue> getInstanceAnswers() {
        if (instanceAnswers == null) {
            instanceAnswers = new ArrayList<IdValue>();
        }
        return instanceAnswers;
    }

    public void setInstanceAnswer(List<IdValue> instanceAnswers) {
        this.instanceAnswers = instanceAnswers;
    }

    public
    @Nullable
    String getAnswer(@Nullable Long id) {
        if (id == null || instanceAnswers == null) return null;
        for (IdValue item : instanceAnswers) {
            if (item.getId().equals(id)) {
                return item.getValue();
            }
        }
        return null;
    }

    public
    @Nullable
    List<String> getListAnswers(Long id) {
        if (instanceAnswers == null) return null;
        List<String> listAnswers = new ArrayList<>();
        for (IdValue item : instanceAnswers) {
            if (item.getId().equals(id)) {
                listAnswers.add(item.getValue());
            }
        }
        return listAnswers;
    }
}
