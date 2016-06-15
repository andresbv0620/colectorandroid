package colector.co.com.collector.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.rey.material.widget.CheckBox;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;
import colector.co.com.collector.model.Question;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */
public class MultipleItemView extends ScrollView {

    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.toggle)
    CheckBox toggle;

    public MultipleItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.multiple_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(String name){
        textView.setText(name);
    }
}
