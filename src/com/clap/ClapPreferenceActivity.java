package com.clap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.Settings;

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
		currentPreference.setSummary(sharedPreferences.getBoolean(KEY_LOCATION, false)
				? getString(R.string.pref_location_enabled)
				: getString(R.string.pref_location_disabled));
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		// Check System Location Access
		// If it is disabled, disable Applause's Location Access
		LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!enabled) {
			Editor editor = sharedPreferences.edit();
			editor.putBoolean(KEY_LOCATION, false);
			editor.apply();
			Preference locationPreference = getPreferenceScreen().findPreference(KEY_LOCATION);
			locationPreference.setSummary(sharedPreferences.getBoolean(KEY_LOCATION, false)
				? getString(R.string.pref_location_enabled)
				: getString(R.string.pref_location_disabled));
		}
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
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Restart Applause");
			dialogBuilder.setMessage("To See The Changes Immediately Applause Must Be Restarted");
			dialogBuilder.setPositiveButton(getString(R.string.ok), new onRestartOkClick(this));
			dialogBuilder.setNeutralButton(getString(R.string.dialog_cancel), null);
			
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		} else if (key.equals(KEY_DATA_USAGE)) {
			// set the summary to the Data Usage then set WebAPI
			summary = sharedPreferences.getString(key, "");
			WebAPI.setDataUsage(summary);
		} else if (key.equals(KEY_LOCATION)) {
			boolean locationEnabled = sharedPreferences.getBoolean(key, false);
			summary = locationEnabled
				? getString(R.string.pref_location_enabled)
				: getString(R.string.pref_location_disabled);
			if (locationEnabled) {
				checkPhoneLocationEnabled();
			}
		}
		
		if (currentPreference != null) {
			currentPreference.setSummary(summary);
		}
	}
	
	private void checkPhoneLocationEnabled() {
		LocationManager service = (LocationManager)getSystemService(LOCATION_SERVICE);
		boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| service.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if (!enabled) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("System Location Access Disabled");
			dialogBuilder.setMessage("Do You Want To Enable System Location Access?");
			dialogBuilder.setPositiveButton(getString(R.string.btnYes), new EnablePhoneLocationClick());
			dialogBuilder.setNeutralButton(getString(R.string.btnNo), null);
			
			AlertDialog locationDialog = dialogBuilder.create();
			locationDialog.show();
		}
	}

	private class EnablePhoneLocationClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
	}

	private class onRestartOkClick implements OnClickListener {
		private Context context;
		
		public onRestartOkClick(Context c) {
			context = c;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			startActivity(new Intent(context, SplashActivity.class));
			
		}
	}
}
