package com.clap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ClapActivity extends Activity {
	protected String title = "Applause";

	// the EXTRA Strings are used get Extras that are sent
	// when one Activity calls another Activity
	protected static final String EXTRA_COUNTRY_NAME = "country name";
	protected static final String EXTRA_LANGUAGE_NAME = "language name";
	protected static final String EXTRA_LESSON_NAME = "lesson name";

	// Menu Items
	private static final int ITEM_PREFERENCES = 0;
	private static final int ITEM_CLEAR_DATA = 1;
	private static final int ITEM_EXPORT_PHRASES = 2;
	private static final int ITEM_ADD_TO_MY_LESSONS = 3;
	
	private int clearDataItemSelected = 0;
	private boolean lessonIsInMyLessons = false;
	
	// boolean to determine if the back button on the Action Bar should be used
	protected boolean USE_ACTION_BAR_BACK_BUTTON = true;
	
	// booleans to determine if the activity should have the options menu for:
	//   exporting phrases to text file
	protected boolean USE_OPTIONS_MENU_EXPORT = false;
	//   adding the lesson to 'My Lessons'
	protected boolean USE_OPTIONS_MENU_ADD = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(title);
		if (USE_ACTION_BAR_BACK_BUTTON) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		if (USE_OPTIONS_MENU_ADD) {
			ApplicationState applicationState = (ApplicationState)getApplication();
			lessonIsInMyLessons = applicationState.lessonIsInMyLessons(getLessonName());
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, ITEM_PREFERENCES, 0, getString(R.string.menu_preferences)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, ITEM_CLEAR_DATA, 0, getString(R.string.menu_clear_data)).setIcon(android.R.drawable.ic_menu_delete);
		if (USE_OPTIONS_MENU_EXPORT) {
			menu.add(Menu.NONE, ITEM_EXPORT_PHRASES, 0, getString(R.string.menu_export_phrases)).setIcon(android.R.drawable.ic_menu_manage);
		}
		if (USE_OPTIONS_MENU_ADD) {
			String menuTitle;
			if (!lessonIsInMyLessons) {
				menuTitle = getString(R.string.menu_add_to_my_lessons);
			} else {
				menuTitle = getString(R.string.menu_remove_from_my_lessons);
			}
			menu.add(Menu.NONE, ITEM_ADD_TO_MY_LESSONS, 0, menuTitle).setIcon(android.R.drawable.ic_menu_add);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case ITEM_PREFERENCES:
				startActivity(new Intent(this, ClapPreferenceActivity.class));
				return true;
			case ITEM_CLEAR_DATA:
				clearData();
				return true;
			case ITEM_EXPORT_PHRASES:
				if (USE_OPTIONS_MENU_EXPORT) {
					// This option is only in:
					//   PlayActivity
					//   StudyActivity
					//   LessonActivity
					exportPhrases();
					return true;
				} else {
					return false;
				}
			case ITEM_ADD_TO_MY_LESSONS:
				if (USE_OPTIONS_MENU_ADD) {
					// This option is only in:
					//   PlayActivity
					//   StudyActivity
					//   LessonActivity
					if (!lessonIsInMyLessons) {
						addToMyLessons();
						lessonIsInMyLessons = true;
						item.setTitle(R.string.menu_remove_from_my_lessons);
					} else {
						removeFromMyLessons();
						lessonIsInMyLessons = false;
						item.setTitle(R.string.menu_add_to_my_lessons);
					}
					return true;
				} else {
					return false;
				}
		}
		return false;
	}

	private void clearData() {
		//AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Clear Data");
		dialogBuilder.setSingleChoiceItems(R.array.dialog_clear_items, 0, new dialogItemClick());
		dialogBuilder.setPositiveButton(R.string.dialog_clear, new clearDataOnClick(this));
		dialogBuilder.setNegativeButton(R.string.dialog_cancel, new cancelOnClick());
		
		AlertDialog clearDataDialog = dialogBuilder.create();
		clearDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(R.color.default_background));
		clearDataDialog.show();
	}
	
	private void exportPhrases() {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Export Phrases");
		String lessonName = getLessonName();
		String fileName = getFileName(lessonName);
		dialogBuilder.setMessage("The phrases for \'" + lessonName + "\' will be exported to file: " + fileName);
		dialogBuilder.setPositiveButton(R.string.dialog_export, new exportPhrasesOnClick(lessonName, fileName));
		dialogBuilder.setNegativeButton(R.string.dialog_cancel, new cancelOnClick());
		
		AlertDialog exportPhrasesDialog = dialogBuilder.create();
		exportPhrasesDialog.show();
	}

	private void addToMyLessons() {
		String lessonName = getLessonName();
		ApplicationState applicationState = (ApplicationState)getApplication();
		applicationState.addLessonToMyLessons(lessonName);
	}

	private void removeFromMyLessons() {
		String lessonName = getLessonName();
		ApplicationState applicationState = (ApplicationState)getApplication();
		applicationState.removeLessonFromMyLessons(lessonName);
	}

	private String getLessonName() {
		String lessonName = (String)this.getTitle();
		if (lessonName.contains(":")) {
			// In the PlayActivity when audio is playing or paused the title will be
			// "Playing: LessonName" or "Paused: LessonName" - we just want LessonName
			lessonName = lessonName.substring(lessonName.indexOf(":") + 2);
		}
		return lessonName;
	}

	private String getFileName(String lessonName) {
		ApplicationState appState = (ApplicationState)getApplication();
		return appState.getAppDirectory() + "/" + lessonName.split(" ")[0] + "/"
				+ lessonName.replace(' ', '_') + ".txt";
	}

	private class dialogItemClick implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			clearDataItemSelected = which;
		}
	}

	private class exportPhrasesOnClick implements OnClickListener {
		private String fileName;
		private String lessonName;
		
		public exportPhrasesOnClick(String lName, String fName) {
			fileName = fName;
			lessonName = lName;
		}
		
		@Override
		public void onClick(final DialogInterface dialog, int which) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					try {
						ApplicationState appState = (ApplicationState)getApplication();
						ArrayList<Phrase> phrases = appState.getPhrases(lessonName);
						FileOutputStream fileOutput = new FileOutputStream(new File(fileName));
						for (Phrase p : phrases) {
							String line = p.getPhraseText() + " : " + p.getTranslatedText() + "\n";
							fileOutput.write(line.getBytes());
						}
						fileOutput.close();
					} catch (final Exception e) {
						dialog.dismiss();
						runOnUiThread(new Runnable() {
							public void run() {
								showErrorMessage(e, false);
							}
						});
					}
				}
			});
			thread.start();
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
			
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
			dialogBuilder.setTitle("Restart Applause");
			dialogBuilder.setMessage("To See The Changes Immediately Applause Must Be Restarted");
			dialogBuilder.setPositiveButton(getString(R.string.ok), new onRestartOkClick(context));
			dialogBuilder.setNeutralButton(getString(R.string.dialog_cancel), null);
			
			AlertDialog alertDialog = dialogBuilder.create();
			alertDialog.show();
		}
		
		private class onRestartOkClick implements OnClickListener {
			private Context context;
			
			public onRestartOkClick(Context c) {
				context = c;
			}

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(context, SplashActivity.class));
			}
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

		AlertDialog alertDialog = dialogBuilder.create();
		alertDialog.show();
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
