package co.colector.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by dherrera on 11/10/15.
 */
public class Section extends RealmObject {

    @PrimaryKey
    private Long section_id;
    private String name;
    private String description;
    private boolean repetible;

    private RealmList<Question> inputs;

    public Section(){
        super();
    }

    public Section(Long id,String name, String description, RealmList<Question> inputs, boolean repetible) {
        super();
        this.section_id=id;
        this.name = name;
        this.description = description;
        this.inputs = inputs;
        this.repetible = repetible;
    }

    public Long getId() {
        return section_id;
    }

    public void setId(Long id) {
        this.section_id = id;
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

    public RealmList<Question> getInputs() {
        if(inputs==null){
            inputs=new RealmList<>();
        }
        return inputs;
    }

    public void setInputs(RealmList<Question> inputs) {
        this.inputs = inputs;
    }

    public Long getSection_id() {
        return section_id;
    }

    public void setSection_id(Long section_id) {
        this.section_id = section_id;
    }

    public boolean isRepetible() {
        return repetible;
    }

    public void setRepetible(boolean repetible) {
        this.repetible = repetible;
    }
}
