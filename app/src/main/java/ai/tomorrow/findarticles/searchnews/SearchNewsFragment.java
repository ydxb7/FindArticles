package ai.tomorrow.findarticles.searchnews;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.databinding.FragmentSearchNewsBinding;
import ai.tomorrow.findarticles.searchnews.SearchNewsFragmentDirections;
import ai.tomorrow.findarticles.searchnews.SearchNewsViewModel;
import ai.tomorrow.findarticles.settings.SettingFragment;
import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.util.DataLoadingStatus;
import ai.tomorrow.findarticles.util.EndlessRecyclerViewScrollListener;
import io.realm.RealmResults;

public class SearchNewsFragment extends Fragment {

    private String TAG = SearchNewsFragment.class.getSimpleName();
    private FragmentSearchNewsBinding mBinding;
    private SearchNewsViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ai.tomorrow.findarticles.searchnews.NewsGridAdapter mAdapter;
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

        mViewModel = ViewModelProviders.of(this, factory)
                .get(SearchNewsViewModel.class);

        mBinding.setSearchViewModel(mViewModel);

        mRecyclerView = (RecyclerView) mBinding.newsGrid;

        mRecyclerView.setHasFixedSize(true);

        mSwipeRefreshLayout = mBinding.swipeLayout;

        mAdapter = new ai.tomorrow.findarticles.searchnews.NewsGridAdapter(new ai.tomorrow.findarticles.searchnews.NewsGridAdapter.ItemClickListener() {
            @Override
            public void onListItemClick(Article article) {
                mViewModel.displayArticleDetails(article);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG, "viewModel.getArticles().getValue().size() = " + mViewModel.getArticles().getValue().size());
                Log.d(TAG, "page = " + page);
                mViewModel.fetchArticle(page);
                Log.d(TAG, "viewModel.getArticles().getValue().size() = " + mViewModel.getArticles().getValue().size());
            }
        };
        // Adds the scroll listener to RecyclerView
        mRecyclerView.addOnScrollListener(scrollListener);

        mViewModel.getArticles().observe(this, new Observer<RealmResults<Article>>() {
            @Override
            public void onChanged(RealmResults<Article> articles) {
                mAdapter.setArticles(articles);
            }
        });

        mViewModel.getIsFinishLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFinishLoading) {
                if (isFinishLoading){
                    if (null == mViewModel.getArticles().getValue() || mViewModel.getArticles().getValue().isEmpty()){
                        mViewModel.mStatus.setValue(DataLoadingStatus.EMPTY);
                    } else {
                        mViewModel.mStatus.setValue(DataLoadingStatus.DONE);
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    Log.d(TAG, "mSwipeRefreshLayout.setRefreshing(false);");
                }
            }
        });

        mViewModel.getNavigateToSelectedArticle().observe(this, new Observer<Article>() {
            @Override
            public void onChanged(Article article) {
                if (null != article){
                    Navigation.findNavController(getView()).navigate(SearchNewsFragmentDirections
                            .actionSearchNewsFragmentToArticleDetailFragment(article));
                    mViewModel.displayArticleDetailsComplete();
                }
            }
        });

        // set SwipeRefreshLayout
        // 设置转动颜色变化
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        // 刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // 开始转动
                mSwipeRefreshLayout.setRefreshing(true);

                mViewModel.swipRefresh();
                scrollListener.resetState();
            }
        });

        setHasOptionsMenu(true);
        return mBinding.getRoot();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_articles_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(mViewModel.onQueryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
//            Navigation.findNavController(getView()).navigate(R.id.action_searchNewsFragment_to_settingFragment);
            SettingFragment settingFragment = new SettingFragment();
            settingFragment.show(getFragmentManager(), SettingFragment.class.getSimpleName());
            settingFragment.setCallback(() -> {
                if (mViewModel.isSearchChanged()){
                    mViewModel.updateSearch();
                    scrollListener.resetState();
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }
}
