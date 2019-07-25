package ai.tomorrow.findnews.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import ai.tomorrow.findnews.R;
import ai.tomorrow.findnews.databinding.FragmentSettingBinding;

public class SettingViewModel extends ViewModel implements DatePickerDialog.OnDateSetListener{

    private static final String TAG = SettingViewModel.class.getSimpleName();

    private final FragmentSettingBinding mBinding;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    private DatePickerDialog dpd;

    private String PREF_ARTS_KEY;
    private String PREF_FASHION_KEY;
    private String PREF_SPORTS_KEY;
    private String PREF_SORT_KEY;

    private Boolean PREF_ARTS_DEFAULT;
    private Boolean PREF_FASHION_DEFAULT;
    private Boolean PREF_SPORTS_DEFAULT;
    private String PREF_SORT_DEFAULT;

    public SettingViewModel(Context context, FragmentSettingBinding binding){

        mContext = context;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        PREF_ARTS_DEFAULT =  mContext.getResources().getBoolean(R.bool.pref_arts_default);
        PREF_FASHION_DEFAULT =  mContext.getResources().getBoolean(R.bool.pref_fashion_default);
        PREF_SPORTS_DEFAULT =  mContext.getResources().getBoolean(R.bool.pref_sports_default);
        PREF_SORT_DEFAULT = mContext.getResources().getString(R.string.pref_sort_newest);

        PREF_ARTS_KEY = mContext.getResources().getString(R.string.pref_arts_key);
        PREF_FASHION_KEY = mContext.getResources().getString(R.string.pref_fashion_key);
        PREF_SPORTS_KEY = mContext.getResources().getString(R.string.pref_sports_key);
        PREF_SORT_KEY = mContext.getResources().getString(R.string.pref_sort_key);


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

        String spinnerValue = mPreferences.getString(PREF_SORT_KEY, PREF_SORT_DEFAULT);
        for (int i=0; i<mBinding.prefSortSpinner.getCount(); i++){
            if (mBinding.prefSortSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(spinnerValue)){
                mBinding.prefSortSpinner.setSelection(i);
            }
        }
    }

    public void onClickSavedButton(){
        mEditor.putBoolean(PREF_ARTS_KEY, mBinding.prefArtsCheckBox.isChecked());
        mEditor.putBoolean(PREF_FASHION_KEY, mBinding.prefFashionCheckBox.isChecked());
        mEditor.putBoolean(PREF_SPORTS_KEY, mBinding.prefSportsCheckBox.isChecked());
        mEditor.putString(PREF_SORT_KEY, mBinding.prefSortSpinner.getSelectedItem().toString());

        mEditor.commit();
    }

    public void showDatePickerDialog(){
        Calendar now = Calendar.getInstance();
            /*
            It is recommended to always create a new instance whenever you need to show a Dialog.
            The sample app is reusing them because it is useful when looking for regressions
            during testing
             */
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.setTitle("Date Picker");
//        dpd.setAccentColor(Color.parseColor("#9C27B0"));
        dpd.setOnCancelListener(dialog -> {
            Log.d("DatePickerDialog", "Dialog was cancelled");
            dpd = null;
        });
        dpd.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(++monthOfYear)+"/"+year;
        mBinding.dateTextview.setText(date);
        mBinding.dateEditText.setText(date);
        dpd = null;
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
