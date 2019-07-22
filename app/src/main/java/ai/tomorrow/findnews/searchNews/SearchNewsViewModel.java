package ai.tomorrow.findnews.searchNews;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.tomorrow.findnews.model.Article;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;

public class SearchNewsViewModel extends AndroidViewModel {

    private static String TAG = SearchNewsViewModel.class.getSimpleName();

    private Realm realm;
//    private RealmResults<Article> articles;
    public MutableLiveData<RealmResults<Article>> articles = new MutableLiveData<RealmResults<Article>>();


    public SearchNewsViewModel(@NonNull Application application) {
        super(application);
        Realm.deleteRealm(Realm.getDefaultConfiguration());
        realm = Realm.getDefaultInstance();
        fetchArticle(1);
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
                    articles.setValue(realm.where(Article.class).findAll());
//                    mAdapter.submitList(articles);


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
    protected void onCleared() {
        super.onCleared();
        realm.close();
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Application mApplication;

        public Factory(@NonNull Application application) {
            mApplication = application;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SearchNewsViewModel(mApplication);
        }
    }
}
