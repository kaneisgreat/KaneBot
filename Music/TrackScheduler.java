package me.kaneisgreat;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.LinkedList;

public final class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler {
	//Create audio player to playback audio frames
    private final AudioPlayer player;
    //Create a list to hold the queue of songs
    LinkedList<AudioTrack> schedule = new LinkedList<AudioTrack>();
    
    public TrackScheduler(final AudioPlayer player) {
        this.player = player;
    }

    @Override
    public void trackLoaded(final AudioTrack track) {
        // LavaPlayer found an audio source for us to play
    	schedule.add(track);
    }

    @Override
    public void playlistLoaded(final AudioPlaylist playlist) {
    	//add all songs in a playlist to the queue
        for (AudioTrack track : playlist.getTracks()) {
            schedule.add(track);
        }
    }
    
    public void play() {
    	//play the song and remove it from the queue
    	player.playTrack(schedule.remove());
    }
    
    public void pause() {
    	//pause player
    	player.setPaused(true);
    }
    
    public void resume() {
    	//resume the player where it left off
    	player.startTrack(player.getPlayingTrack(), false);
    }
    
    public synchronized String currentSong() {
    	//retrieves the song currently being played and converts it to a string
    	return player.getPlayingTrack().toString();
    }
    @Override
    public void noMatches() {
        // LavaPlayer find no matches to the provided input
    	System.out.println("no matches found");
    }

    @Override
    public void loadFailed(final FriendlyException exception) {
        // LavaPlayer fails for some reason
    	System.out.println("FAILED");
    }
}
