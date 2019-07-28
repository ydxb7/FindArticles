package ai.tomorrow.findarticles.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.databinding.FragmentSettingBinding;


public class SettingFragment extends DialogFragment {

    private FragmentSettingBinding mBinding;
    private SettingFragmentCallback settingFragmentCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        mBinding.setLifecycleOwner(this);

        SettingViewModel.Factory factory = new SettingViewModel.Factory(getContext(), mBinding);

        final SettingViewModel viewModel = ViewModelProviders.of(this, factory)
                .get(SettingViewModel.class);

        mBinding.setViewModel(viewModel);

        viewModel.getNavigateBack().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isNagigateBack) {
                if (isNagigateBack){
                    getDialog().dismiss();
                    viewModel.navigateBackCompelete();
                    settingFragmentCallback.onDialogDismiss();
                }
            }
        });

        return mBinding.getRoot();
    }

    public void setCallback(SettingFragmentCallback settingFragmentCallback) {
        this.settingFragmentCallback = settingFragmentCallback;
    }

    public interface SettingFragmentCallback{
        void onDialogDismiss();
    }

}
