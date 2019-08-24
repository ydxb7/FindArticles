package ai.tomorrow.findarticles.searcharticles;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.models.Article;
import ai.tomorrow.findarticles.util.DataLoadingStatus;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;

public class SearchArticlesViewModel extends ViewModel {

    private static String TAG = SearchArticlesViewModel.class.getSimpleName();

    private SharedPreferences mPreferences;

    // Preference key
    private String PREF_ARTS_KEY;
    private String PREF_FASHION_KEY;
    private String PREF_SPORTS_KEY;
    private String PREF_SORT_KEY;
    private String PREF_BEGIN_DATE_KEY;

    // Preference default value
    private Boolean PREF_ARTS_DEFAULT;
    private Boolean PREF_FASHION_DEFAULT;
    private Boolean PREF_SPORTS_DEFAULT;
    private String PREF_SORT_DEFAULT;
    private int PREF_BEGIN_DATE_DEFAULT;

    // All the preference values for sort type
    private String PREF_SORT_VALUE_NEWEST;
    private String PREF_SORT_VALUE_OLDEST;
    private String PREF_SORT_VALUE_ASCENDING;
    private String PREF_SORT_VALUE_DESCENDING;

    // Preference values we use
    private Boolean mArts;
    private Boolean mFashion;
    private Boolean mSports;
    private Boolean mSort;
    private int mBgindate;
    private String mQuery = "";

    private Context mContext;

    private Realm mRealm = Realm.getDefaultInstance();

    // LiveData realm results
    private ArrayList<Article> articles = new ArrayList<>();

    public List<Article> getArticles() {
        return articles;
    }

    // The article to pass to the detail fragment
    private MutableLiveData<Article> navigateToSelectedArticle = new MutableLiveData<>();

    public LiveData<Article> getNavigateToSelectedArticle() {
        return navigateToSelectedArticle;
    }

    // The loading indicator status
    public MutableLiveData<DataLoadingStatus> mStatus = new MutableLiveData<>();

    // is first time articles finish loaded
    private MutableLiveData<Boolean> isFinishLoading = new MutableLiveData<>();

    public LiveData<Boolean> getIsFinishLoading() {
        return isFinishLoading;
    }

    // Initialize the adapter, and set the click listener. When clicked on the item, change the
    // navigateToSelectedArticle value in the viewModel and navigate to the detailFragment
    public ArticlesGridAdapter mAdapter = new ArticlesGridAdapter(new ArticlesGridAdapter.ItemClickListener() {
        @Override
        public void onListItemClick(Article article) {
            displayArticleDetails(article);
        }
    });

