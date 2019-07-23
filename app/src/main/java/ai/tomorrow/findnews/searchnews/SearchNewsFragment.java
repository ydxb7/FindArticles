package ai.tomorrow.findnews.searchnews;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.database.entity.Article;
import ai.tomorrow.findnews.databinding.FragmentSearchNewsBinding;
import ai.tomorrow.findnews.util.EndlessRecyclerViewScrollListener;
import io.realm.RealmResults;

public class SearchNewsFragment extends Fragment {

    private String TAG = SearchNewsFragment.class.getSimpleName();
    private FragmentSearchNewsBinding mBinding;

    private NewsGridAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, viewGroup, false);
//        FragmentSearchNewsBinding binding = FragmentSearchNewsBinding.inflate(getLayoutInflater(), viewGroup, false);
        mBinding.setLifecycleOwner(this);
//        SearchNewsViewModel viewModel = ViewModelProviders.of(this).get(SearchNewsViewModel.class);

        SearchNewsViewModel.Factory factory = new SearchNewsViewModel.Factory(
                getActivity().getApplication());

        final SearchNewsViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(SearchNewsViewModel.class);

        mBinding.setSearchViewModel(viewModel);

        mRecyclerView = (RecyclerView) mBinding.newsGrid;

        mRecyclerView.setHasFixedSize(true);


        mAdapter = new NewsGridAdapter();
        mRecyclerView.setAdapter(mAdapter);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG, "viewModel.getArticles().getValue().size() = " + viewModel.getArticles().getValue().size());
                Log.d(TAG, "page = " + page);
                viewModel.fetchArticle(page);
                Log.d(TAG, "viewModel.getArticles().getValue().size() = " + viewModel.getArticles().getValue().size());
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        viewModel.getArticles().observe(this, new Observer<RealmResults<Article>>() {
            @Override
            public void onChanged(RealmResults<Article> articles) {
                mAdapter.setArticles(articles);
            }
        });



        return mBinding.getRoot();

    }






}
