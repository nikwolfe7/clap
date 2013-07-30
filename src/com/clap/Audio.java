package com.clap;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Audio implements OnCompletionListener {
    MediaPlayer mediaPlayer;
    Phrase phrase;
    boolean isPrepared = false;
     
    public Audio(Phrase p){
        mediaPlayer = new MediaPlayer();
        phrase = p;
        try{
        	p.downloadAudio();
            mediaPlayer.setDataSource(p.getAudioLocation());
            mediaPlayer.prepare();
            isPrepared = true;
            mediaPlayer.setOnCompletionListener(this);
        } catch(Exception ex){
        	throw new RuntimeException("Exception Loading Phrase \'" + phrase.getPhraseText() + "\':\n" + ex.getMessage());
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
        try{
            synchronized(this){
                if(!isPrepared){
                    mediaPlayer.prepare();
                }
                mediaPlayer.start();
            }
        } catch(IllegalStateException ex){
            ex.printStackTrace();
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
 
    public void stop() {
        mediaPlayer.stop();
        synchronized(this){
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
     
    public boolean isLooping() {
        return mediaPlayer.isLooping();
    }
     
    public void setLooping(boolean isLooping) {
        mediaPlayer.setLooping(isLooping);
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