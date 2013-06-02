package com.example.languageproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;


public class Main_menu extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		Button countries = (Button) findViewById(R.id.button1);
		countries.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent countriesActivity = new Intent();
				countriesActivity.setClass(arg0.getContext(), CountriesList.class);
				startActivity(countriesActivity);
				
			}
		});
	}
	
	

}
