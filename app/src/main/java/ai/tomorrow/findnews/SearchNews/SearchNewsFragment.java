package ai.tomorrow.findnews.SearchNews;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSearchNewsBinding;

public class SearchNewsFragment extends Fragment {

    private NewsGridAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {

        FragmentSearchNewsBinding binding = FragmentSearchNewsBinding.inflate(getLayoutInflater(), viewGroup, false);

        mRecyclerView = (RecyclerView) binding.newsGrid;

        mRecyclerView.setHasFixedSize(true);

        mAdapter = new NewsGridAdapter(100);
        mRecyclerView.setAdapter(mAdapter);


        return binding.getRoot();

    }
}
