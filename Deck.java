package cardgame;
import java.util.ArrayList;
import java.util.Random;

public class Deck {
	//arraylist of cards is the deck
	private ArrayList<Card> deck = new ArrayList<Card>();
	
	//add in the cards for the deck
	public Deck() {
		for(Suit suit : Suit.values()) {
			for(Value value : Value.values()) {
				deck.add(new Card(value, suit));
			}
		}
	}
	
	//shuffle method to randomize the deck
    public void shuffle() {      
        Random rand = new Random();   
        for (int i = 0; i < this.deck.size(); i++) { 
            int r = i + rand.nextInt(52 - i); 
             //swapping the elements 
             Card temp = this.deck.get(r); 
             this.deck.set(r, this.deck.get(i)); 
             this.deck.set(i, temp);   
        } 
    }
    
    //deal the top card of the deck
    public Card deal() {
    	return this.deck.remove(0);
    }
    
    //add a card to the deck
    public void add(Card card) {
    	this.deck.add(card);
    }
    
    //returns the size of the deck
    public int size() {
    	return deck.size();
    }
}
