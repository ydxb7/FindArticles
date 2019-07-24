package ai.tomorrow.findnews.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import ai.tomorrow.findnews.databinding.FragmentSettingBinding;

public class SettingViewModel extends ViewModel {

    private static final String TAG = SettingViewModel.class.getSimpleName();

    private final FragmentSettingBinding mBinding;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private static final String PREF_ARTS_KEY = "pref_arts_key";
    private static final String PREF_FASHION_KEY = "pref_fashion_key";
    private static final String PREF_SPORTS_KEY = "pref_sports_key";

    private static final Boolean PREF_ARTS_DEFAULT = false;
    private static final Boolean PREF_FASHION_DEFAULT = false;
    private static final Boolean PREF_SPORTS_DEFAULT = false;

    public SettingViewModel(Context context, FragmentSettingBinding binding){
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Boolean myArts = mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT);
        Boolean myFashion = mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT);
        Boolean mySports = mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT);

        Log.d(TAG, "myArts = " + myArts);
        Log.d(TAG, "myFashion = " + myFashion);
        Log.d(TAG, "mySports = " + mySports);

        mBinding = binding;

        mEditor = mPreferences.edit();

        setViewStatus();
    }

    private void setViewStatus(){
        mBinding.prefArtsCheckBox.setChecked(mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT));
        mBinding.prefFashionCheckBox.setChecked(mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT));
        mBinding.prefSportsCheckBox.setChecked(mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT));
    }

    public void onClickSavedButtom(){
        mEditor.putBoolean(PREF_ARTS_KEY, mBinding.prefArtsCheckBox.isChecked());
        mEditor.putBoolean(PREF_FASHION_KEY, mBinding.prefFashionCheckBox.isChecked());
        mEditor.putBoolean(PREF_SPORTS_KEY, mBinding.prefSportsCheckBox.isChecked());

        mEditor.commit();
    }





    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        @NonNull
        private final Context mContext;
        private final FragmentSettingBinding mBinding;

        public Factory(@NonNull Context context, FragmentSettingBinding binding) {
            mContext = context;
            mBinding = binding;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            //noinspection unchecked
            return (T) new SettingViewModel(mContext, mBinding);
        }
    }

}
