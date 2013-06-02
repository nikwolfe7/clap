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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CountriesList extends Activity{
	private static String HTTP_GET_STRING = "http://www.celebrate-language.com/public-api/?action=get_country_list";
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.country_list);
		setTitle("Countries");
		CountriesList.context = getApplicationContext();
		
		new LongRunningGetIO().execute();
		
	}
	
	private class LongRunningGetIO extends AsyncTask<Void, Void, String> {
		
		public ListView lv;

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
			HttpGet httpGet = new HttpGet(HTTP_GET_STRING);
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
			super.onPostExecute(results);
			if (results != null) {
				lv = (ListView) findViewById(R.id.country_list);

				results = results.replace("\"", "");
				results = results.substring(1,results.length()-1);
				String[] strArray = results.split(",");

				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,R.layout.country_item, R.id.country_item_id, strArray);
				lv.setAdapter(adapter);
//				lv.setOnItemClickListener((OnItemClickListener) context);
				
				lv.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> parent, View arg1,
							int position, long arg3) {
						// TODO Auto-generated method stub
						
						String selectedFromList =(parent.getAdapter().getItem(position).toString());
						
//						Cursor c = (Cursor) parent.getAdapter().getItem(position);
						Intent country_name =new Intent(context, LanguageList.class);
//					    c.getString(c.getColumnIndex("col_name"));	    
					    country_name.putExtra("countryName", selectedFromList);
						startActivity(country_name);
						
					}
					
				});
			}
		}

	}
	
	public static Context getAppContext() {
        return CountriesList.context;
    }


	

}
