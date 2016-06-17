package colector.co.com.collector.model;

import io.realm.RealmObject;

/**
 * Created by dherrera on 11/10/15.
 */
public class QuestionAsociateForm extends RealmObject {
    private Boolean actualizar_existente;
    private String name;
    private Long associate_id;
    private Boolean crear_nuevo;
    private Boolean seleccionar_existentes;
    private Boolean seleccionar_multiples;
    private String description;

    public QuestionAsociateForm(){
        super();
    }

    public QuestionAsociateForm(Boolean actualizar_existente, String name,Long associate_id,
                                Boolean crear_nuevo,Boolean seleccionar_existentes, Boolean seleccionar_multiples,
                                String description) {
        super();
        this.associate_id = associate_id;
        this.actualizar_existente = actualizar_existente;
        this.name = name;
        this.description = description;
        this.crear_nuevo = crear_nuevo;
        this.seleccionar_existentes = seleccionar_existentes;
        this.seleccionar_multiples = seleccionar_multiples;
    }

    public Long getAssociate_id() {
        return associate_id;
    }

    public void setAssociate_id(Long associate_id) {
        this.associate_id = associate_id;
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

    public void setDescripion(String description) {
        this.description = description;
    }
////

    public Boolean getCrea_nuevo() {
        return crear_nuevo;
    }

    public void setCrea_nuevo(Boolean crear_nuevo) {
        this.crear_nuevo = crear_nuevo;
    }

    public Boolean getActualizar_existente() {
        return actualizar_existente;
    }

    public void setActualizar_existente(Boolean actualizar_existente) {
        this.actualizar_existente = actualizar_existente;
    }

    public Boolean getSeleccionar_existentes() {
        return seleccionar_existentes;
    }

    public void setSeleccionar_existentes(Boolean seleccionar_existentes) {
        this.seleccionar_existentes = seleccionar_existentes;
    }

    public Boolean getSeleccionar_multiples() {
        return seleccionar_multiples;
    }

    public void setSeleccionar_multiples(Boolean seleccionar_multiples) {
        this.seleccionar_multiples = seleccionar_multiples;
    }

}
