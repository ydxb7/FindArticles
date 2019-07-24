package ai.tomorrow.findnews.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSettingBinding;

public class SettingFragment extends DialogFragment {

    private FragmentSettingBinding mBinding;
//    private SharedPreferences mPreferences;
//    private SharedPreferences.Editor mEditor;
//
//    private static final String PREF_ARTS_KEY = "pref_arts_key";
//    private static final String PREF_FASHION_KEY = "pref_fashion_key";
//    private static final String PREF_SPORTS_KEY = "pref_sports_key";
//
//    private static final Boolean PREF_ARTS_DEFAULT = false;
//    private static final Boolean PREF_FASHION_DEFAULT = false;
//    private static final Boolean PREF_SPORTS_DEFAULT = false;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        mBinding.setLifecycleOwner(this);

        SettingViewModel.Factory factory = new SettingViewModel.Factory(getContext(), mBinding);

        final SettingViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(SettingViewModel.class);

        mBinding.setViewModel(viewModel);

//        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
//        Boolean mypref = mPreferences.getBoolean("mypref_whatever", true);
//
//
//        mEditor = mPreferences.edit();
//        editor.putBoolean("mypref_whatever", false);
//        editor.commit();


        return mBinding.getRoot();
    }




//    public void onClickSaved(){
//
//
//
//
//    }
}
