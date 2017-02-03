package co.colector.model;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.colector.ColectorApplication;
import co.colector.R;
import co.colector.session.AppSession;
import co.colector.utils.NetworkUtils;

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

    public ImageRequest(Survey survey, AnswerValue imageSave, long questionId) {
        Context context = ColectorApplication.getInstance();
        int dotIndex = imageSave.getValue().lastIndexOf(".");
        int slashIndex = imageSave.getValue().lastIndexOf("/");
        int lastIndex = imageSave.getValue().length();
        this.image = new File(imageSave.getValue());
        this.extension = imageSave.getValue().substring(dotIndex + 1);
        this.question_id = questionId;
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

    public static ArrayList<ImageRequest> getFileSurveys(Survey survey) {
        List<IdValue> answers = survey.getInstanceAnswers();
        ArrayList<ImageRequest> answerWithImages = new ArrayList<>();
        for (IdValue answer : answers) {
            switch (answer.getmType()) {
                case 6:
                case 14:
                case 16:
                    for (AnswerValue answerValue : answer.getValue())
                        if (!answerValue.getValue().equals(""))
                            answerWithImages.add(new ImageRequest(survey, answerValue, answer.getIdQuestion()));
                    break;
                default:
                    break;
            }
        }
        return answerWithImages;
    }
}