package ai.tomorrow.findarticles.searcharticles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.models.Article;
import ai.tomorrow.findarticles.databinding.GridViewItemBinding;
import ai.tomorrow.findarticles.databinding.GridViewItemNoImageBinding;

public class ArticlesGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//        RecyclerView.Adapter<ArticlesGridAdapter.ArticlesViewHolder>{

    private static final String TAG = ArticlesGridAdapter.class.getSimpleName();

    // Define 2 View types for the view with image and without image
    private static final int VIEW_TYPE_WITH_IMAGE = 0;
    private static final int VIEW_TYPE_NO_IMAGE = 1;

    private List<? extends Article> articles;

    // Create a final private ListItemClickListener called mOnClickListener, which will be initialized
    // from the Fragment where adapter is created.
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

    // Add an interface called ListItemClickListener
    // Within that interface, define a void method called onListItemClick that takes an int as a parameter
    public interface ItemClickListener{
        void onListItemClick(Article article);
    }


    public ArticlesGridAdapter(ItemClickListener listener){
        mOnClickListener = listener;
//        super(diffCallback);
    }

    public void setArticles(List<Article> articles){
        // When the search articles updated, we update the whole dataset
        if (this.articles == null){
            this.articles = articles;
            notifyDataSetChanged();
        } else if (articles.size() == 0){
            // clear the database
            notifyDataSetChanged();
        } else {
            // Only new articles are inserted into the articles, so only notifyItemInserted from the oldSize
            int oldSize = this.articles.size();
            this.articles = articles;
            notifyItemInserted(oldSize);
        }

    }

    // Get the view type for the Article according to whether there is image in Article
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

        // Inflater different layout from its view type
        switch (viewType){
            case VIEW_TYPE_WITH_IMAGE:
                return new ArticlesViewHolder(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.grid_view_item, viewGroup, false));
            case VIEW_TYPE_NO_IMAGE:
                return new ArticlesViewHolderNoImage(DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
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
                ((ArticlesViewHolder)holder).bind(article);
                break;
            case VIEW_TYPE_NO_IMAGE:
                ((ArticlesViewHolderNoImage)holder).bind(article);
                break;
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    // ViewHolder with image
    class ArticlesViewHolder extends RecyclerView.ViewHolder {

        GridViewItemBinding mBinding;

        public ArticlesViewHolder(GridViewItemBinding binding){
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

    // ViewHolder without image
    class ArticlesViewHolderNoImage extends RecyclerView.ViewHolder{

        GridViewItemNoImageBinding mBinding;

        public ArticlesViewHolderNoImage(GridViewItemNoImageBinding binding){
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
