package ai.tomorrow.findarticles;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ai.tomorrow.findarticles.util.DataLoadingStatus;

public class BindingAdapters {

    private static String TAG = BindingAdapters.class.getSimpleName();
    private static String BASE_URL = "http://www.nytimes.com/";

    @BindingAdapter("imageUrl")
    public static void bindImage(ImageView imageView, String imgUrl) {
        if (imgUrl == null) {
            imageView.setVisibility(View.GONE);
        } else {
            Glide.with(imageView.getContext())
                    .load(BASE_URL + imgUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    @BindingAdapter("dataLoadStatus")
    public static void bindStatus(ImageView statusImageView, DataLoadingStatus status) {
        switch (status) {
            case LOADING:
                statusImageView.setVisibility(View.VISIBLE);
                statusImageView.setImageResource(R.drawable.loading_animation);
                break;

            case ERROR:
                statusImageView.setVisibility(View.VISIBLE);
                statusImageView.setImageResource(R.drawable.ic_connection_error);
                break;

            case DONE:
                statusImageView.setVisibility(View.GONE);
                break;

            case EMPTY:
                statusImageView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("dataEmptyStatus")
    public static void bindEmptyStatus(TextView statusTextView, DataLoadingStatus status) {
        if (status == DataLoadingStatus.EMPTY) {
            statusTextView.setVisibility(View.VISIBLE);
        } else {
            statusTextView.setVisibility(View.GONE);
        }
    }
}

