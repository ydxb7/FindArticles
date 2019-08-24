package ai.tomorrow.findarticles.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Locale;

import ai.tomorrow.findarticles.R;
import ai.tomorrow.findarticles.databinding.FragmentSettingBinding;

public class SettingViewModel extends ViewModel implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = SettingViewModel.class.getSimpleName();

    private final FragmentSettingBinding mBinding;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private Context mContext;
    private DatePickerDialog dpd;

    // Preference key
    private String PREF_ARTS_KEY;
    private String PREF_FASHION_KEY;
    private String PREF_SPORTS_KEY;
    private String PREF_SORT_KEY;
    private String PREF_BEGIN_DATE_KEY;

    // Preference default value
    private Boolean PREF_ARTS_DEFAULT;
    private Boolean PREF_FASHION_DEFAULT;
    private Boolean PREF_SPORTS_DEFAULT;
    private String PREF_SORT_DEFAULT;
    private int PREF_BEGIN_DATE_DEFAULT;

    private int mBginDate;

    private MutableLiveData<Boolean> navigateBack = new MutableLiveData<>();

    public LiveData<Boolean> getNavigateBack() {
        return navigateBack;
    }

    public void navigateBackCompelete() {
        navigateBack.setValue(false);
    }

    public SettingViewModel(Context context, FragmentSettingBinding binding) {

        mContext = context;

        // Get preference values
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        PREF_ARTS_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_arts_default);
        PREF_FASHION_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_fashion_default);
        PREF_SPORTS_DEFAULT = mContext.getResources().getBoolean(R.bool.pref_sports_default);
        PREF_SORT_DEFAULT = mContext.getResources().getString(R.string.pref_sort_newest);
        PREF_BEGIN_DATE_DEFAULT = mContext.getResources().getInteger(R.integer.pref_begin_date_default);

        PREF_ARTS_KEY = mContext.getResources().getString(R.string.pref_arts_key);
        PREF_FASHION_KEY = mContext.getResources().getString(R.string.pref_fashion_key);
        PREF_SPORTS_KEY = mContext.getResources().getString(R.string.pref_sports_key);
        PREF_SORT_KEY = mContext.getResources().getString(R.string.pref_sort_key);
        PREF_BEGIN_DATE_KEY = mContext.getResources().getString(R.string.pref_begin_date_key);

        mBinding = binding;

        mEditor = mPreferences.edit();

        setViewStatus();
    }

    // Set the View status according to the preference
    private void setViewStatus() {
        mBinding.prefArtsCheckBox.setChecked(mPreferences.getBoolean(PREF_ARTS_KEY, PREF_ARTS_DEFAULT));
        mBinding.prefFashionCheckBox.setChecked(mPreferences.getBoolean(PREF_FASHION_KEY, PREF_FASHION_DEFAULT));
        mBinding.prefSportsCheckBox.setChecked(mPreferences.getBoolean(PREF_SPORTS_KEY, PREF_SPORTS_DEFAULT));

        String spinnerValue = mPreferences.getString(PREF_SORT_KEY, PREF_SORT_DEFAULT);
        for (int i = 0; i < mBinding.prefSortSpinner.getCount(); i++) {
            if (mBinding.prefSortSpinner.getItemAtPosition(i).toString().equalsIgnoreCase(spinnerValue)) {
                mBinding.prefSortSpinner.setSelection(i);
            }
        }

        int myBgindate = mPreferences.getInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);
        if (myBgindate != 0) {
            int dayOfMonth = myBgindate % 100;
            int monthOfYear = (myBgindate / 100) % 100;
            int year = myBgindate / 10000;
            String date = String.format(Locale.US, "%02d/%02d/%04d", monthOfYear, dayOfMonth, year);
            mBinding.dateEditText.setText(date);
        }
    }

    // When the saved button is clicked, we save the preference
    public void onClickSavedButton() {
        mEditor.putBoolean(PREF_ARTS_KEY, mBinding.prefArtsCheckBox.isChecked());
        mEditor.putBoolean(PREF_FASHION_KEY, mBinding.prefFashionCheckBox.isChecked());
        mEditor.putBoolean(PREF_SPORTS_KEY, mBinding.prefSportsCheckBox.isChecked());
        mEditor.putString(PREF_SORT_KEY, mBinding.prefSortSpinner.getSelectedItem().toString());
        mEditor.putInt(PREF_BEGIN_DATE_KEY, mBginDate);

        mEditor.commit();

        navigateBack.setValue(true);
    }

    // Clean the date picked from the DatePicker
    public void onClickCleanButton() {
        mEditor.putInt(PREF_BEGIN_DATE_KEY, PREF_BEGIN_DATE_DEFAULT);
        mBinding.dateEditText.setText("");
    }

    // Show DatePickerDialog, and pick the date, the date picked will be saved use a 8-digit int
    public void showDatePickerDialog() {
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
        dpd.setOnCancelListener(dialog -> {
            Log.d("DatePickerDialog", "Dialog was cancelled");
            dpd = null;
        });
        dpd.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Datepickerdialog");
    }

    // Called when the date is saved
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format(Locale.US, "%02d/%02d/%04d", ++monthOfYear, dayOfMonth, year);
        // Set the Edit text value
        mBinding.dateEditText.setText(date);
        dpd = null;
        // the date is saved as a 8-digit int
        mBginDate = year * 10000 + monthOfYear * 100 + dayOfMonth;
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
