package colector.co.com.collector.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class PhotoItemViewContainer extends LinearLayout {

    @BindView(R.id.label)
    TextView label;
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.photo_container)
    LinearLayout photoContainer;

    public PhotoItemViewContainer(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item_view_container, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(String label) {
        this.label.setText(label);
        this.button.setText(label);
    }
}
