package ai.tomorrow.findarticles.searcharticles;


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
import ai.tomorrow.findarticles.settings.SettingFragment;
import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.databinding.FragmentSearchNewsBinding;
import ai.tomorrow.findarticles.util.DataLoadingStatus;
import ai.tomorrow.findarticles.util.EndlessRecyclerViewScrollListener;
import io.realm.RealmResults;

public class SearchArticlesFragment extends Fragment {

    private String TAG = SearchArticlesFragment.class.getSimpleName();
    private FragmentSearchNewsBinding mBinding;
    private SearchArticlesViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // Adapter for GridLayout
    private ArticlesGridAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        // Inflater the layout
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, viewGroup, false);

        mBinding.setLifecycleOwner(this);

        SearchArticlesViewModel.Factory factory = new SearchArticlesViewModel.Factory(
                getActivity().getApplication());

        // Get the viewModel for this fragment
        mViewModel = ViewModelProviders.of(this, factory)
                .get(SearchArticlesViewModel.class);

        // Set the viewModel in the xml
        mBinding.setSearchViewModel(mViewModel);

        mRecyclerView = (RecyclerView) mBinding.newsGrid;

        // Get the swipeRefreshLayout
        mSwipeRefreshLayout = mBinding.swipeLayout;

        // Initialize the adapter, and set the click listener. When clicked on the item, change the
        // navigateToSelectedArticle value in the viewModel and navigate to the detailFragment
        mAdapter = new ArticlesGridAdapter(new ArticlesGridAdapter.ItemClickListener() {
            @Override
            public void onListItemClick(Article article) {
                mViewModel.displayArticleDetails(article);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // Set the StaggeredGridLayoutManager
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        // Set the EndlessRecyclerViewScrollListener to load the data endlessly
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                Log.d(TAG, "page = " + page);
                mViewModel.fetchArticle(page);
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

        // When the first page of the web query is loaded, then change the loading indicator status.
        mViewModel.getIsFinishLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFinishLoading) {
                if (isFinishLoading){
                    if (null == mViewModel.getArticles().getValue() || mViewModel.getArticles().getValue().isEmpty()){
                        // If the data is finish loading and it's empty, loading status to EMPTY
                        mViewModel.mStatus.setValue(DataLoadingStatus.EMPTY);
                    } else {
                        // If the data is finish loading, set the loading status to DONE
                        mViewModel.mStatus.setValue(DataLoadingStatus.DONE);
                    }
                    // Close the swipeRefresh loading indicator
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        // Navigate to DetailFragment and pass the article
        mViewModel.getNavigateToSelectedArticle().observe(this, new Observer<Article>() {
            @Override
            public void onChanged(Article article) {
                if (null != article){
                    Navigation.findNavController(getView()).navigate(SearchArticlesFragmentDirections
                            .actionSearchArticlesFragmentToArticleDetailFragment(article));
                    // Navigate to detail fragment complete
                    mViewModel.displayArticleDetailsComplete();
                }
            }
        });

        // SwipeRefreshLayout: set the color for the loading indicator for the SwipeRefreshLayout
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        // when the refresh setOnRefreshListener 刷新监听
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                // loading indicator start to rotate
                mSwipeRefreshLayout.setRefreshing(true);

                // Refresh the search result
                mViewModel.swipRefresh();
                // Reset the EndlessRecyclerViewScroll
                scrollListener.resetState();
            }
        });

        // Set the option menu
        setHasOptionsMenu(true);

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_articles_menu, menu);

        // Set the SearchView in the menu bar.
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        // Set the setOnQueryTextListener for SearchView
        searchView.setOnQueryTextListener(mViewModel.onQueryTextListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            // Open the setting fragment to change the preference
            SettingFragment settingFragment = new SettingFragment();
            settingFragment.show(getFragmentManager(), SettingFragment.class.getSimpleName());
            // When back from the setting fragment, search the result and refresh the recyclerView
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
