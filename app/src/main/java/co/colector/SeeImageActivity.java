package co.colector;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Jose Rodriguez on 19/08/2016.
 */
public class SeeImageActivity extends AppCompatActivity {

    private String url;
    @BindView(R.id.mainImageView)
    ImageView photo;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_image);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("uriImage");
        ColectorApplication.getInstance().getGlideInstance().load(url).asBitmap().centerCrop().into(
            new BitmapImageViewTarget(photo) {
            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                photo.setImageBitmap(resource);
            }
        });
        mAttacher = new PhotoViewAttacher(photo);
        mAttacher.update();
    }
}
