package com.clap;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {
	private String title = "Help";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		setTitle(title);
	}
}
