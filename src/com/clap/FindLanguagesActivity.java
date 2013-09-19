package com.clap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FindLanguagesActivity extends ClapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		title = "Find Languages";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.find_languages);

		Button findByCountry = (Button)findViewById(R.id.buttonFindByCountry);
		findByCountry.setOnClickListener(new FindByCountryButton());
		
		Button findByLocation = (Button)findViewById(R.id.buttonFindByLocation);
		findByLocation.setOnClickListener(new FindByLocationButton());
		
		Button search = (Button)findViewById(R.id.buttonSearch);
		search.setOnClickListener(new SearchButton());
	}
	
	private class FindByCountryButton implements OnClickListener {
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
	
	private class FindByLocationButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// Start the find by location activity
				Intent findByLocationActivity = new Intent();
				findByLocationActivity.setClass(v.getContext(),
						FindByLocationActivity.class);
				startActivity(findByLocationActivity);
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}

	private class SearchButton implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				
			} catch (Exception e) {
				showErrorMessage(e.getMessage(), false);
			}
		}
	}
}
