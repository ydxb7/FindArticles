package ai.tomorrow.findnews.SearchNews;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.tomorrow.findnews.Model.Article;
import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSearchNewsBinding;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

public class SearchNewsFragment extends Fragment {

    private String TAG = SearchNewsFragment.class.getSimpleName();

    private NewsGridAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Realm realm;
    private RealmResults<Article> articles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {

        FragmentSearchNewsBinding binding = FragmentSearchNewsBinding.inflate(getLayoutInflater(), viewGroup, false);

        mRecyclerView = (RecyclerView) binding.newsGrid;

        mRecyclerView.setHasFixedSize(true);

        Realm.deleteRealm(Realm.getDefaultConfiguration());
        realm = Realm.getDefaultInstance();

        mAdapter = new NewsGridAdapter();
        mRecyclerView.setAdapter(mAdapter);
        fetchArticle(1);

        return binding.getRoot();

    }


    private void fetchArticle(int page){
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        String key = "iyRiXtZ9sd78MbN2h6E20udA2NQwamal";
        RequestParams params = new RequestParams();
        params.put("api-key", key);
        params.put("page", page);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    Article.insertArticlesIntoDatabse(articleJsonResults, realm);
                    articles = realm.where(Article.class).findAll();
                    mAdapter.submitList(articles);
//                    newArticles = Article.fromJSONArray(articleJsonResults);


                } catch (JSONException e) {
                    Log.d(TAG, "Error int getting response or docs.", e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

}
