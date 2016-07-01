package colector.co.com.collector.model;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.session.AppSession;
import colector.co.com.collector.utils.NetworkUtils;

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

    public ImageRequest(Survey survey, IdValue imageSave, Context context) {
        int dotIndex = imageSave.getValue().lastIndexOf(".");
        int slashIndex = imageSave.getValue().lastIndexOf("/");
        int lastIndex = imageSave.getValue().length();
        this.image = new File(imageSave.getValue());
        this.extension = imageSave.getValue().substring(dotIndex + 1);
        this.question_id = imageSave.getId();
        this.survey_id = survey.getForm_id();
        this.colector_id = AppSession.getInstance().getUser().getColector_id();
        this.name = context.getString(R.string.image_name_format,
                NetworkUtils.getAndroidID(context),
                imageSave.getValue().substring((slashIndex + 1), lastIndex));
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

    public static ArrayList<IdValue> getFileSurveys(List<IdValue> answers) {
        ArrayList<IdValue> answerWithImages = new ArrayList<>();
        for (IdValue answer : answers) {
            switch (answer.getmType()) {
                case 6:
                case 14:
                case 16:
                    if (!answer.getValue().equals("")) answerWithImages.add(answer);
                    break;
                default:
                    break;
            }
        }
        return answerWithImages;
    }
}