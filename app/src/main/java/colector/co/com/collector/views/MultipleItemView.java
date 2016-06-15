package colector.co.com.collector.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class MultipleItemView extends ScrollView {

    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.toggle)
    CheckBox toggle;

    public IdOptionValue option;

    public MultipleItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(IdOptionValue optionValue) {
        this.option = optionValue;
        textView.setText(optionValue.getValue());
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) option.setStatus(true);
                else option.setStatus(false);
            }
        });
    }

}
