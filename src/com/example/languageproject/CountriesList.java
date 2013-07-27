package com.example.languageproject;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CountriesList extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.country_list);
		setTitle("Countries");
		new LongRunningGetIO(this).execute();
	}
	
	private class LongRunningGetIO extends AsyncTask<Void, Void, ArrayList<String>> {
		private ListView lv;
		private Context context;
		private ProgressDialog progressDialog;

		public LongRunningGetIO(Context c) {
			this.context = c;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading...");
			progressDialog.show();
		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState)getApplication();
			return state.getCountryNames(); // This is sent to onPostExecute
		}

		private void showErrorMessage(String error) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setMessage(error)
			.setTitle("Error");
			AlertDialog errorDialog = builder.create();
			errorDialog.show();
		}
		
		// receives a list of country names from doInBackground process
		protected void onPostExecute(ArrayList<String> countryList) {
			progressDialog.dismiss();
			
			if (countryList == null || countryList.isEmpty()) {
				showErrorMessage("Empty List");
			} else if (countryList.get(0).startsWith("Error:")) {
				showErrorMessage(countryList.get(0));
			} else {
				lv = (ListView) findViewById(R.id.country_list);

				// Display the countries
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,R.layout.country_item, R.id.country_item_id, countryList);
				lv.setAdapter(adapter);
				
				lv.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// What was selected?
						String countrySelected = (parent.getAdapter().getItem(position).toString());
						
						// Start the language list activity
						Intent languageActivity = new Intent(context, LanguageList.class);
					    languageActivity.putExtra("countryName", countrySelected);
						startActivity(languageActivity);
					}
				});
			}
		}
	}
}