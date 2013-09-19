package com.clap;

import android.os.Bundle;

public class HelpActivity extends ClapActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		title = "Applause Help";
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
	}
}
