package com.clap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class ClapActivity extends Activity {
	protected static final String EXTRA_COUNTRY_NAME = "country name";
	protected static final String EXTRA_LANGUAGE_NAME = "language name";
	protected static final String EXTRA_LESSON_NAME = "lesson name";

	protected void showErrorMessage(String error) {
		showErrorMessage(error, true);
	}

	protected void showErrorMessage(String error, boolean returnToPreviousActivity) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setPositiveButton(R.string.ok, new ErrorOkButtonOnClick(returnToPreviousActivity));
		dialogBuilder.setMessage(error).setTitle("Oops!!");

		AlertDialog errorDialog = dialogBuilder.create();
		errorDialog.show();
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
