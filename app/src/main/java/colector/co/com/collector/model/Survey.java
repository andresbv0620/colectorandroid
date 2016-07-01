package colector.co.com.collector.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 11/10/15.
 */
public class Survey extends RealmObject {

    @PrimaryKey
    private Long form_id;
    private String form_name;
    private String form_description;
    private Boolean precargado;

    private String longitud;
    private String latitud;
    private String horaini;
    private String horafin;

    private RealmList<Section> sections;
    private
    @Nullable
    Long instanceId;
    private String instanceDate;
    private
    RealmList<IdValue> instanceAnswers = new RealmList<>();
    private boolean uploaded;

    public Survey() {
        super();
    }

    public Survey(Survey survey, SurveySave answers) {
        form_id = survey.getForm_id();
        form_name = survey.getForm_name();
        form_description = survey.getForm_description();
        precargado = survey.getForm_precargados();
        sections = survey.getSections();
        instanceId = answers.getId();
        instanceAnswers = answers.getResponses();
        longitud = answers.getLongitude();
        latitud = answers.getLatitude();
        horaini = answers.getHoraIni();
        horafin = answers.getHoraFin();
        uploaded = answers.isUploaded();
    }

    public Survey(Long form_id, String form_name, String form_description,//String precargado,
                  List<Section> sections) {
        this.form_id = form_id;
        this.form_name = form_name;
        this.form_description = form_description;
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

    public RealmList<Section> getSections() {
        if (sections == null) {
            sections = new RealmList<>();
            for (Section section : sections) {
                for (Question question : section.getInputs()) {
                    this.precargado = question.getDefectoPrevio();

                }
            }

        }
        return sections;
    }

    public void setSections(RealmList<Section> sections) {
        this.sections = sections;
    }

    public
    @Nullable
    Long getInstanceId() {
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

    public RealmList<IdValue> getInstanceAnswers() {
        return instanceAnswers;
    }

    public void setInstanceAnswer(RealmList<IdValue> instanceAnswers) {
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

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Survey && this.form_id.equals(((Survey) o).form_id) &&
                this.instanceAnswers.equals(((Survey) o).instanceAnswers);
    }
}
