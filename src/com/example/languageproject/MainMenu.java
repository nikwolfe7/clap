package com.example.languageproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;


public class MainMenu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		Button countries = (Button) findViewById(R.id.buttonCountry);
		countries.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Start the countries list activity
				Intent countriesActivity = new Intent();
				countriesActivity.setClass(arg0.getContext(), CountriesList.class);
				startActivity(countriesActivity);
				
			}
		});
		
		Button reset = (Button) findViewById(R.id.buttonReset);
		reset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Reset the database
				ApplicationState state = (ApplicationState)getApplication();
				state.resetDatabase();
			}
		});
	}
	
	

}
