package com.clap;

import android.os.Bundle;

public class AboutActivity extends ClapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		title = "About Applause";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_activity);
	}
}
