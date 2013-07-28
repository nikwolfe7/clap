package com.clap;

import android.app.Activity;
import android.os.Bundle;

public class AboutActivity extends Activity {
	private String title = "About";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setTitle(title);
	}
}
