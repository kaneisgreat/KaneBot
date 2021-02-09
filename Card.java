package cardgame;

//Standard card with suit and value
public class Card {
	
	//value and suit are private and unchanging
	private final Value value;
	private final Suit suit;
	
	public Card(Value value, Suit suit) {
		this.value = value;
		this.suit = suit;
	}
	
	public Value getValue() {
		return this.value;
	}
	
	public Suit getSuit() {
		return this.suit;
	}
	
	//show the card as a string in the forma t of "VALUE of SUIT", ex. "king of spade"
	public String toString() {
		String cardToString = "";
		cardToString += value.toString();
		cardToString += " of ";
		cardToString += suit.toString();
		return cardToString;
	}
}
