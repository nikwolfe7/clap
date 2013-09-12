package com.clap;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ClapListActivity extends ClapActivity {
	protected String title = "Applause";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		setTitle(title);
	}
	
	protected class LoadListTask extends AsyncTask<Void,Void,ArrayList<String>> {
		protected ListView listView;
		protected Context context;
		protected ProgressDialog progressDialog;
		protected Exception exception = null;
		protected String emptyListMessage = "Empty List";
		
		public LoadListTask(Context c) {
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
			return null;
		}

		@Override
		// receives a list of names from doInBackground process
		protected void onPostExecute(ArrayList<String> list) {
			progressDialog.dismiss();
			
			// check that our list is not empty
			// and that WebAPI didn't have an error
			if (list == null || list.isEmpty()) {
				if (exception == null || exception.getMessage() == WebAPI.ERROR_EMPTY_LIST) {
					showErrorMessage(emptyListMessage);
				} else {
					showErrorMessage(exception);
				}
			} else {
				listView = (ListView) findViewById(R.id.list);

				// Display the list of names 
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,
						R.layout.item,
						R.id.item_id,
						list);
				listView.setAdapter(adapter);
				
				// Set what will happen when an item in the list is clicked on
				listView.setOnItemClickListener(new listItemOnClickListener());
			}
		}

		protected void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
			// This needs to be overridden by the subclass
			// Get item selected
			// Do something with the item
		}

		private class listItemOnClickListener implements OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent,
					View arg1,
					int position,
					long arg3) {
				listItemOnClick(parent, arg1, position, arg3);
			}
		}
		
		private String getTitle() {
			return title;
		}
	}
}