package ai.tomorrow.findnews.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {

    @PrimaryKey
    private String id;
    private String webUrl;
    private String headline;
    private String thumbnail;
    private String snippet;

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





}
