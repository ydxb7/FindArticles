package ai.tomorrow.findarticles.database.dao;

import androidx.lifecycle.LiveData;

import javax.annotation.Nonnull;

import ai.tomorrow.findarticles.database.entity.Article;
import ai.tomorrow.findarticles.database.util.RealmResultsLiveData;
import io.realm.Realm;
import io.realm.RealmModel;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class ArticleDao<T extends RealmModel> {

    private Realm db;

    public ArticleDao(Realm db){
        this.db = db;
    }

    // Get the LiveData RealmResults, get all the data in the database
    public LiveData<RealmResults<Article>> findAll(){
        return new RealmResultsLiveData<>(where().findAll());
    }

    private RealmQuery<Article> where() {
        return db.where(Article.class);
    }

    // Delete all the data in the database
    public void deleteAll() {
        db.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@Nonnull Realm bgRealm) {
                bgRealm.delete(Article.class);
            }
        });
    }
}
