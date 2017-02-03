package co.colector.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.bumptech.glide.load.resource.bitmap.ImageVideoBitmapDecoder;

import java.util.ArrayList;
import java.util.List;

import co.colector.R;
import co.colector.adapters.DataAdapter;
import co.colector.model.IdOptionValue;
import co.colector.model.Question;

/**
 * Created by danielsierraf on 6/25/16.
 */
public class DialogList extends DialogFragment {

    private static Activity activity;
    private static List<IdOptionValue> items;
    private static String titulo;
    private ListView listitem;
    private SearchView searchBar;

    private TextInputLayout otherTextLayout;
    private TextInputEditText otherEditText;
    private ImageButton buttonOk;

    private ListSelectorDialogListener listener;
    private ListMultipleSelectorListener listener_multiple;
    private static int type;
    private ArrayAdapter<String> arrayAdapter;
    public static Question question;
    public static String defaultValues;

    public static DialogList newInstance(Activity context, String title, ArrayList<IdOptionValue> itms,
                                         int type_dialog) {
        activity = context;
        items = itms;
        titulo = title;
        type = type_dialog;

        Bundle args = new Bundle();
        
        DialogList fragment = new DialogList();
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogList newInstance(Activity context, String title, ArrayList<IdOptionValue> itms,
                                         int type_dialog, String defaultValuesString) {
        activity = context;
        items = itms;
        titulo = title;
        type = type_dialog;
        defaultValues = defaultValuesString;

        Bundle args = new Bundle();

        DialogList fragment = new DialogList();
        fragment.setArguments(args);
        return fragment;
    }

    public static DialogList newInstance(Activity context, String title, ArrayList<IdOptionValue> itms,
                                         int type_dialog, Question questn) {
        activity = context;
        items = itms;
        titulo = title;
        type = type_dialog;
        question = questn;

        Bundle args = new Bundle();

        DialogList fragment = new DialogList();
        fragment.setArguments(args);
        return fragment;
    }

    public void setListDialogListener(ListSelectorDialogListener listen) {
        listener = listen;
    }

    public void setListener_multiple(ListMultipleSelectorListener listener_multiple) {
        this.listener_multiple = listener_multiple;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(titulo);
        builder.setCancelable(true);
        View toplist = activity.getLayoutInflater().inflate(R.layout.listdialog, null);
        searchBar = (SearchView) toplist.findViewById(R.id.search_bar);
        listitem = (ListView) toplist.findViewById(R.id.list_item_dialog);

        otherTextLayout = (TextInputLayout) toplist.findViewById(R.id.other_text_layout);
        otherEditText = (TextInputEditText) toplist.findViewById(R.id.other_edit_text);

        otherTextLayout.setHint(activity.getString(R.string.other_field));

        buttonOk = (ImageButton) toplist.findViewById(R.id.button_ok);

        List<String> options = populateDialog();

        if (type == 0)
        {
            // type == 0 Implica que es una pregunta de selección múltiple con única respuesta
            arrayAdapter = new ArrayAdapter<>(activity, R.layout.itemdialog, R.id.textOpcion, options);
            listitem.setAdapter(arrayAdapter);
            listitem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (listener != null) listener.setItemSelected(arrayAdapter.getItem(i));
                    dismiss();
                }
            });
            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) listener.setItemSelected(otherEditText.getText().toString());
                    dismiss();
                }
            });
        }
        else
        {
            // type != 1 (usualmente 1) Implica que es una pregunta de selección múltiple con múltiples respuestas
            arrayAdapter = new DataAdapter(activity, R.layout.itemdialog, options, defaultValues);
            listitem.setAdapter(arrayAdapter);
            // Set the action buttons
            builder.setPositiveButton(R.string.common_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK, so save the mSelectedItems results somewhere
                    // or return them to the component that opened the dialog
                    if (listener_multiple != null) addOtherToListResponses();
                    dismiss();
                }
            })
            .setNegativeButton(R.string.common_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });

            buttonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener_multiple != null) addOtherToListResponses();
                    dismiss();
                }
            });
        }

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
        {
            list.add(item.getValue());
        }
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

    public interface ListMultipleSelectorListener {
        void setItemsSelected(List<String> items, Question question);
    }

    private void addOtherToListResponses()
    {
        List<String> itemsSelected =((DataAdapter) arrayAdapter).getItemsSelected();
        if (!otherEditText.getText().toString().isEmpty())
        {
            itemsSelected.add(otherEditText.getText().toString());
        }
        listener_multiple.setItemsSelected(
                itemsSelected, question
        );
    }
}
