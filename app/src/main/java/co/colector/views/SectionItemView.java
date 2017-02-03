package co.colector.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;

/**
 * @author Gabriel Rodriuez
 * @version 1.0
 */
public class SectionItemView extends LinearLayout {

    @BindView(R.id.section_title)
    TextView title;
    @BindView(R.id.section_items_container)
    public LinearLayout sectionItemsContainer;

    // New Button for add the same section again
    @BindView(R.id.section_items_add_other)
    public Button sectionItemsAddOther;

    // Boolean to make aviable button to add other section
    boolean repetible;
    // Id for section
    int sectionCount;



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

    /**
     * Added for Add Section identifier to survey POST
     * Set the title of the section Item View
     * @param title of the section
     */
    public void bind(String title, int sectionCount) {
        this.title.setText(title);
        this.sectionCount = sectionCount;
    }

    // Add Other visibility
    public void addOtherSetVisibility(boolean visibility)
    {
        if (visibility)
        {
            sectionItemsAddOther.setVisibility(View.VISIBLE);
        }
        else
        {
            sectionItemsAddOther.setVisibility(View.GONE);
        }
    }


    public void setRepetible(boolean repetible)
    {
        this.repetible = repetible;
        addOtherSetVisibility(repetible);
    }

    public boolean isRepetible()
    {
        return repetible;
    }

    public void increaseSectionCount()
    {
        sectionCount += 1;
    }

    public int getSectionCount()
    {
        return this.sectionCount;

    }
}
