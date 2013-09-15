package com.clap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainMenuActivity extends ClapActivity {

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		USE_ACTION_BAR_BACK_BUTTON = false;
		
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		// Set the user's preferences from the previous time Applause was run
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		ListItemTextView.setUserTextSize(sharedPreferences.getString(ClapPreferenceActivity.KEY_TEXT_SIZE, ""));
		WebAPI.setDataUsage(sharedPreferences.getString(ClapPreferenceActivity.KEY_DATA_USAGE, ""));

		setContentView(R.layout.main_menu);

		Button countries = (Button) findViewById(R.id.buttonCountry);
		countries.setOnClickListener(new CountriesButton());

		Button help = (Button) findViewById(R.id.buttonHelp);
		help.setOnClickListener(new HelpButton());

		Button about = (Button) findViewById(R.id.buttonAbout);
		about.setOnClickListener(new AboutButton());
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.press_back_again,
				Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	private class CountriesButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the countries list activity
				Intent countriesActivity = new Intent();
				countriesActivity.setClass(v.getContext(),
						CountriesListActivity.class);
				startActivity(countriesActivity);
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}

	private class HelpButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the about activity
				Intent helpActivity = new Intent();
				helpActivity.setClass(v.getContext(), HelpActivity.class);
				startActivity(helpActivity);
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}

	private class AboutButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the about activity
				Intent aboutActivity = new Intent();
				aboutActivity.setClass(v.getContext(), AboutActivity.class);
				startActivity(aboutActivity);
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}
}
