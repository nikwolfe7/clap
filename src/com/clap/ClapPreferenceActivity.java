package com.clap;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@SuppressWarnings("deprecation")
public class ClapPreferenceActivity extends PreferenceActivity
	implements OnSharedPreferenceChangeListener {

	public static final String KEY_TEXT_SIZE = "pref_text_size";
	public static final String KEY_DATA_USAGE = "pref_data_usage";
	public static final String KEY_LOCATION = "pref_location";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

		Preference currentPreference = getPreferenceScreen().findPreference(KEY_TEXT_SIZE);
		currentPreference.setSummary(sharedPreferences.getString(KEY_TEXT_SIZE, ""));
		
		currentPreference = getPreferenceScreen().findPreference(KEY_DATA_USAGE);
		currentPreference.setSummary(sharedPreferences.getString(KEY_DATA_USAGE, ""));

		currentPreference = getPreferenceScreen().findPreference(KEY_LOCATION);
		currentPreference.setSummary(sharedPreferences.getBoolean(KEY_LOCATION, false) ? "Use Location" : "Do Not Use Location");
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference currentPreference = getPreferenceScreen().findPreference(key);
		String summary = "";

		if (key.equals(KEY_TEXT_SIZE)) {
			// set the summary to the Text Size then change the List Item Text Size
			summary = sharedPreferences.getString(key, "");
			ListItemTextView.setUserTextSize(summary);
		} else if (key.equals(KEY_DATA_USAGE)) {
			// set the summary to the Data Usage then set WebAPI
			summary = sharedPreferences.getString(key, "");
			WebAPI.setDataUsage(summary);
		} else if (key.equals(KEY_LOCATION)) {
			summary = sharedPreferences.getBoolean(key, false) ? "Use Location" : "Do Not Use Location";
		}
		
		if (currentPreference != null) {
			currentPreference.setSummary(summary);
		}
	}
}
