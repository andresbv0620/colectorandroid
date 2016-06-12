package colector.co.com.collector.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dherrera on 11/10/15.
 */
public class Section {

    private Long section_id;
    private String name;
    private String description;
    private List<Question> inputs;

    public Section(){
        super();
    }

    public Section(Long id,String name, String description, List<Question> inputs) {
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

    public List<Question> getInputs() {
        if(inputs==null){
            inputs=new ArrayList<Question>();
        }
        return inputs;
    }

    public void setInputs(List<Question> inputs) {
        this.inputs = inputs;
    }
}
