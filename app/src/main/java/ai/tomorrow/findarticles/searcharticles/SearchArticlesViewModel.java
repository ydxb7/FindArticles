package ai.tomorrow.findarticles.searcharticles;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.AndroidViewModel;
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

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.database.dao.ArticleDao;
import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.util.DataLoadingStatus;
import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SearchArticlesViewModel extends AndroidViewModel {

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

    // The database
    private Realm realm;
    private ArticleDao dao;

    // LiveData realm results
    private LiveData<RealmResults<Article>> articles;

    public LiveData<RealmResults<Article>> getArticles() {
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

    public LiveData<Boolean> getIsFinishLoading(){
        return isFinishLoading;
    }


    public SearchArticlesViewModel(@NonNull Application application) {
        super(application);

        // Get the preference values
        mPreferences = PreferenceManager.getDefaultSharedPreferences(application);

        PREF_ARTS_DEFAULT = application.getResources().getBoolean(R.bool.pref_arts_default);
        PREF_FASHION_DEFAULT = application.getResources().getBoolean(R.bool.pref_fashion_default);
        PREF_SPORTS_DEFAULT = application.getResources().getBoolean(R.bool.pref_sports_default);
        PREF_SORT_DEFAULT = application.getResources().getString(R.string.pref_sort_newest);
        PREF_BEGIN_DATE_DEFAULT = application.getResources().getInteger(R.integer.pref_begin_date_default);

        PREF_ARTS_KEY = application.getResources().getString(R.string.pref_arts_key);
        PREF_FASHION_KEY = application.getResources().getString(R.string.pref_fashion_key);
        PREF_SPORTS_KEY = application.getResources().getString(R.string.pref_sports_key);
        PREF_SORT_KEY = application.getResources().getString(R.string.pref_sort_key);
        PREF_BEGIN_DATE_KEY = application.getResources().getString(R.string.pref_begin_date_key);

        PREF_SORT_VALUE_NEWEST = application.getResources().getString(R.string.pref_sort_newest);
        PREF_SORT_VALUE_OLDEST = application.getResources().getString(R.string.pref_sort_oldest);
        PREF_SORT_VALUE_ASCENDING = application.getResources().getString(R.string.pref_sort_ascending);
        PREF_SORT_VALUE_DESCENDING = application.getResources().getString(R.string.pref_sort_descending);

        mArts = mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT);
        mFashion = mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT);
        mSports = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
        mSort = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);
        mBgindate = mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);

        // Set the DataLoadingStatus to LOADING
        mStatus.setValue(DataLoadingStatus.LOADING);
        isFinishLoading.setValue(false);
        Realm.deleteRealm(Realm.getDefaultConfiguration());
        realm = Realm.getDefaultInstance();
        dao = new ArticleDao(realm);

        // articles are got from database
        articles = dao.findAll();
        // fetch the data online and insert into the database
        fetchArticle(0);
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
        if (!mQuery.isEmpty()){
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
                    RealmList<Article> articleRealmList = Article.parseJsonIntoArticleList(articleJsonResults, realm);

                    // Insert the Article List into the database
                    try {
                        // Open a transaction to store items into the realm
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(articleRealmList);
                        realm.commitTransaction();
                        isFinishLoading.setValue(true);
                    } catch (Exception e) {
                        if (realm.isInTransaction()) {
                            realm.cancelTransaction();
                        }
                        throw new RuntimeException(e);
                    }

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
        dao.deleteAll();
        // Fetch the article online and insert them into the database and show them
        fetchArticle(0);
    }

    // Refresh the data by swipeRefreshLayout
    public void swipRefresh(){
        isFinishLoading.setValue(false);
        dao.deleteAll();
        fetchArticle(0);
    }

    // Set the article to passed to the detail fragment
    public void displayArticleDetails(Article article) {
        navigateToSelectedArticle.setValue(article);
    }

    // After navigate to detail fragment, reset its state
    public void displayArticleDetailsComplete() {
        navigateToSelectedArticle.setValue(null);
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
            return (T) new SearchArticlesViewModel(mApplication);
        }
    }
}
