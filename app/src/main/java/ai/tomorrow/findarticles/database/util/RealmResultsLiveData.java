package ai.tomorrow.findarticles.database.util;

import androidx.lifecycle.LiveData;

import javax.annotation.Nonnull;

import io.realm.RealmChangeListener;
import io.realm.RealmModel;
import io.realm.RealmResults;

public class RealmResultsLiveData<T extends RealmModel> extends LiveData<RealmResults<T>> {

    private RealmResults<T> results;

    private RealmChangeListener<RealmResults<T>> listener;

    public RealmResultsLiveData(RealmResults<T> results) {
        this.results = results;

        // When the RealmResults change, the LiveData changes
        listener = new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(@Nonnull RealmResults<T> updates) {
                setValue(updates);
            }
        };

    }

    // Add the realm database change Listener
    @Override
    protected void onActive() {
        super.onActive();
        results.addChangeListener(listener);
    }

    // Remove the realm database change listener
    @Override
    protected void onInactive() {
        super.onInactive();
        results.removeChangeListener(listener);
    }

}