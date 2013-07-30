package com.clap;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Audio implements OnCompletionListener {
    private MediaPlayer mediaPlayer;
    private Phrase phrase;
    private boolean isPrepared = false;
     
    public Audio(Phrase p, Context c){
        mediaPlayer = new MediaPlayer();
        phrase = p;
        try {
        	p.downloadAudio(c);

            mediaPlayer.setDataSource(p.getAudioLocation());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch(Exception e) {
        	throw new RuntimeException("Exception Loading Phrase \'" + phrase.getPhraseText() + "\':\n" + e.getMessage());
        }
    }
     
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        synchronized(this){
            isPrepared = false;
        }
    }
 
    public String getPhraseText() {
    	return phrase.getPhraseText();
    }
    
    public String getTranslatedText() {
    	return phrase.getTranslatedText();
    }

    public void play() {
        if(mediaPlayer.isPlaying()){
            return;
        }
        try {
            synchronized(this) {
                if(!isPrepared) {
                    mediaPlayer.prepare();
                }
                mediaPlayer.start();
            }
        } catch(IllegalStateException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
 
    public void stop() {
        mediaPlayer.stop();
        synchronized(this) {
            isPrepared = false;
        }
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