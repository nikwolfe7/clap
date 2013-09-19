package com.clap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

// This class is used instead of TextView for the items in
// the ListViews so that if the user wants to change the text
// size, it is easy to update all the Activities
public class ListItemTextView extends TextView {
	private static float textSize = TEXT_SIZE.MEDIUM.getTextSizeFloat();
	
	// The map from String to float for the text size
	private static final Map<String, TEXT_SIZE> TEXT_SIZE_MAP = createMap();
	private static final Map<String, TEXT_SIZE> createMap() {
		HashMap<String, TEXT_SIZE> tempMap = new HashMap<String, TEXT_SIZE>();
		tempMap.put("Very Small", TEXT_SIZE.VERY_SMALL);
		tempMap.put("Small", TEXT_SIZE.SMALL);
		tempMap.put("Medium", TEXT_SIZE.MEDIUM);
		tempMap.put("Large", TEXT_SIZE.LARGE);
		tempMap.put("Very Large", TEXT_SIZE.VERY_LARGE);
		return Collections.unmodifiableMap(tempMap);
	}

	// The float values for each of the text sizes
	private enum TEXT_SIZE {
		VERY_SMALL((float)10.0),
		SMALL((float)15.0),
		MEDIUM((float)20.0),
		LARGE((float)25.0),
		VERY_LARGE((float)30.0);
		
		private float textSizeFloat;
		
		private TEXT_SIZE(float f) {
			this.textSizeFloat = f;
		}
		
		public float getTextSizeFloat() {
			return this.textSizeFloat;
		}
	}

	public ListItemTextView(Context context) {
		// call TextView constructor then set the text size
		super(context);
		this.setTextSize(textSize);
	}

	public ListItemTextView(Context context, AttributeSet attrs) {
		// call TextView constructor then set the text size
		super(context, attrs);
		this.setTextSize(textSize);
	}
	
	public ListItemTextView(Context context, AttributeSet attrs, int defStyle) {
		// call TextView constructor then set the text size
		super(context, attrs, defStyle);
		this.setTextSize(textSize);
	}
	
	public static void setUserTextSize(String size) {
		// Preference Activity gets the text size as a string
		// convert it to the float value
		TEXT_SIZE tSize = TEXT_SIZE.MEDIUM;
		if (TEXT_SIZE_MAP.containsKey(size)) {
			tSize = TEXT_SIZE_MAP.get(size);
		}
		textSize =  tSize.getTextSizeFloat();
	}
}