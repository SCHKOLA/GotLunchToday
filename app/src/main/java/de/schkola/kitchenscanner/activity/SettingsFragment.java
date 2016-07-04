package de.schkola.kitchenscanner.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import de.schkola.kitchenscanner.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupPreferences();
    }

    private void setupPreferences() {
        addPreferencesFromResource(R.xml.pref_kitchen_scanner);
    }
}

