package cardgame;

import java.util.Map;
import java.util.Optional;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import me.kaneisgreat.Command;
import reactor.core.publisher.Mono;

public class War extends Game {
	//create a deck of cards that others cannot see
	private Deck deck = new Deck();
	
	//variable to see if the game is ongoing
	boolean inSession;
	
	//boolean variable to see if war condition has been met
	private boolean warInit = false;
	
	//boolean variable to see who won when war condition is triggered
	private boolean p1WinWar = false;
	private boolean p2WinWar = false;
	
	//Player info will not change and should not be visible to others
	private final Player p1;
	private final Player p2;
	
	public War(Player p1, Player p2) {
		//set the players into the current game
		this.p1 = p1;
		this.p2 = p2;
		
		//war game is created but not started
		this.inSession = false;
	}
	
	@Override
	public void deal() {
		//deal out the cards evenly to both players, size will decrease as it is dealing
		for(int i = 0; i < deck.size(); i++) {
			p1.addToHand(deck.deal(), 0);
			p2.addToHand(deck.deal(), 0);
		}
	}
	
	//start the game of war
	@Override
	public void start() {
		//randomize the deck
		deck.shuffle();
		
		//deal cards to both players
		this.deal();
		
		//game is in session
		this.inSession = true;
	}
	
	//check if game is ongoing
	public boolean onGoing() {
		return this.inSession;
	}
	
	//check if someone has won (a player has 0 cards)
	public boolean gameOver() {
		//if either player has no cards in hand the game is over else its still going
		if(p1.getHand().size() == 0 || p2.getHand().size() == 0) {
			this.inSession = false;
			return true;
		} else {
			return false;
		}
	}

	//compare card strength
	public int compare(Card c1, Card c2) {
		/*if c1 value is greater then the value returned is greater than 0
		 * if less then value is less than 0
		 * if equal then value is 0 */
		return (c1.getValue()).compareTo(c2.getValue());
	}
	
	//when both users are ready to play their card then play their cards
	public Mono<Void> play(Optional<User> user, Snowflake snowflake, Mono<MessageChannel> mono) {
		//set who ever issued the play command to be ready
		if(snowflake.equals(p1.getId())){
			p1.setReady(true);
		}
		if(snowflake.equals(p2.getId())){
			p2.setReady(true);
		}
		
		//if both players are ready then play their top card
		if(p1.isReady() && p2.isReady()) {
			Card c1 = p1.play(p1.getHand().get(0));
			Card c2 = p2.play(p2.getHand().get(0));
			
			//state what each player has played
			((MessageChannel) mono).createMessage("p1 has played: " + c1.toString());
			((MessageChannel) mono).createMessage("p2 has played: " + c2.toString());
			
			//compare the cards and see what is higher
			if(compare(c1, c2) > 0) {
				//notify that player 1 has won
				((MessageChannel) mono).createMessage(c1.toString() + " is bigger than " + 
						c2.toString() + " so " + p1.getUser().toString() + " wins");
				
				//add cards to the end of player 1's hand
				p1.addToHand(c1, p1.cardsLeft() - 1);
				p1.addToHand(c2, p1.cardsLeft() - 1);
			} else if (compare(c1,c2) == 0) {
				/*WAR condition so play three cards face down and then play() again*/
				//notify channel war has begun
				((MessageChannel) mono).createMessage(c1.toString() + " is equal to "  + c2.toString() + "!!!");
				((MessageChannel) mono).createMessage("That means WAR! 1... 2... 3... WAR!!!!!!!!!!!!");
				
				//set that the condition has been met
				this.warInit = true;
				
				//make sure there are enough cards else the player without enough cards loses
				if(p1.cardsLeft() < 4) {
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " does not have enough cards left and has lost");
					((MessageChannel) mono).createMessage(p2.getUser().toString() + " has won!");
					this.inSession = false;
				}
				if(p2.cardsLeft() < 4) {
					((MessageChannel) mono).createMessage(p2.getUser().toString() + " does not have enough cards left and has lost");
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " has won!");
					this.inSession = false;
				}
				
				//place cards
				Card fd11 = p1.play(p1.getHand().get(0));
				Card fd12 = p1.play(p1.getHand().get(0));
				Card fd13 = p1.play(p1.getHand().get(0));
				
				Card fd21 = p2.play(p2.getHand().get(0));
				Card fd22 = p2.play(p2.getHand().get(0));
				Card fd23 = p2.play(p2.getHand().get(0));
				
				//play() another card again to compare with the potential for 2nd war
				this.play(user, snowflake, mono);
				
				if(this.p1WinWar) {
					//set to false again for next war
					this.p1WinWar = false;
					
					//Notify that p1 won war
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " has won the war!!!");
					
					//show p1's cards
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " had the cards: " 
							+ fd11.toString() + ", " + fd12.toString() + ", " + fd13.toString());
					//show p2's cards
					((MessageChannel) mono).createMessage(p2.getUser().toString() + " had the cards: " 
							+ fd21.toString() + ", " + fd22.toString() + ", " + fd23.toString());
					
