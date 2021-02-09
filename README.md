# KaneBot
My own [Discord](https://discord.com/) bot coded using the [Discord4j](https://github.com/Discord4J/Discord4J), [LavaPlayer](https://github.com/sedmelluq/lavaplayer), 
and [Project Reactor](https://projectreactor.io/) libraries. This is a project I did for fun and is for my own personal use.

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
 #### Card Games
    * war
        -play
        -cardsleft
## Command Details and Usage
### Create Message Commands
    [k]<command>  //KaneBot will respond with the corresponding message.
### Music Commands
    [k]join           //KaneBot will join the current channel you are in
    [k]disconnect     //KaneBot will disconnect from the current channel it is in
    [k]play <link>    //KaneBot will play the provided link as audio if valid
    [k]pause          //KaneBot will pause the current playing track
    [k]resume         //KaneBot will resume from where it left off
### Card Games
    [k]war            //starts a game of War. ***You must start a game of war before using any of the War Game commands.
   #### War Game
    [k]play           //signals that you are ready to play your card. If both players are ready then the cards will be played.
    [k]cardsLeft      //KaneBot will tell you how many cards you have left in your deck
