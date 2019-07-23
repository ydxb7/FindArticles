package ai.tomorrow.findnews;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ai.tomorrow.findnews.database.entity.Article;
import ai.tomorrow.findnews.searchnews.NewsGridAdapter;

public class BindingAdapters{

    @BindingAdapter("listData")
    public static void bindRecyclerView(RecyclerView recyclerView, List<Article> data){
        NewsGridAdapter adapter = (NewsGridAdapter) recyclerView.getAdapter();
        adapter.submitList(data);

    }
}

