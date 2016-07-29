package co.colector.model;

import io.realm.RealmObject;

/**
 * Created by Jose Rodriguez on 29/07/2016.
 */
public class Autollenar extends RealmObject {
    private Long entrada_fuente;
    private Long entrada_destino;

    public Long getEntrada_fuente() {
        return entrada_fuente;
    }

    public Long getEntrada_destino() {
        return entrada_destino;
    }
}
