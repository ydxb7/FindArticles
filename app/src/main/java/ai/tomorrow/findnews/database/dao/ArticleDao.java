package ai.tomorrow.findnews.database.dao;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.annotation.Nonnull;

import ai.tomorrow.findnews.database.entity.Article;
import ai.tomorrow.findnews.database.util.RealmResultsLiveData;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class ArticleDao<T extends RealmModel> {

    private Realm db;

    public ArticleDao(Realm db){
        this.db = db;
    }

    public LiveData<RealmResults<Article>> findAll(){
        return new RealmResultsLiveData<>(where().findAll());
    }

    private RealmQuery<Article> where() {
        return db.where(Article.class);
    }

    public void deleteAll() {
        db.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@Nonnull Realm bgRealm) {
                bgRealm.delete(Article.class);
            }
        });
    }
}
