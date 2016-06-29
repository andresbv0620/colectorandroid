package colector.co.com.collector.model;

import java.io.File;

/**
 * Created by Jose Rodriguez on 29/06/2016.
 */
public class ImageRequest {
    private File image;
    private String extension;
    private long question_id;
    private long survey_id;
    private long colector_id;
    private String name;

    public ImageRequest(File image, String extension, long question_id, long survey_id, long colector_id, String name) {
        this.image = image;
        this.extension = extension;
        this.question_id = question_id;
        this.survey_id = survey_id;
        this.colector_id = colector_id;
        this.name = name;
    }

    public File getImage() {
        return image;
    }

    public String getExtension() {
        return extension;
    }

    public long getQuestion_id() {
        return question_id;
    }

    public long getSurvey_id() {
        return survey_id;
    }

    public long getColector_id() {
        return colector_id;
    }

    public String getName() {
        return name;
    }
}
