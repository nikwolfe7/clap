package com.clap;

import com.clap.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;


public class MainMenu extends Activity {

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
			// Start the countries list activity
			Intent countriesActivity = new Intent();
			countriesActivity.setClass(v.getContext(), CountriesList.class);
			startActivity(countriesActivity);
		}
	}

	private class ResetButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			// Reset the database
			ApplicationState state = (ApplicationState)getApplication();
			state.resetDatabase();
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
			
		}
	}
}
