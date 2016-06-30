package colector.co.com.collector.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.listeners.OnAddSignatureListener;
import colector.co.com.collector.model.IdValue;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class SignatureItemViewContainer extends LinearLayout {

    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.signature_container)
    FrameLayout signatureContainer;
    public Long id;
    private String validation;
    private OnAddSignatureListener internalCallback;
    private boolean required;
    private int mType;

    public SignatureItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.signature_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(Question question, @Nullable String previewDefault, OnAddSignatureListener callback) {
        internalCallback = callback;
        id = question.getId();
        required = question.getRequerido();
        mType = question.getType();
        validation = question.getValidacion();
        label.setText(question.getName());
        button.setText(question.getName());
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                internalCallback.onAddSignatureClicked(SignatureItemViewContainer.this);
            }
        });
        if (previewDefault != null) {
            addSignature(previewDefault);
        }
    }

    public void addSignature(String url) {
        signatureContainer.removeAllViews();
        final SignatureItemView signatureItemView = new SignatureItemView(getContext());
        signatureItemView.bind(url);
        signatureContainer.addView(signatureItemView);
    }

    public IdValue getResponse() {
        if (signatureContainer.getChildCount() > 0)
            return new IdValue(id, ((SignatureItemView) signatureContainer.getChildAt(0)).url, validation, mType);
        else return new IdValue(id, "", validation, mType);


    }

    public boolean validateField() {
        if (!required) return true;
        if (signatureContainer.getChildCount() > 0) {
            label.setTextColor(ContextCompat.getColor(getContext(), R.color.text_color));
            return true;
        }
        label.setTextColor(ContextCompat.getColor(getContext(), R.color.red_label_error_color));
        return false;
    }

}
