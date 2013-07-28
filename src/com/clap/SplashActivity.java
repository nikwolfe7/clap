package com.clap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;

public class SplashActivity extends Activity {
	// used to know if the back button was pressed
	// in the splash screen activity and avoid opening the next activity
    private boolean isBackButtonPressed = false;
    private static final int SPLASH_DURATION = 3000; // 3 seconds
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		Handler handler = new Handler();

		// run a thread after 2 seconds to start the home screen
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				// make sure we close the splash screen so the user
				// won't come back when it presses back key
				finish();
            
	            if (!isBackButtonPressed) {
	                // start the home screen if the back button wasn't pressed already 
					Intent openStartingPoint = new Intent(SplashActivity.this, MainMenu.class);
					 startActivity(openStartingPoint);
	            }
			}
		}, SPLASH_DURATION); // time in milliseconds until the run() method will be called
	}

	@Override
	public void onBackPressed() {
	    // set the flag to true so the next activity won't start up
	    isBackButtonPressed = true;
	    super.onBackPressed();
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.main_menu, menu);
		return true;
	}

}
