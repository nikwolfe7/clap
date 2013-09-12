package com.clap;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class CountriesListActivity extends ClapListActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		title = "Country List";
		super.onCreate(savedInstanceState);
		new LoadCountryListTask(this).execute();
	}
	
	protected class LoadCountryListTask extends LoadListTask {

		public LoadCountryListTask(Context c) {
			super(c);
			emptyListMessage = "Country list was empty...";
		}

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			try {
				ApplicationState state = (ApplicationState)getApplication();
				return state.getCountryNames(); // This is sent to onPostExecute
			} catch (Exception e) {
				exception = e;
				return null;
			}
		}	

		@Override
		protected void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			// What was selected?
			String countrySelected = parent.getAdapter().getItem(position).toString();
			
			// Start the language list activity
			Intent languageActivity = new Intent(context, LanguageListActivity.class);
		    languageActivity.putExtra(EXTRA_COUNTRY_NAME, countrySelected);
		    startActivity(languageActivity);
		}
	}
}