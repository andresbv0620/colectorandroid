package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 11/10/15.
 */
public class Question extends RealmObject {

    @PrimaryKey
    private Long input_id;
    /**
     * TEXTO = '1' OK
     * PARRAFO = '2' OK
     * OPCION = '3' OK
     * UNICA = '4' OK
     * MULTIPLE = '5'
     * FOTO = '6'
     * FECHA = '7' OK
     * NUMERO = '8' OK
     * SCAN = '9'
     * DINAMICA = '10'
     */
    private int type;

    private RealmList<QuestionVisibilityRules> valorvisibility;
    private RealmList<QuestionAsociateForm> asociate_form;
    private RealmList<IdOptionValue> responses;
    private RealmList<ResponseComplex> options;
    private RealmList<ResponseAttribute> atributos;
    private String name;
    private String description;
    private String minimo;
    private String defecto;
    private String maximo;
    private Boolean requerido;
    private String validacion;
    private Boolean defecto_previo;
    private String solo_lectura;
    private Boolean oculto;
    private String orden;


    public Question() {
        super();
    }

    public Question(Long id, int type, RealmList<IdOptionValue> responses, RealmList<QuestionVisibilityRules> valorvisibility,
                    RealmList<QuestionAsociateForm> asociate_form,
                    RealmList<ResponseComplex> options, RealmList<ResponseAttribute> atributos,
                    String name, String description,
                    String minimo, String maximo,
                    String defecto, Boolean requerido,
                    String validacion,
                    Boolean defecto_previo, String solo_lectura, Boolean oculto, String orden) {

        this.input_id = id;
        this.type = type;
        this.valorvisibility = valorvisibility;
        this.asociate_form = asociate_form;

        this.responses = responses;
        this.options = options;
        this.atributos = atributos;

        this.name = name;
        this.description = description;
        this.minimo = minimo;
        this.maximo = maximo;
        this.defecto = defecto;
        this.requerido = requerido;
        this.validacion = validacion;

        this.defecto_previo = defecto_previo;

        this.solo_lectura = solo_lectura;
        this.oculto = oculto;
        this.orden = orden;

    }

    public void setDefectoPrevio(Boolean defecto_previo) {
        this.defecto_previo = defecto_previo;
    }

    public Boolean getDefectoPrevio() {
        return defecto_previo;
    }

    public void setSoloLectura(String solo_lectura) {
        this.solo_lectura = solo_lectura;
    }

    public String getSoloLectura() {
        return solo_lectura;
    }

    public void setOculto(Boolean oculto) {
        this.oculto = oculto;
    }

    public Boolean getoculto() {
        return oculto;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }

    public String getOrden() {
        return orden;
    }

    public void setDefecto(String defecto) {
        this.defecto = defecto;
    }

    public String getDefecto() {
        return defecto;
    }

    public void setMin(String min) {
        this.minimo = minimo;
    }

    public String getMin() {
        return minimo;
    }

    public void setMax(String max) {
        this.maximo = maximo;
    }

    public String getMax() {
        return maximo;
    }

    public void setRequerido(Boolean requerido) {
        this.requerido = requerido;
    }

    public Boolean getRequerido() {
        return requerido;
    }

    public void setValidacion(String validacion) {
        this.validacion = validacion;
    }

    public String getValidacion() {
        return validacion;
    }

    public Long getId() {
        return input_id;
    }

    public void setId(Long id) {
        this.input_id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public RealmList<IdOptionValue> getResponses() {
        if (responses == null) {
            responses = new RealmList<>();
        }
        return responses;
    }

    public void setResponses(RealmList<IdOptionValue> responses) {
        this.responses = responses;
    }

    public RealmList<QuestionVisibilityRules> getValorVisibility() {
        if (valorvisibility == null) {
            valorvisibility = new RealmList<>();
        }
        return valorvisibility;
    }

    public void setValorVisibility(RealmList<QuestionVisibilityRules> valorvisibility) {
        this.valorvisibility = valorvisibility;
    }

    public RealmList<QuestionAsociateForm> getAsociate_form() {
        if (asociate_form == null) {
            asociate_form = new RealmList<>();
        }
        return asociate_form;
    }

    public void setAsociate_form(RealmList<QuestionAsociateForm> asociate_form) {
        this.asociate_form = asociate_form;
    }

    public RealmList<ResponseComplex> getOptions() {

        if (options == null) {
            options = new RealmList<>();
        }
        return options;
    }

    public void setOptions(RealmList<ResponseComplex> options) {
        this.options = options;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RealmList<ResponseAttribute> getAtributos() {
        if (atributos == null) {
            atributos = new RealmList<>();
        }
        return atributos;
    }

    public void setAtributos(RealmList<ResponseAttribute> atributos) {
        this.atributos = atributos;
    }
}
