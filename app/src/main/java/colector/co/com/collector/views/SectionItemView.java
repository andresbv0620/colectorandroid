package colector.co.com.collector.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.R;

/**
 * @author Gabriel Rodriuez
 * @version 1.0
 */
public class SectionItemView extends LinearLayout {

    @BindView(R.id.section_title)
    TextView title;
    @BindView(R.id.section_items_container)
    public LinearLayout sectionItemsContainer;

    public SectionItemView(Context context) {
        super(context);
        init(context);
    }

    public SectionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SectionItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    /**
     * Set the title of the section Item View
     * @param title of the section
     */
    public void bind(String title) {
        this.title.setText(title);
    }
}
