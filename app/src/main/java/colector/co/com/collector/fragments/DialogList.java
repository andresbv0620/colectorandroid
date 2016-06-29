package colector.co.com.collector.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import colector.co.com.collector.R;
import colector.co.com.collector.model.IdOptionValue;

/**
 * Created by danielsierraf on 6/25/16.
 */
public class DialogList extends DialogFragment {

    private static Activity activity;
    private static List<IdOptionValue> items;
    private static String titulo;
    private ListView listitem;
    private SearchView searchBar;
    private ListSelectorDialogListener listener;

    public static DialogList newInstance(Activity context, String title, ArrayList<IdOptionValue> itms) {
        activity = context;
        items = itms;
        titulo = title;

        Bundle args = new Bundle();
        
        DialogList fragment = new DialogList();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListDialogListener(ListSelectorDialogListener listen) {
        listener = listen;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(titulo);
        builder.setCancelable(true);
        View toplist = activity.getLayoutInflater().inflate(R.layout.listdialog, null);
        searchBar = (SearchView) toplist.findViewById(R.id.search_bar);
        listitem = (ListView) toplist.findViewById(R.id.list_item_dialog);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity,
                R.layout.itemdialog, R.id.textOpcion, populateDialog());

        listitem.setAdapter(arrayAdapter);
        listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                listener.setItemSelected(arrayAdapter.getItem(i));
                dismiss();
            }
        });

        searchBar.setQueryHint(getString(R.string.buscar));
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        builder.setView(toplist);

        return builder.create();
    }

    public List<String> populateDialog(){
        ArrayList<String> list = new ArrayList<>();
        for (IdOptionValue item: items)
            list.add(item.getValue());
        return list;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public interface ListSelectorDialogListener {
        void setItemSelected(String item);
    }
}
