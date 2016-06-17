package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

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
    private RealmList<Question> inputs;

    public Section(){
        super();
    }

    public Section(Long id,String name, String description, RealmList<Question> inputs) {
        super();
        this.section_id=id;
        this.name = name;
        this.description = description;
        this.inputs = inputs;
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
}
