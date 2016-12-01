package com.example.android.popularmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.renderscript.Sampler;

import static android.R.attr.value;

public class settingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        bindPrefenceSummryToValue(findPreference("sortby"));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String stringValue= newValue.toString();
        if(preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference)preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex>-0)
            {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
            else
            {
                preference.setSummary(stringValue);
            }
        }



        return true;
    }
    private void   bindPrefenceSummryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(),""));
    }
}
