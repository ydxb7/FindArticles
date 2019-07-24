package ai.tomorrow.findnews.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSettingBinding;

public class SettingFragment extends DialogFragment {

    private FragmentSettingBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        mBinding.setLifecycleOwner(this);



        return mBinding.getRoot();
    }
}
