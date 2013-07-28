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

public class LessonList extends Activity {
	private String language_name;
	private String country_name;
	private String title = "Lesson List for ";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_list);
		Intent i = getIntent();
		Bundle b = i.getExtras();
		language_name = b.getString("languageName");
		country_name = b.getString("countryName");
		title = title + language_name;
		setTitle(title);
		new LongRunningGetIO(this).execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, ArrayList<String>> {
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
			return state.getLessonNames(language_name);
		}

		private void showErrorMessage(String error) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent languageActivity = new Intent(context, LanguageList.class);
					startActivity(languageActivity);
				}
			});
			builder.setMessage(error).setTitle("Oops!");
			AlertDialog errorDialog = builder.create();
			errorDialog.show();
		}
		
		protected void onPostExecute(ArrayList<String> lessonList) {
			progressDialog.dismiss();
			
			if (lessonList == null || lessonList.isEmpty() || lessonList.get(0).startsWith("Empty List")) {
				showErrorMessage("Unable to load lessons for " + this.getLanguageName());
			} else if (lessonList.get(0).startsWith("Invalid List")) {
				showErrorMessage(lessonList.get(0));
			} else {
				lv = (ListView)findViewById(R.id.lesson_list);

				// Display the lessons
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,R.layout.lesson_item, R.id.lesson_item_id, lessonList);
				lv.setAdapter(adapter);
				
				lv.setOnItemClickListener(new OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// What was selected?
						String lessonSelected = (parent.getAdapter().getItem(position).toString());
						
						Intent lessonAudio = new Intent(context, PlayAudio.class);
						lessonAudio.putExtra("lessonName", lessonSelected);
						lessonAudio.putExtra("languageName", language_name);
						lessonAudio.putExtra("countryName", country_name);
						startActivity(lessonAudio);
					}
				});
			}
		}

		private String getLanguageName() {
			return language_name;
		}
		
		private String getTitle() {
			return title;
		}
	}
}