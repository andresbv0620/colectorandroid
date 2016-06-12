package colector.co.com.collector.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.model.ResponseComplex;
import colector.co.com.collector.model.ResponseItem;

/**
 * Created by dherrera on 11/10/15.
 */
public class OptionAdapter extends ArrayAdapter<ResponseComplex> {

    private Context context;
    private List<ResponseComplex> items;

    public OptionAdapter(Context context, List<ResponseComplex> items) {
        super(context, R.layout.adapter_options,items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ResponseComplex item = this.items.get(position);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_options, parent, false);

        LinearLayout linear = (LinearLayout) row.findViewById(R.id.adapter_option_item);
        linear.setTag(item.getRecord_id());

        for (ResponseItem data : item.getResponses()){

            TextView toInsert = new TextView(context);
            toInsert.setText(data.getLabel() + ": " + data.getValue());
            toInsert.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            linear.addView(toInsert);
        }

        return row;
    }




}
