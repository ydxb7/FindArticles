package ai.tomorrow.findarticles.models;

import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject implements Parcelable {

    private static String TAG = Article.class.getSimpleName();
    private static Realm realm;

    @PrimaryKey
    private String id;
    private String webUrl;
    private String headline;
    private String thumbnail;
    private String snippet;

    public Article() {

    }

    // Parcel the Article class
    protected Article(android.os.Parcel in) {
        id = in.readString();
        webUrl = in.readString();
        headline = in.readString();
        thumbnail = in.readString();
        snippet = in.readString();
    }

    // Parcel the Article class
    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(android.os.Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getSnippet() {
        return snippet;
    }

    // Parse one article instance from Json object
    public static Article ParseJson(JSONObject articleJson) {
        Article article = new Article();
        try {
            article.id = articleJson.getString("_id");
            article.webUrl = articleJson.getString("web_url");
            article.snippet = articleJson.getString("snippet");

            // Get the headline for Article
            JSONObject headlineJSONObject = articleJson.getJSONObject("headline");
            String printHeadline = headlineJSONObject.getString("print_headline");
            if (printHeadline != "null") {
                article.headline = printHeadline;
            } else {
                article.headline = headlineJSONObject.getString("main");
            }

            // Get the thumbnail
            JSONArray multimedia = articleJson.getJSONArray("multimedia");
            if (multimedia != null && multimedia.length() > 0) {
                JSONObject multimedia0 = multimedia.getJSONObject(0);
                article.thumbnail = multimedia0.getString("url");
            }

        } catch (JSONException e) {
            Log.d(TAG, "Parsing Json object into Article error!");
            e.printStackTrace();
        }

        return article;
    }

    // Parse the ArticleList from the JsonArray
    public static RealmList<Article> parseJsonIntoArticleList(JSONArray articleJsonResults) {
        RealmList<Article> articleRealmList = new RealmList<>();
        for (int i = 0; i < articleJsonResults.length(); i++) {
            try {
                articleRealmList.add(ParseJson(articleJsonResults.getJSONObject(i)));
            } catch (JSONException e) {
                Log.d(TAG, "get Jsonobject error!");
            }
        }
        return articleRealmList;
    }

    // Parcel
    @Override
    public int describeContents() {
        return 0;
    }

    // Parcel
    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(webUrl);
        dest.writeString(headline);
        dest.writeString(thumbnail);
        dest.writeString(snippet);
    }
}
