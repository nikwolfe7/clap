package com.clap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Phrase {
	private String phraseId;
	private String phraseText;
	private String translatedText;
	private String audioURL;
	private Lesson lesson;
	private String audioLocation;

	// Constructors
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
		int temp = audioURL.lastIndexOf("/");
		audioLocation = lesson.getDirectoryName() + "/"
				+ audioURL.substring(temp + 1);
	}

	public Phrase(String id, String pText, String tText, String url, Lesson phraseLesson) {
		phraseId = id;
		phraseText = pText;
		translatedText = tText;
		audioURL = url;
		lesson = phraseLesson;
		int temp = audioURL.lastIndexOf("/");
		audioLocation = lesson.getDirectoryName() + "/"
				+ audioURL.substring(temp + 1);
	}

	public Phrase(String id, String pText, String tText, String url) {
		phraseId = id;
		phraseText = pText;
		translatedText = tText;
		audioURL = url;
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

	public void downloadAudioFile() throws MalformedURLException, IOException {
		File audioFile = new File(audioLocation);
		if (!audioFile.exists()) {
			if (!audioFile.createNewFile()) {
				throw new IOException("Could Not Create New File");
			}
			WebAPI.DownloadAndSaveAudio(audioURL, audioFile);
		}
	}
}