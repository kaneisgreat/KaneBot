package me.kaneisgreat;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

//an interface for Commands, which will be a Mono stream to implement a reactive system
public interface Command {
	//execute command based on corresponding event
	Mono<Void> execute(MessageCreateEvent event);
}
