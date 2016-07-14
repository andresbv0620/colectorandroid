package co.colector.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.colector.R;
import co.colector.model.IdOptionValue;

/**
 * Created by dherrera on 11/10/15.
 */
public class SurveyAdapterOptionalType extends ArrayAdapter<IdOptionValue> {

    private Context context;
    private List<IdOptionValue> items;

    public SurveyAdapterOptionalType(Context context, ArrayList<IdOptionValue> items) {
        super(context, R.layout.adapter_survey_options,items);
        this.context = context;
        this.items = items;
    }


    @Override
    public View getDropDownView(int position, View convertView,ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        IdOptionValue item = this.items.get(position);

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.adapter_survey_options, parent, false);

        TextView row_name = (TextView) row.findViewById(R.id.adapter_survey_item_option);
        row_name.setText(item.getValue());
        row_name.setTextColor(ContextCompat.getColor(this.context, R.color.text_color));

        row.setTag(item.getId());

        return row;
    }

}
