package colector.co.com.collector.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;

/**
 * Created by tavo on 24/10/15.
 */
public class SurveyAdapterMultipleType extends ArrayAdapter<IdOptionValue> {

    private List<IdOptionValue>items;

    public SurveyAdapterMultipleType(Context context, ArrayList<IdOptionValue> items) {
        super(context, R.layout.list_check_survey, items);
        this.items = items ;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        convertView = inflater.inflate(R.layout.list_check_survey, parent, false);

        IdOptionValue item = this.items.get(position);

        TextView txt = (TextView) convertView.findViewById(R.id.label);
        txt.setText(item.getValue().toString());

        CheckBox checkbox = (CheckBox) convertView.findViewById(R.id.check);
        checkbox.setTag(item);

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                IdOptionValue select = (IdOptionValue) buttonView.getTag();
                select.setStatus(isChecked);

            }
        });
        checkbox.setChecked(item.isStatus());

        return convertView;
    }

    public List<IdOptionValue> getItems() {
        return items;
    }

    public List<IdOptionValue> getTrueStatusItems() {
        List<IdOptionValue> toRet = new ArrayList<IdOptionValue>();
        for (IdOptionValue kvs : this.items) {
            if (kvs.isStatus()) {
                toRet.add(kvs);
            }
        }
        return toRet;
    }

    public List<IdOptionValue> getFalseStatusItems() {
        List<IdOptionValue> toRet = new ArrayList<IdOptionValue>();
        for (IdOptionValue kvs : this.items) {
            if (!kvs.isStatus()) {
                toRet.add(kvs);
            }
        }
        return toRet;
    }

    public void setStatusByValue(String value, boolean status) {
        for (IdOptionValue kvs : this.items) {
            if (kvs.getValue().equals(value)) {
                kvs.setStatus(status);
                break;
            }
        }
    }

    public void setStatusById(@Nullable Long id, boolean status) {
        if(id == null) return;
        for (IdOptionValue kvs : this.items) {
            if (kvs.getId().equals(id)) {
                kvs.setStatus(status);
                break;
            }
        }
    }

    public void setFalseItems() {
        for (IdOptionValue kvs : this.items) {
            kvs.setStatus(false);
        }
    }

}
