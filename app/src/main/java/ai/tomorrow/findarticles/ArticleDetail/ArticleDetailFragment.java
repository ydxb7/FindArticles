package ai.tomorrow.findarticles.ArticleDetail;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.parceler.Parcels;

import ai.tomorrow.findarticles.ArticleDetail.ArticleDetailFragmentArgs;
import ai.tomorrow.findarticles.MainActivity;
import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.databinding.FragmentArticleDetailBinding;


public class ArticleDetailFragment extends Fragment {

    private String TAG = ArticleDetailFragment.class.getSimpleName();
    private FragmentArticleDetailBinding mBinding;
    private Article mArticle;
    private WebView myWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_detail, container, false);
        mArticle = ArticleDetailFragmentArgs.fromBundle(getArguments()).getSelectedArticle();

        myWebView = mBinding.webview;

        setWebView();

        setHasOptionsMenu(true);

        return mBinding.getRoot();
    }

    private void setWebView() {
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Enabling JavaScript
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadsImagesAutomatically(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        // Load the page
        myWebView.loadUrl(mArticle.getWebUrl());

        myWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Check if the key event was the Back button and if there's history
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == event.KEYCODE_BACK){
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        // If it wasn't the Back key or there's no web page history, bubble up to the default
                        // system behavior (probably exit the activity)
                        getActivity().onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("https://www.nytimes.com")) {
                // This is my website, so do not override; let my WebView load the page
                myWebView.loadUrl(url);
                return true;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.article_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share){
            String textToShare = mArticle.getWebUrl();
            prepareShareIntent(textToShare);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Gets the article URL and setup the associated share intent to hook into the provider
    public void prepareShareIntent(String textToShare) {
        String mimeType = "text/plain";
        String title = "Share Article";

        ShareCompat.IntentBuilder.from(getActivity())
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser();

    }
}
