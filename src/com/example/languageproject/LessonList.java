package com.example.languageproject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LessonList extends Activity {

	private static String HTTP_GET_STRING = "http://www.celebrate-language.com/public-api/?action=get_lessons_by_lang&lang=";
	private static Context context;
	
	private String language_name;
	private final Hashtable<String, String> lessonNameAndIDHashtable = new Hashtable<String, String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lesson_list);
		
		Intent i=getIntent();
		Bundle b=i.getExtras();
		language_name=b.getString("languageName");
		
		setTitle(language_name);
		
		LessonList.context = getApplicationContext();
		
		new LongRunningGetIO().execute();
	}

	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		private ListView lv;

		protected String getASCIIContentFromEntity(HttpEntity entity)
				throws IllegalStateException, IOException {
			InputStream in = entity.getContent();
			StringBuffer out = new StringBuffer();
			int n = 1;
			while (n > 0) {
				byte[] b = new byte[4096];
				n = in.read(b);
				if (n > 0)
					out.append(new String(b, 0, n));
			}
			return out.toString();
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			HttpGet httpGet = new HttpGet(HTTP_GET_STRING + language_name);
			String text = null;
			try {
				HttpResponse response = httpClient.execute(httpGet,
						localContext);
				HttpEntity entity = response.getEntity();
				text = getASCIIContentFromEntity(entity);
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
			return text;
		}

		protected void onPostExecute(String results) {
			// super.onPostExecute(results);
			if (results != null) {
				lv = (ListView) findViewById(R.id.lesson_list);

				results = results.replace("\"", "");
				results = results.substring(1,results.length()-1); //remove surrounding brackets
				String[] lessonNameAndIDArray = results.split(",");
				String[] lessonNames = new String[lessonNameAndIDArray.length];
				for (int i=0; i<lessonNameAndIDArray.length; i++) {
					String temp = lessonNameAndIDArray[i].replace("{", "");
					temp = temp.replace("}", "");
					String[] tempArray = temp.split(":");
					String lessonName = tempArray[0];
					String lessonID = tempArray[1];
					lessonNameAndIDHashtable.put(lessonName, lessonID);
					lessonNames[i] = lessonName;
				}

				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,R.layout.lesson_item, R.id.lesson_item_id, lessonNames);
				lv.setAdapter(adapter);
				
				lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						
						String selectedFromList =(parent.getAdapter().getItem(position).toString());
						String lessonID = lessonNameAndIDHashtable.get(selectedFromList);
						
						Intent lessonAudio = new Intent(context, PlayAudio.class);
						lessonAudio.putExtra("lessonName", selectedFromList);
						lessonAudio.putExtra("lessonID", lessonID);
						startActivity(lessonAudio);
						
					}
					
				});

			}
		}

	}
}
