package com.clap;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Audio {
    private MediaPlayer mediaPlayer;
    private Phrase phrase;
    private boolean isPrepared = false;
     
    public Audio(Phrase p, OnCompletionListener l){
        mediaPlayer = new MediaPlayer();
        phrase = p;

        try {
			mediaPlayer.setDataSource(phrase.getAudioLocation());
	        mediaPlayer.prepare();
	        isPrepared = true;
	        mediaPlayer.setOnCompletionListener(l);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
 
    public String getPhraseText() {
    	return phrase.getPhraseText();
    }
    
    public String getTranslatedText() {
    	return phrase.getTranslatedText();
    }

    public void play() throws IllegalStateException, IOException {
        if(mediaPlayer.isPlaying()){
            return;
        }
        synchronized(this) {
            if(!isPrepared) {
                mediaPlayer.prepare();
            }
            mediaPlayer.start();
        }
    }
 
    public void stop() {
        mediaPlayer.stop();
        synchronized(this) {
            isPrepared = false;
        }
        mediaPlayer.release();
    }
     
    public void switchTracks(){
        mediaPlayer.seekTo(0);
        mediaPlayer.pause();
    }
     
    public void pause() {
        mediaPlayer.pause();
    }
 
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }
     
    public void setVolume(float volumeLeft, float volumeRight) {
        mediaPlayer.setVolume(volumeLeft, volumeRight);
    }
 
    public void dispose() {
        if(mediaPlayer.isPlaying()){
            stop();
        }
        mediaPlayer.release();
    }
}