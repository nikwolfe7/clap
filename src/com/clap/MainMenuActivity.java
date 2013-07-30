package com.clap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends ClapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		Button countries = (Button)findViewById(R.id.buttonCountry);
		countries.setOnClickListener(new CountriesButton());
		
		Button reset = (Button)findViewById(R.id.buttonReset);
		reset.setOnClickListener(new ResetButton()); 
		
		Button help = (Button)findViewById(R.id.buttonHelp);
		help.setOnClickListener(new HelpButton());
		
		Button about = (Button)findViewById(R.id.buttonAbout);
		about.setOnClickListener(new AboutButton());
	}
	
	private class CountriesButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the countries list activity
				Intent countriesActivity = new Intent();
				countriesActivity.setClass(v.getContext(), CountriesListActivity.class);
				startActivity(countriesActivity);
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}

	private class ResetButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Reset the database
				ApplicationState state = (ApplicationState)getApplication();
				state.resetDatabase();
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}
	
	private class HelpButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			
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