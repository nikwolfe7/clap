package com.clap;

import java.io.File;
import java.io.IOException;

import android.content.Context;

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

	public boolean audioFileExists() {
		File audioFile = new File(audioLocation);
		return audioFile.exists();
	}

	public void downloadAudio(Context c) throws IOException {
		File audioFile = new File(audioLocation);
		if (!audioFile.exists()) {
			// If the file doesn't exist yet we need to download it
			if (!audioFile.createNewFile()) {
				throw new IOException("Could Not Create File: \'" + audioFile.getName() + "\'");
			}
			WebAPI clapWebAPI = new WebAPI(c);
			clapWebAPI.DownloadAndSaveAudio(audioURL, audioFile);
		}
	}
}