					//add the cards into p1's hand
					p1.addToHand(c1, p1.cardsLeft() - 1);
					p1.addToHand(c2, p1.cardsLeft() - 1);
					p1.addToHand(fd11, p1.cardsLeft() - 1);
					p1.addToHand(fd12, p1.cardsLeft() - 1);
					p1.addToHand(fd13, p1.cardsLeft() - 1);
					p1.addToHand(fd21, p1.cardsLeft() - 1);
					p1.addToHand(fd22, p1.cardsLeft() - 1);
					p1.addToHand(fd23, p1.cardsLeft() - 1);
				} else {
					//set to false again for next war
					this.p2WinWar = false;
					
					//Notify that p2 won war
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " has won the war!!!");
					
					//show p2's cards
					((MessageChannel) mono).createMessage(p2.getUser().toString() + " had the cards: " 
							+ fd21.toString() + ", " + fd22.toString() + ", " + fd23.toString());
					//show p2's cards
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " had the cards: " 
							+ fd11.toString() + ", " + fd12.toString() + ", " + fd13.toString());
					
					//add the cards into p1's hand
					p2.addToHand(c1, p2.cardsLeft() - 1);
					p2.addToHand(c2, p2.cardsLeft() - 1);
					p2.addToHand(fd11, p2.cardsLeft() - 1);
					p2.addToHand(fd12, p2.cardsLeft() - 1);
					p2.addToHand(fd13, p2.cardsLeft() - 1);
					p2.addToHand(fd21, p2.cardsLeft() - 1);
					p2.addToHand(fd22, p2.cardsLeft() - 1);
					p2.addToHand(fd23, p2.cardsLeft() - 1);
				}
				
				//set war condition to false for future use
				this.warInit = false;
				
			} else {
				//notify that player 2 has won
				((MessageChannel) mono).createMessage(c2.toString() + " is bigger than " + 
						c1.toString() + " so " + p2.getUser().toString() + " wins");
				
				//add cards to the end of player 2's hand
				p2.addToHand(c1, p2.cardsLeft() - 1);
				p2.addToHand(c2, p2.cardsLeft() - 1);
			}
			
			//check if a player has won or not and if so end the game
			if(this.gameOver()) {
				//state which player won
				if(p1.cardsLeft() > 0) {
					((MessageChannel) mono).createMessage(p1.getUser().toString() + " has won!");
				} else {
					((MessageChannel) mono).createMessage(p2.getUser().toString() + " has won!");
				}
				this.inSession = false;
			}
			
		} else {
			//if p1 is not ready notify the channel
			if(!p1.isReady()) {
				((MessageChannel) mono).createMessage("p1 is not ready");
			}
			
			//if p2 is not ready notify the channel
			if(!p2.isReady()) {
				((MessageChannel) mono).createMessage("p2 is not ready");
			}
		}
		return null;
	}
	
	//return the cards left in a player's hands
	public Mono<Void> playerCardsLeft(Snowflake id, Mono<MessageChannel> mono){
		//if snowflake is player 1 then return player 1 cards left
		if(id.equals(p1.getId())) {
			((MessageChannel) mono).createMessage("Player 1 has " + p1.cardsLeft() + " cards left");
			
		//if its player 2's then return player 2's cards left
		} else if(id.equals(p2.getId())){
			((MessageChannel) mono).createMessage("Player 2 has " + p2.cardsLeft() + " cards left");
			
		//if not a player tell them they are not a player
		} else {
			((MessageChannel) mono).createMessage("You are not a player in the current session");
		}
		return null;
	}
	
	//add in all the commands to play a game of War
	public void initCommands(Map<String, Command> commands) {
		//command to play cards in the game of war
	    commands.put("play", event -> play(event.getMessage().getAuthor(), event.getMessage().getId(), event.getMessage().getChannel())
	    		.then());
	    
	    //command to see how many cards you have left
	    commands.put("play", event -> playerCardsLeft(event.getMessage().getId(), event.getMessage().getChannel())
	    		.then());
	}

}
