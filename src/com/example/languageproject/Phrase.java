package com.example.languageproject;

import java.io.File;

public class Phrase {
	private String phraseId;
	private String phraseText;
	private String translatedText;
	private String audioURL;
	private Lesson lesson;
	private String audioLocation;

	public Phrase(String[] entry, Lesson phraseLesson) {
		try {
			phraseId = entry[0];
			phraseText = entry[1];
			translatedText = entry[2];
			audioURL = entry[3];
		} catch (Exception e) {
			e.printStackTrace();
		}
		lesson = phraseLesson;
		audioLocation = lesson.getDirectoryName() + "/" + phraseId;
	}

	// Constructors
	public Phrase(String id, String pText, String tText, String url, Lesson phraseLesson) {
		phraseId = id;
		phraseText = pText;
		translatedText = tText;
		audioURL = url;
		lesson = phraseLesson;
		int temp = audioURL.lastIndexOf("/");
		audioLocation = lesson.getDirectoryName() + "/" + audioURL.substring(temp + 1);
	}

	public String getPhraseId() {
		return phraseId;
	}

	public String getPhraseText() {
		return phraseText;
	}

	public String getTranslatedText() {
		return translatedText;
	}

	public String getAudioURL() {
		return audioURL;
	}
	
	public Lesson getLesson() {
		return lesson;
	}
	
	public String getAudioLocation() {
		return audioLocation;
	}
	
	public void downloadAudioFile() {
		File loc = new File(audioLocation);
		if (!loc.exists()) {
			WebAPI.DownloadAndSaveAudio(audioURL, audioLocation);
		}
	}
}