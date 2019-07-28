package ai.tomorrow.findarticles.searchnews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.databinding.GridViewItemBinding;
import ai.tomorrow.findarticles.databinding.GridViewItemNoImageBinding;

public class NewsGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        RecyclerView.Adapter<NewsGridAdapter.NewsViewHolder>{

    private static final String TAG = NewsGridAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    private List<? extends Article> articles;
    private List<? extends Article> articlesFull;

////    // TODO (3) Create a final private ListItemClickListener called mOnClickListener
    private final ItemClickListener mOnClickListener;

//    private static DiffUtil.ItemCallback<Article> diffCallback = new DiffUtil.ItemCallback<Article>() {
//
//        @Override
//        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
//            return oldItem == newItem;
//        }
//
//        @Override
//        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
//            return oldItem.getId().equals(newItem.getId());
//        }
//    };

    // TODO (1) Add an interface called ListItemClickListener
    // TODO (2) Within that interface, define a void method called onListItemClick that takes an int as a parameter
    public interface ItemClickListener{
        void onListItemClick(Article article);
    }


    public NewsGridAdapter(ItemClickListener listener){
        mOnClickListener = listener;
//        super(diffCallback);
    }

    public void setArticles(List<Article> articles){
        if (this.articles == null){
            this.articles = articles;
            this.articlesFull = new ArrayList<>(articles);
            notifyDataSetChanged();
        } else if (articles.size() == 0){
            notifyDataSetChanged();
        } else {
            int oldSize = this.articles.size();
            this.articles = articles;
            this.articlesFull = new ArrayList<>(articles);
            notifyItemInserted(oldSize);
        }

    }

    /**
     * Returns an integer code related to the type of View we want the ViewHolder to be at a given
     * position. This method is useful when we want to use different layouts for different items
     * depending on their position. In Sunshine, we take advantage of this method to provide a
     * different layout for the "today" layout. The "today" layout is only shown in portrait mode
     * with the first item in the list.
     *
     * @param position index within our RecyclerView and Cursor
     * @return the view type (today or future day)
     */
    @Override
    public int getItemViewType(int position) {
        Article article = this.articles.get(position);
        if (article.getThumbnail() == null){
            return VIEW_TYPE_NO_IMAGE;
        } else {
            return VIEW_TYPE_WITH_IMAGE;
        }
    }

    @Override
    public int getItemCount() {
        return articles == null ? 0 : articles.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType){
            case VIEW_TYPE_WITH_IMAGE:
                return new NewsViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.grid_view_item, viewGroup, false));
            case VIEW_TYPE_NO_IMAGE:
                return new NewsViewHolderNoImage(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.grid_view_item_no_image, viewGroup, false));
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Article article = this.articles.get(position);
        int viewType = getItemViewType(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onListItemClick(article);
            }
        });
        switch (viewType){
            case VIEW_TYPE_WITH_IMAGE:
                ((NewsViewHolder)holder).bind(article);
                break;
            case VIEW_TYPE_NO_IMAGE:
                ((NewsViewHolderNoImage)holder).bind(article);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }


    class NewsViewHolder extends RecyclerView.ViewHolder {

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

    class NewsViewHolderNoImage extends RecyclerView.ViewHolder{

        GridViewItemNoImageBinding mBinding;

        public NewsViewHolderNoImage(GridViewItemNoImageBinding binding){
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
