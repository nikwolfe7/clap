package com.clap;

import android.os.Bundle;

public class HelpActivity extends ClapActivity {
	private String title = "Help";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		setTitle(title);
	}
}
