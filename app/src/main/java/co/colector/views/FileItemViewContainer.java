package co.colector.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.listeners.OnAddFileListener;
import co.colector.model.IdValue;
import co.colector.model.Question;
import io.realm.RealmList;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class FileItemViewContainer extends LinearLayout {

    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.photo_container)
    LinearLayout photoContainer;
    public Long id;
    private String validation;
    private OnAddFileListener internalCallback;
    private boolean required;
    private int mType;
    public final static int ERROR_PATH = 0x00;
    private final static int IMAGE_PATH = 0x01;
    private final static int PDF_PATH = 0x02;

    public FileItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    /**
     * Bind the basic information of the view
     * @param question to be inflate
     * @param callback to notify abut click actions
     * @param previewDefault value
     */
    public void bind(Question question, OnAddFileListener callback, @Nullable List<String> previewDefault) {
        this.internalCallback = callback;
        id = question.getId();
        required = question.getRequerido();
        mType = question.getType();
        validation = question.getValidacion();
        label.setText(question.getName());
        button.setText(question.getName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                internalCallback.onAddFileClicked(FileItemViewContainer.this);
            }
        });
        if (previewDefault != null) for (String url : previewDefault) {
            if (!url.equals("")) addImagesFile(url);
        }
    }

    /**
     * Add image reference to the url file
     * @param url of file
     */
    public void addImagesFile(String url) {
        final PhotoItemView photoItemView = new PhotoItemView(getContext());
        if (getExtension(url) == PDF_PATH) photoItemView.bind(getFileName(url), url);
        else photoItemView.bind(url);
        photoItemView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                internalCallback.onFileClicked(photoContainer, (PhotoItemView) v);
            }
        });
        photoContainer.addView(photoItemView);
    }

    /**
     * Get url responses
     * @return List of url responses
     */
    public RealmList<IdValue> getResponses() {
        RealmList<IdValue> responses = new RealmList<>();
        if (photoContainer.getChildCount() > 0)
            for (int itemViewIndex = 0; itemViewIndex < photoContainer.getChildCount(); itemViewIndex++)
                responses.add(new IdValue(id, ((PhotoItemView) photoContainer.getChildAt(itemViewIndex)).url,
                        validation, mType));
        else responses.add(new IdValue(id, "", validation, mType));
        return responses;
    }

    /**
     * Validate Fields of the question if it is necessary
     *
     * @return true if it is valid
     */
    public boolean validateFields() {
        if (!required) return true;
        if (photoContainer.getChildCount() > 0) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            return true;
        }
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
        return false;
    }

    /**
     * Get file extension to know how to inflate it content
     *
     * @param url file
     * @return validation code
     */
    public int getExtension(String url) {
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension.equals("jpg") || extension.equals("png")) return IMAGE_PATH;
        if (extension.equals("pdf")) return PDF_PATH;
        return ERROR_PATH;
    }

    /**
     * Get the file Name without slash or dot
     *
     * @param url path
     * @return file name
     */
    private String getFileName(String url) {
        int dotIndex = url.lastIndexOf(".");
        int slashIndex = url.lastIndexOf("/");
        return url.substring((slashIndex + 1), dotIndex);
    }

}
