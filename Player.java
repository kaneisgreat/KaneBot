package cardgame;
import java.util.ArrayList;
import java.util.Optional;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;

//player class for the players involved in a game
public class Player {
	//players hand
	private ArrayList<Card> hand = new ArrayList<Card>();
	
	//discord user associated to this player
	private final User user;
	
	//the snowflake id of the player
	private final Snowflake id;
	
	// variable to see if player is ready to play card
	private boolean ready;
	
	//provide discord user and their snowflake id to create a player
	public Player(User user, Snowflake id) {
		this.user = user;
		this.id = id;
		this.ready = false;
	}
	
	//returns hand or deck depending on the game
	public ArrayList<Card> getHand(){
		return this.hand;
	}
	
	//add a card to their hand or deck
	public void addToHand(Card card, int i) {
		this.hand.add(i, card);
	}
	
	//play a card from their hand or deck
	public Card play(Card card) {
		if(this.isReady()) {
			return this.hand.remove(this.hand.indexOf(card));
		} else {
			this.setReady(true);
			return null;
		}
	}
	
	//gets the cards left in the players hand
	public int cardsLeft() {
		return this.hand.size();
	}
	
	//getter for the user
	public User getUser(){
		return this.user;
	}
	
	//getter for the snowflake id
	public Snowflake getId() {
		return this.id;
	}
	
	//getter to see if player is ready
	public boolean isReady() {
		return this.ready;
	}
	
	//setter to set player being ready
	public void setReady(boolean r) {
		this.ready = r;
	}
}
