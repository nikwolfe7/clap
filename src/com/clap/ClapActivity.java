package com.clap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ClapActivity extends Activity {
	// the EXTRA Strings are used get Extras that are sent
	// when one Activity calls another Activity
	protected static final String EXTRA_COUNTRY_NAME = "country name";
	protected static final String EXTRA_LANGUAGE_NAME = "language name";
	protected static final String EXTRA_LESSON_NAME = "lesson name";

	// Menu Items
	private static final int ITEM_PREFERENCES = 0;
	private static final int ITEM_CLEAR_DATA = 1;
	
	private int clearDataItemSelected = 0;
	
	// boolean to determine if the back button on the Action Bar should be used
	protected boolean USE_ACTION_BAR_BACK_BUTTON = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (USE_ACTION_BAR_BACK_BUTTON) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ITEM_PREFERENCES, 0, getString(R.string.preferences)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ITEM_CLEAR_DATA, 0, getString(R.string.menu_clear)).setIcon(android.R.drawable.ic_menu_delete);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case ITEM_PREFERENCES:
				startActivity(new Intent(this, ClapPreferenceActivity.class));
				return true;
			case ITEM_CLEAR_DATA:
				// TODO: need to find a better way to do this, right now
				// if this is not done in the main menu the list of items
				// in the current and previous activities are not cleared
				clearData();
				return true;
		}
		return false;
	}

	private void clearData() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Clear Data");
		dialogBuilder.setSingleChoiceItems(R.array.dialog_clear_items, 0, new dialogItemClick());
		dialogBuilder.setPositiveButton(R.string.dialog_clear, new clearDataOnClick(this));
		dialogBuilder.setNegativeButton(R.string.dialog_cancel, new cancelOnClick());
		
		AlertDialog clearDataDialog = dialogBuilder.create();
		clearDataDialog.show();
	}
	
	private class dialogItemClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			clearDataItemSelected = which;
		}
	}

	private class clearDataOnClick implements OnClickListener {
		private Context context;
		
		public clearDataOnClick(Context c) {
			context = c;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			switch(clearDataItemSelected) {
				case 0:
					new clearDataTask(context, true, true).execute();
					return;
				case 1:
					new clearDataTask(context, true, false).execute();
					return;
				case 2:
					new clearDataTask(context, false, true).execute();
					return;
				default:
					return;
			}
		}
	}
	
	private class clearDataTask extends AsyncTask<Void,Void,Void> {
		private boolean clearDatabase = false;
		private boolean clearFiles = false;
		private Context context;
		private ProgressDialog progressDialog;

		public clearDataTask(Context c, boolean clrDatabase, boolean clrFiles) {
			context = c;
			progressDialog = new ProgressDialog(context);
			clearDatabase = clrDatabase;
			clearFiles = clrFiles;
		}

		@Override
		protected void onPreExecute() {
			progressDialog.setMessage("Clearing Data...");
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			ApplicationState state = (ApplicationState)getApplication();

			if (clearDatabase) {
				try {
					// Reset the database
					state.resetDatabase();
				} catch (Exception e) {
					progressDialog.dismiss();
					showErrorMessage("Exception Clearing Database:\n" + e.getMessage(), false);
				}
			}
			
			if (clearFiles) {
				try {
					// Delete all files
					state.deleteAllFiles();
				} catch (Exception e) {
					progressDialog.dismiss();
					showErrorMessage("Exception Clearing Files:\n" + e.getMessage(), false);
				}
			}

			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			progressDialog.dismiss();
		}
	}

	private class cancelOnClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	}

	protected void showErrorMessage(Exception exception) {
		showErrorMessage(exception, true);
	}
	
	protected void showErrorMessage(Exception exception, boolean returnToPreviousActivity) {
		if (exception.getMessage() == null) {
			showErrorMessage(exception.toString(), returnToPreviousActivity);
		} else {
			showErrorMessage(exception.getMessage(), returnToPreviousActivity);
		}
	}

	protected void showErrorMessage(String error) {
		// by default return to the previous activity
		showErrorMessage(error, true);
	}

	protected void showErrorMessage(String error, boolean returnToPreviousActivity) {
		// build a dialog box with 'error' as the message, 'Oops!!' as the title and an 'OK' button
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(R.string.ok, new ErrorOkButtonOnClick(returnToPreviousActivity));
		dialogBuilder.setMessage(error).setTitle("Oops!!");

		dialogBuilder.create().show();
	}
	
	private class ErrorOkButtonOnClick implements OnClickListener {
		private boolean returnToPreviousActivity;
		
		public ErrorOkButtonOnClick(boolean retToPrev) {
			returnToPreviousActivity = retToPrev;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (returnToPreviousActivity) {
				// return to the previous activity
				finish();
			}
		}
	}
}
