package com.clap;

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

public class LanguageList extends Activity {
	private String country_name;
	private String title = "Language List for ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		country_name = b.getString("countryName");
		title = title + country_name;
		setTitle(title);
		new LongRunningGetIO(this).execute();
	}

	private class LongRunningGetIO extends
			AsyncTask<Void, Void, ArrayList<String>> {
		private ListView lv;
		private Context context;
		private ProgressDialog progressDialog;

		public LongRunningGetIO(Context c) {
			context = c;
			progressDialog = new ProgressDialog(context);
		}
		
		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Loading " + this.getTitle() + "...");
			progressDialog.show();
		}
		
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			ApplicationState state = (ApplicationState) getApplication();
			return state.getLanguageNames(country_name);
		}

		private void showErrorMessage(String error) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// return to countries activity
					finish();
				}
			});
			builder.setMessage(error).setTitle("Oops!");
			AlertDialog errorDialog = builder.create();
			errorDialog.show();
		}
		
		protected void onPostExecute(ArrayList<String> languageList) {
			progressDialog.dismiss();
			
			if (languageList == null || languageList.isEmpty() || languageList.get(0).startsWith("Empty List")) {
				showErrorMessage("Language lessons currently unavailable for " + this.getCountryName());
			} else if (languageList.get(0).startsWith("Invalid List")) {
				showErrorMessage(languageList.get(0));
			} else {
				lv = (ListView)findViewById(R.id.list);

				// Display the languages in a list
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,
						R.layout.item,
						R.id.item_id,
						languageList);
				lv.setAdapter(adapter);

				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// What was selected?
						String languageSelected = (parent.getAdapter().getItem(position).toString());

						// Start the lesson list activity
						// Pass it the language name and country name
						Intent lessonActivity = new Intent(context, LessonList.class);
						lessonActivity.putExtra("languageName", languageSelected);
						lessonActivity.putExtra("countryName", getCountryName());
						startActivity(lessonActivity);
					}
				});
			}
		}
		
		private String getTitle() {
			return title;
		}
		
		private String getCountryName() {
			return country_name;
		}
	}
}
