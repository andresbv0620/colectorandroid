package co.colector.model;

import io.realm.RealmObject;

/**
 * Created by dherrera on 11/10/15.
 */
public class QuestionVisibilityRules extends RealmObject{
    private Long elemento;
    private String operador;
    private String valor;

    public QuestionVisibilityRules(){
        super();
    }

    public QuestionVisibilityRules(Long elemento,String operador, String valor) {
        super();
        this.elemento = elemento;
        this.operador = operador;
        this.valor = valor;
    }

    public Long getElemento() {
        return elemento;
    }

    public void setElemento(Long elemento) {
        this.elemento = elemento;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