    public SearchArticlesViewModel(@NonNull Context context) {

        mContext = context;

        // Get the preference values
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        PREF_ARTS_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_arts_default);
        PREF_FASHION_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_fashion_default);
        PREF_SPORTS_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_sports_default);
        PREF_SORT_DEFAULT = mContext.getResources().getString(R.string.pref_sort_newest);
        PREF_BEGIN_DATE_DEFAULT = mContext.getResources().getInteger(R.integer.pref_begin_date_default);

        PREF_ARTS_KEY = mContext.getResources().getString(R.string.pref_arts_key);
        PREF_FASHION_KEY = mContext.getResources().getString(R.string.pref_fashion_key);
        PREF_SPORTS_KEY = mContext.getResources().getString(R.string.pref_sports_key);
        PREF_SORT_KEY = mContext.getResources().getString(R.string.pref_sort_key);
        PREF_BEGIN_DATE_KEY = mContext.getResources().getString(R.string.pref_begin_date_key);

        PREF_SORT_VALUE_NEWEST = mContext.getResources().getString(R.string.pref_sort_newest);
        PREF_SORT_VALUE_OLDEST = mContext.getResources().getString(R.string.pref_sort_oldest);
        PREF_SORT_VALUE_ASCENDING = mContext.getResources().getString(R.string.pref_sort_ascending);
        PREF_SORT_VALUE_DESCENDING = mContext.getResources().getString(R.string.pref_sort_descending);

        mArts = mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT);
        mFashion = mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT);
        mSports = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
        mSort = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
        mBgindate = mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);

        if (!isConnected()) {
            // articles are got from database
            mStatus.setValue(DataLoadingStatus.LOADING);
            articles.addAll(mRealm.where(Article.class).findAll());
            mAdapter.setArticles(articles);
            mStatus.setValue(DataLoadingStatus.DONE);
        } else {
            // Set the DataLoadingStatus to LOADING
            mStatus.setValue(DataLoadingStatus.LOADING);
            isFinishLoading.setValue(false);
            // remove data in the database
            mRealm.executeTransaction(realm -> realm.deleteAll());
            // fetch the data online and insert into the database
            fetchArticle(0);
        }
    }


    public void fetchArticle(int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        String key = "iyRiXtZ9sd78MbN2h6E20udA2NQwamal";

        // Set the parameters for the url
        RequestParams params = new RequestParams();
        params.put("api-key", key);
        params.put("page", page);

        // Get query preference and put them into the query params
        boolean myArts = mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT);
        boolean myFashion = mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT);
        boolean mySports = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
        String mySort = mPreferences.getString(PREF_SORT_KEY, PREF_SORT_VALUE_NEWEST);
        int myBgindate = mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);

        String deskValues = "";

        if (myArts) {
            deskValues += "\"Arts\" ";
        }

        if (myFashion) {
            deskValues += "\"Fashion\" ";
        }

        if (mySports) {
            deskValues += "\"Sports\"";
        }

        if (!deskValues.isEmpty()) {
            params.put("fq", "news_desk:(" + deskValues + ")");
        }

        // params from SearchView
        if (!mQuery.isEmpty()) {
            params.put("q", mQuery);
        }

        if (mySort.equals(PREF_SORT_VALUE_NEWEST) || mySort.equals(PREF_SORT_VALUE_OLDEST)) {
            params.put("sort", mySort);
        } else if (mySort.equals(PREF_SORT_VALUE_ASCENDING)) {
            params.put("sort", "asc");
        } else if (mySort.equals(PREF_SORT_VALUE_DESCENDING)) {
            params.put("sort", "desc");
        }

        if (myBgindate != 0) {
            params.put("begin_date", String.valueOf(myBgindate));
        }

        // Get the data online
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Parse the Json data from the web to ArticleList
                    JSONArray articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    RealmList<Article> articleRealmList = Article.parseJsonIntoArticleList(articleJsonResults);
                    articles.addAll(articleRealmList);
                    mAdapter.setArticles(articles);

                    mRealm.executeTransaction(realm -> realm.copyToRealmOrUpdate(articleRealmList));

                    // Insert the Article List into the database
                    try {
                        // Open a transaction to store items into the realm
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(articleRealmList);
                        isFinishLoading.setValue(true);
                        mRealm.commitTransaction();
                    } catch (Exception e) {
                        if (mRealm.isInTransaction()) {
                            mRealm.cancelTransaction();
                        }
                        throw new RuntimeException(e);
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "Error int getting response or docs.", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                mStatus.setValue(DataLoadingStatus.ERROR);
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    // SearchView
    public SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {
        // When the text in the query of SearchView is finished
        @Override
        public boolean onQueryTextSubmit(String query) {
            mQuery = query;
            updateSearch();
            return true;
        }

        // When we still typing the query text, we do nothing, because if we send to much query to
        // the server, it will throw exception.
        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    // Check the preference is changed
    public boolean isSearchChanged() {
        if (mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT) != mArts ||
                mFashion != mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT) ||
                mSports != mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT) ||
                mBgindate != mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT)) {
            mArts = mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT);
            mFashion = mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT);
            mSports = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
            mBgindate = mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);
            return true;
        }
        return false;
    }

    // Update the search
    public void updateSearch() {
        // Set the loading status to LOADING
        mStatus.setValue(DataLoadingStatus.LOADING);
        // Reset the isFinishLoading because there is no articles to show
        isFinishLoading.setValue(false);
        // Delete all the articles in the database
        mRealm.executeTransaction(realm -> realm.deleteAll());
        // remove all the articles in the adapter
        articles.clear();
        mAdapter.setArticles(articles);
        // Fetch the article online and insert them into the database and show them
        fetchArticle(0);
    }

    // Refresh the data by swipeRefreshLayout
    public void swipRefresh() {
        // there is no data to show
        isFinishLoading.setValue(false);
        // delete all data in the database
        mRealm.executeTransaction(realm -> realm.deleteAll());
        // remove all the articles in the adapter
        articles.clear();
        mAdapter.setArticles(articles);
        fetchArticle(0);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRealm.close();
    }

    // Set the article to passed to the detail fragment
    public void displayArticleDetails(Article article) {
        navigateToSelectedArticle.setValue(article);
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    // After navigate to detail fragment, reset its state
    public void displayArticleDetailsComplete() {
        navigateToSelectedArticle.setValue(null);
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Context mContext;

        public Factory(@NonNull Context context) {
            mContext = context;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SearchArticlesViewModel(mContext);
        }
    }
}
