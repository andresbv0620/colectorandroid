package co.colector.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.colector.ColectorApplication;
import co.colector.R;

/**
 * @author Gabriel Rodriguez
 * @version 2.0
 */

public class PhotoItemView extends LinearLayout {

    @BindView(R.id.photo)
    ImageView photo;
    @BindView(R.id.file_name)
    TextView fileName;
    public String url;

    public PhotoItemView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item_view, this, true);
        ButterKnife.bind(this, view);
    }

    public void bind(String url) {
        this.url = url;
        ColectorApplication.getInstance().getGlideInstance().load(url).asBitmap().centerCrop().into(
                new BitmapImageViewTarget(photo) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        super.setResource(resource);
                        photo.setImageBitmap(resource);
                    }
                });
    }

    public void bind(String fileName, String url) {
        this.url = url;
        this.fileName.setVisibility(VISIBLE);
        this.fileName.setText(fileName);
        ColectorApplication.getInstance().getGlideInstance().load(R.drawable.ic_document)
                .centerCrop().into(photo);
    }
}
