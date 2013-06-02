package com.example.languageproject;

import java.io.IOException;
import java.io.InputStream;

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LanguageList extends Activity{
	
	private static String HTTP_GET_STRING = "http://www.celebrate-language.com/public-api/?action=get_lang_list_by&country=";
	private static Context context;
	
	private String country_name; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.language_list);
		
		Intent i=getIntent();
		Bundle b=i.getExtras();
//		int topic_id=b.getInt("topicId");
		country_name=b.getString("countryName");
		
		setTitle(country_name);
		
		LanguageList.context = getApplicationContext();
		
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
			HttpGet httpGet = new HttpGet(HTTP_GET_STRING + country_name);
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
				lv = (ListView) findViewById(R.id.language_list);

				results = results.replace("\"", "");
				results = results.substring(1,results.length()-1);
				String[] strArray = results.split(",");

				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,R.layout.language_item, R.id.language_item_id, strArray);
				lv.setAdapter(adapter);
//				lv.setOnItemClickListener((OnItemClickListener) context);
				
				lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						
						String selectedFromList =(parent.getAdapter().getItem(position).toString());
						
//						Cursor c = (Cursor) parent.getAdapter().getItem(position);
						Intent language_name =new Intent(context, LessonList.class);
//					    c.getString(c.getColumnIndex("col_name"));	    
						language_name.putExtra("languageName", selectedFromList);
						startActivity(language_name);
						
					}
					
				});

			}
		}

	}
	

}
