package com.clap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class Phrase {
	private String lessonId;
	private String phraseId;
	private String phraseText;
	private String translatedText;
	private String audioURL;
	private String audioLocation;

	public Phrase(String inLessonId,
			String inPhraseId,
			String inPhraseText,
			String inTranslatedText,
			String inAudioURL,
			String inAudioLoc) {
		lessonId = inLessonId;
		phraseId = inPhraseId;
		phraseText = inPhraseText;
		translatedText = inTranslatedText;
		audioURL = inAudioURL;
		audioLocation = inAudioLoc;
	}

	public String getLessonId() {
		return lessonId;
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

	public String getAudioLocation() {
		return audioLocation;
	}

	public void downloadAudio() throws MalformedURLException, IOException {
		File audioFile = new File(audioLocation);
		if (!audioFile.exists()) {
			if (!audioFile.createNewFile()) {
				throw new IOException("Could Not Create Audio File: " + audioLocation);
			}
			WebAPI.DownloadAndSaveAudio(audioURL, audioFile);
		}
	}
}