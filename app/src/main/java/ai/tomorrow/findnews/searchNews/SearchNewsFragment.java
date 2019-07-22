package ai.tomorrow.findnews.searchNews;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSearchNewsBinding;

public class SearchNewsFragment extends Fragment {

    private String TAG = SearchNewsFragment.class.getSimpleName();
    private FragmentSearchNewsBinding mBinding;

    private NewsGridAdapter mAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, viewGroup, false);
//        FragmentSearchNewsBinding binding = FragmentSearchNewsBinding.inflate(getLayoutInflater(), viewGroup, false);
//        mBinding.setLifecycleOwner(this);
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

        return mBinding.getRoot();

    }






}
