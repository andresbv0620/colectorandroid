package colector.co.com.collector.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import colector.co.com.collector.ColectorApplication;
import colector.co.com.collector.R;

/**
 * @author Gabriel Rodriguez
 * @version 1.0
 */

public class PhotoItemView extends FrameLayout {

    @BindView(R.id.photo)
    ImageView photo;
    private String url;

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
}
