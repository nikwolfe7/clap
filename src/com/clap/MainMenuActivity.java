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

		Button findLanguages = (Button)findViewById(R.id.buttonFindLanguages);
		findLanguages.setOnClickListener(new FindLanguagesButton());

		Button myLessons = (Button)findViewById(R.id.buttonMyLessons);
		myLessons.setOnClickListener(new MyLessonsButton());

		Button help = (Button) findViewById(R.id.buttonHelp);
		help.setOnClickListener(new HelpButton());

		Button about = (Button) findViewById(R.id.buttonAbout);
		about.setOnClickListener(new AboutButton());
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			// the back button has now been pressed twice within 2 seconds
			// exit the app
			super.onBackPressed();
			return;
		}

		// set the flag, inform the user to press back again to exit
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, R.string.press_back_again,
				Toast.LENGTH_SHORT).show();

		// after 2 seconds, the flag should be set to false again
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}

	private class FindLanguagesButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the find languages activity
				startActivity(new Intent(v.getContext(), FindLanguagesActivity.class));
			} catch (Exception e) {
				showErrorMessage(e, false);
			}
		}
	}

	private class MyLessonsButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the my lessons activity
				startActivity(new Intent(v.getContext(), MyLessonsActivity.class));
			} catch (Exception e) {
				showErrorMessage(e, false);
			}
		}
	}
	private class HelpButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the help activity
				startActivity(new Intent(v.getContext(), HelpActivity.class));
			} catch (Exception e) {
				showErrorMessage(e, false);
			}
		}
	}

	private class AboutButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the about activity
				startActivity(new Intent(v.getContext(), AboutActivity.class));
			} catch (Exception e) {
				showErrorMessage(e, false);
			}
		}
	}
}
