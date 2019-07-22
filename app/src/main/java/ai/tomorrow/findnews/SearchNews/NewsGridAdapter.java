package ai.tomorrow.findnews.SearchNews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.GridViewItemBinding;

public class NewsGridAdapter extends RecyclerView.Adapter<NewsGridAdapter.NewsViewHolder>{

    private static final String TAG = NewsGridAdapter.class.getSimpleName();

    private int mNumberItems;

    public NewsGridAdapter(int numberOfItems){
        mNumberItems = numberOfItems;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        return new NewsViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(viewGroup.getContext())));

    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumberItems;
    }



    class NewsViewHolder extends RecyclerView.ViewHolder{

        TextView newsTextView;

        public NewsViewHolder(GridViewItemBinding binding){
            super(binding.getRoot());

            newsTextView = binding.itemText;

        }

        void bind(int listIndex) {
            newsTextView.setText(String.valueOf(listIndex));
        }

    }


}
