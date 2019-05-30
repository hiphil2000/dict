package com.example.dictionary.View;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.EditTextPreference.SimpleSummaryProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.dictionary.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            EditTextPreference pref_youtube_max_result = findPreference("pref_youtube_max_result");
            pref_youtube_max_result.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Pattern pattern = Pattern.compile("[^0-9]");
                    Matcher matcher = pattern.matcher(newValue.toString());
                    if (matcher.find()) {
                        Toast.makeText(getContext(), "숫자만 입력해주세요.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    int newval = Integer.parseInt(newValue.toString());
                    if (newval <= 0 || newval > 10) {
                        Toast.makeText(getContext(), "0~10 사이의 값만 입력해주세요.", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    return true;
                }
            });

            Preference pref_data_current_usage = findPreference("pref_data_current_usage");
            pref_data_current_usage.setSummaryProvider(new Preference.SummaryProvider<Preference>() {
                @Override
                public CharSequence provideSummary(Preference preference) {
                    double totalSpace = getContext().getExternalFilesDir(null).getTotalSpace() / (1024 * 1024);
                    double usableSpace = getContext().getExternalFilesDir(null).getUsableSpace() / (1024 * 1024);

                    return String.format("%.2fmb 중 %.2fmb 사용중.", totalSpace, usableSpace);
                }
            });
        }
    }
}