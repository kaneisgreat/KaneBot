package me.kaneisgreat;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.log4j.BasicConfigurator;
import org.reactivestreams.Subscription;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;

import cardgame.Player;
import cardgame.War;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Bot {
	//create a map to hold all the general and music bot commands
	private static Map<String, Command> commands = new HashMap<String, Command>();
	
	//create a map to hold all war commands
	private static Map<String, Command> warCommands = new HashMap<String, Command>();
	
	//create an audio provider class to provide audio to the bot
	public static class LavaPlayerAudioProvider extends AudioProvider {
		//audio player object
	    private final AudioPlayer player;
	    //audio frame that holds the audio data(OPUS frames)
	    private final MutableAudioFrame frame = new MutableAudioFrame();

	    public LavaPlayerAudioProvider(final AudioPlayer player) {
	        // Allocate a ByteBuffer to hold the OPUS frames for Discord
	        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
	        // Set the MutableAudioFrame object we created to use this ByteBuffer
	        frame.setBuffer(getBuffer());
	        this.player = player;
	    }

	    @Override
	    public boolean provide() {
	        // AudioPlayer writes audio data to its AudioFrame
	        final boolean didProvide = player.provide(frame);
	        // If audio is already provided then read the audio data instead of write
	        if (didProvide) {
	            getBuffer().flip();
	        }
	        return didProvide;
	    }
	}
	
	public static void main(String[] args) {
		//Sets up basic configurations for log4j to print out logs as the bot is running
		BasicConfigurator.configure();
		
		// Manager creates AudioPlayer instances and converts URLS to audio tracks to be added
		final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
		
		// An optimization strategy Discord4j uses when configuring
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		
		// Allows the player manager to use outside sources such as links to videos
		AudioSourceManagers.registerRemoteSources(playerManager);
		
		// Use the manager to create a player than can play audio tracks
		final AudioPlayer player = playerManager.createPlayer();
		
		// Create an AudioProvider and TrackScheduler. The provider will provide audio data for the scheduler to handle
		AudioProvider provider = new LavaPlayerAudioProvider(player);
		final TrackScheduler scheduler = new TrackScheduler(player);
		
		//Create client object that will establish a connection to Discord, using my bot's token
		final GatewayDiscordClient client = DiscordClientBuilder.create(args[0]).build().login().block();
		
		//Basic commands that make the bot send a particular message to the command issuer's channel. Mostly messages I made for friends
	    commands.put("ping", event -> event.getMessage().getChannel()
	            .flatMap(channel -> channel.createMessage("Pong!"))
	            .then());
	    commands.put("ruhullah", event -> event.getMessage().getChannel()
	            .flatMap(channel -> channel.createMessage("haha xD"))
	            .then());
	    commands.put("frank", event -> event.getMessage().getChannel()
	            .flatMap(channel -> channel.createMessage("you're in exp range"))
	            .then());
	    commands.put("testie", event -> event.getMessage().getChannel()
	            .flatMap(channel -> channel.createMessage("Talk to me when you have over 50 honbot rating lol."))
	            .then());
	    commands.put("milan", event -> event.getMessage().getChannel()
	            .flatMap(channel -> channel.createMessage("mannn milannnnn"))
	            .then());
	    commands.put("dave", event -> event.getMessage().getChannel()
	    		.flatMap(channel -> channel.createMessage("never question my every intellegince every again"))
	    		.then());
	    
	    //join the command issuer's channel
		commands.put("join", event -> Mono.justOrEmpty(event.getMember())
			    .flatMap(Member::getVoiceState)
			    .flatMap(VoiceState::getChannel)
			    // join returns a VoiceConnection which would be required if we were
			    // adding disconnection features, but for now we are just ignoring it.
			    .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
			    .then());
		
		//disconnects the bot from the current channel
		commands.put("disconnect", event -> Mono.justOrEmpty(event.getMember())
			    .flatMap(Member::getVoiceState)
			    .flatMap(VoiceState::getChannel)
			    .flatMap(channel -> channel.sendDisconnectVoiceState())
			    .then());
		
		//pause the current track being played
		commands.put("pause", event -> Mono.justOrEmpty(event.getMessage().getContent())
				.map(content -> Arrays.asList(content.split(" ")))
				.doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
				.then());
		
		//attempts to load provided audio content and play it back
		commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
		    .map(content -> Arrays.asList(content.split(" ")))
		    .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
		    .then());
		
		//Starts a game of war between 2 users
		commands.put("war", event -> warGame(event, client).then());
		System.out.println("command put in");
		
		//Event listener that listens for message that starts with '[k]' and will then look for corresponding action in the map
		client.getEventDispatcher().on(MessageCreateEvent.class)
	    .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
	        .flatMap(content -> Flux.fromIterable(commands.entrySet())
	            .filter(entry -> content.startsWith("[k]" + entry.getKey()))
	            .flatMap(entry -> entry.getValue().execute(event))
	            .next()))
	    .subscribe();
		

		//When disconnected block until next signal
		client.onDisconnect().block();
	}
	
	//sets up war game and start it
	public static Mono<Void> warGame(MessageCreateEvent event, GatewayDiscordClient client) {
		//listen for event message to start war
		Message message = event.getMessage();
		
		//get the message's channel to play war in
		MessageChannel channel = message.getChannel().block();

		//Initialize player 1 by getting the message author and the message snowflake id
		Player p1 = new Player(message.getAuthor().get(), message.getId());
		
		//initialize player 2 by getting the user mentioned and the corresponding snowflake id
		Player p2 = new Player(message.getUserMentions().blockFirst(), message.getUserMentionIds().iterator().next());
		
		//create war game
		War war = new War(p1, p2);
		
		//add in the commands for the wargame
		war.initCommands(warCommands);
		
		//start war game
		war.start();
		
		//send message to signal that war game has started
		channel.createMessage("War has started Start");
		
		//keep playing while war is in session
		while(war.onGoing()) {
			//event listener for war game specific to war game commands
			client.getEventDispatcher().on(MessageCreateEvent.class)
		    .flatMap(e -> Mono.justOrEmpty(e.getMessage().getContent())
		        .flatMap(content -> Flux.fromIterable(warCommands.entrySet())
		            .filter(entry -> content.startsWith("[k]" + entry.getKey()))
		            .flatMap(entry -> entry.getValue().execute(e))
		            .next()))
		    .subscribe();
			
		}
		    
		return null;
	}
}
