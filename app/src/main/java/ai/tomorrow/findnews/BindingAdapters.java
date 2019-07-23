package ai.tomorrow.findnews;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import ai.tomorrow.findnews.database.entity.Article;
import ai.tomorrow.findnews.searchnews.NewsGridAdapter;

public class BindingAdapters{

    private static String TAG = BindingAdapters.class.getSimpleName();
    private static String BASE_URL = "http://www.nytimes.com/";

    @BindingAdapter("listData")
    public static void bindRecyclerView(RecyclerView recyclerView, List<Article> data){
        NewsGridAdapter adapter = (NewsGridAdapter) recyclerView.getAdapter();
        adapter.submitList(data);
    }

    @BindingAdapter("imageUrl")
    public static void bindImage(ImageView imageView, String imgUrl){
//        Uri imgUri = Uri.parse(BASE_URL + imgUrl).buildUpon().scheme("https").build();
//        Log.d(TAG, "imgUrl = " + imgUrl);
        if (imgUrl == null){
            imageView.setVisibility(View.GONE);
        } else {
//            Glide.with(imageView.getContext())
//                    .load(BASE_URL + imgUrl)
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.loading_animation)
//                            .error(R.drawable.ic_broken_image))
//                    .into(imageView);
            Glide.with(imageView.getContext())
                    .load(BASE_URL + imgUrl)

                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }


}

