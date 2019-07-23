package ai.tomorrow.findnews.searchnews;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ai.tomorrow.findnews.database.entity.Article;
import ai.tomorrow.findnews.databinding.GridViewItemBinding;

public class NewsGridAdapter extends ListAdapter<Article, NewsGridAdapter.NewsViewHolder> {
//        RecyclerView.Adapter<NewsGridAdapter.NewsViewHolder>{

    private static final String TAG = NewsGridAdapter.class.getSimpleName();


    private static DiffUtil.ItemCallback<Article> diffCallback = new DiffUtil.ItemCallback<Article>() {

        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    };


    public NewsGridAdapter(){
        super(diffCallback);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new NewsViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(viewGroup.getContext())));

    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Article article = getItem(position);
        holder.bind(article);
    }

//    @Override
//    public int getItemCount() {
//        return ;
//    }



    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView newsTextView;
        GridViewItemBinding mBinding;

        public NewsViewHolder(GridViewItemBinding binding){
            super(binding.getRoot());

            mBinding = binding;

        }

        void bind(Article article) {
            mBinding.setArticle(article);
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            mBinding.executePendingBindings();
        }

    }


}
