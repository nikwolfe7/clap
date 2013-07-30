package com.clap;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListActivity extends ClapActivity {
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
		
		public LoadListTask(Context c) {
			this.context = c;
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

		protected void showEmptyListMessage() {
			showErrorMessage("List was empty!");
		}

		/*protected void showErrorMessage(String error) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setPositiveButton(R.string.ok, new errorMessageOkButtonOnClick());
			dialogBuilder.setMessage(error).setTitle("Oops!");
			AlertDialog errorDialog = dialogBuilder.create();
			errorDialog.show();
		}*/
		
		protected class errorMessageOkButtonOnClick implements OnClickListener {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// return to the previous page
					finish();
				}
		}

		// receives a list of country names from doInBackground process
		protected void onPostExecute(ArrayList<String> list) {
			progressDialog.dismiss();
			
			if (list == null || list.isEmpty() || list.get(0).startsWith(WebAPI.ERROR_EMPTY_LIST)) {
				showEmptyListMessage();
			} else if (list.get(0).startsWith(WebAPI.ERROR_INVALID_LIST)) {
				showErrorMessage(list.get(0));
			} else {
				listView = (ListView) findViewById(R.id.list);

				// Display the list 
				ArrayAdapter<String> adapter;
				adapter = new ArrayAdapter<String>(context,
						R.layout.item,
						R.id.item_id,
						list);
				listView.setAdapter(adapter);
				
				listView.setOnItemClickListener(new listItemOnClickListener());
			}
		}

		protected void listItemOnClick(AdapterView<?> parent, View arg1, int position, long arg3) {
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