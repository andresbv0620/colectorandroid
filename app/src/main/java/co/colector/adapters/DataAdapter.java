package co.colector.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.R;
import co.colector.helpers.PreferencesManager;

/**
 * Created by danielsierraf on 6/27/16.
 */
public class DataAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> opciones;
    private List<String> filteredData;
    private List<String> mSelected = new ArrayList<>();
    private String valuesSelected;

    public DataAdapter(Context context, int resource, List<String> objects, String valuesSelected) {
        super(context, resource, objects);
        opciones = objects;
        filteredData = objects;
        this.valuesSelected = valuesSelected;
    }

    public List<String> getItemsSelected() {
        return mSelected;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                //If there's nothing to filter on, return the original data for your list
                if(charSequence == null || charSequence.length() == 0)
                {
                    results.values = opciones;
                    results.count = opciones.size();
                }
                else
                {
                    ArrayList<String> filterResultsData = new ArrayList<>();
                    charSequence = charSequence.toString().toLowerCase();
                    for(String data : opciones)
                    {
                        if (data.toLowerCase().contains(charSequence))
                            filterResultsData.add(data);
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                filteredData = (ArrayList<String>)filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public String getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row = convertView;

        if (row == null || !( row.getTag() instanceof ViewHolder)) {
            LayoutInflater mInflater = LayoutInflater.from(getContext());
            row = mInflater.inflate(R.layout.card_row, parent, false);
            // Creates a ViewHolder and store references to the children views we want to bind data to.
            holder = new ViewHolder(row);
        } else {
            // Get the ViewHolder back
            holder = (ViewHolder) row.getTag();
        }
        holder.textView.setText(filteredData.get(position));

        if (mSelected.contains(filteredData.get(position)))
            holder.checkBox.setChecked(true);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) mSelected.add(filteredData.get(position));
                else mSelected.remove(filteredData.get(position));
            }
        });

        if (!PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.OPTIONS_SELECTEDS,"").isEmpty()){
            if (PreferencesManager.getInstance().getPrefs().getString(PreferencesManager.OPTIONS_SELECTEDS,"").contains(filteredData.get(position))){
                holder.checkBox.setChecked(true);
            }
        }

        if (valuesSelected != null && !valuesSelected.isEmpty()){
            if (valuesSelected.contains(filteredData.get(position))){
                  holder.checkBox.setChecked(true);
            }
        }

        return row;
    }



    public class ViewHolder {
        @BindView(R.id.chk_dialog)
        CheckBox checkBox;
        @BindView(R.id.txt_dialog)
        TextView textView;
        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
