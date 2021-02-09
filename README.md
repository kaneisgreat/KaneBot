# KaneBot
My own [Discord](https://discord.com/) bot coded using the [Discord4j](https://github.com/Discord4J/Discord4J), [LavaPlayer](https://github.com/sedmelluq/lavaplayer), 
[Log4j](https://logging.apache.org/log4j/2.x/), and [Project Reactor](https://projectreactor.io/) libraries. This is a project I did for fun and is for my own personal use. 

*** There is a handshake issue with my bot and the Discord server that I am looking into 2/1/2021. ***

## Functionality
* Create messages in channels
* Play music from provided links
* Play the card game, War, with another mentioned user
### Commands
To initiate a command start all commands with "[k]"
Here is the list of all currently availabe commands:
#### Create Messages
* ping
* ruhullah
* frank
* milan
* dave
#### Music
* join
* disconnect
* play
* pause
* resume
* currentSong
#### Card Games
* war
   * play
   * cardsLeft
## Command Details and Usage
### Create Message Commands
    [k]<command>      //KaneBot will respond with the corresponding message.
### Music Commands
    [k]join           //KaneBot will join the current channel you are in
    [k]disconnect     //KaneBot will disconnect from the current channel it is in
    [k]play <link>    //KaneBot will play the provided link as audio if valid. Can be a link to a video or a playlist.
    [k]pause          //KaneBot will pause the current playing track
    [k]resume         //KaneBot will resume from where it left off
    [k]currentSong    //KaneBot will tell you the current song being played
### Card Games
    [k]war <user>     //starts a game of War with the valid user provided. ***You must start a game of war before using any of the War Game commands.
#### War Game
    [k]play           //signals that you are ready to play your card. If both players are ready then the cards will be played.
    [k]cardsLeft      //KaneBot will tell you how many cards you have left in your deck
## Dependencies
This project uses Java 8+ and all dependencies were handled using Maven.
* Discord4j
   * Version 3.1.3
* LavaPlayer
   * Version 1.3.67
* Log4j
   * Version 1.7.5
* Project Reactor
   * Version 3.4.2
## Future Plans
### Music
I want to add in more functionality for to the music side of my bot. Some things that I will be looking to add in the future are skipping tracks, current track length, and clearing the current track's playlist.
### Card Games
I will be looking to add in more advanced card games such as Poker and BlackJack. Additionally, I will improve upon the current War Game by having KaneBot send card images instead of strings (ex. an actual picture of a king of spades vs "king of spades").
### Others
Will fix bugs as I find them.
