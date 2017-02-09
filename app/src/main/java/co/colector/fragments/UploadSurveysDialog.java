package co.colector.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import co.colector.R;

/**
 * Created by ma0 on 2/7/17.
 */

public class UploadSurveysDialog extends DialogFragment {

    protected String TAG;

    protected Activity baseActivity;

    View view;

    TextView percentage;
    ProgressBar progressBar;

    int percetageValue;

    public UploadSurveysDialog()
    {
        TAG = this.getClass().getSimpleName();
        percetageValue = 0;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.baseActivity = (Activity) activity;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.upload_surveys, container, false);
        percentage = (TextView)view.findViewById(R.id.upload_surveys_text);
        progressBar = (ProgressBar)view.findViewById(R.id.upload_surveys_progress);
        return view;
    }

    public void setPercetageValue(int percetageValue) {
        this.percetageValue = percetageValue;
        progressBar.setProgress(percetageValue);
        percentage.setText(percetageValue+"%");
    }
}
