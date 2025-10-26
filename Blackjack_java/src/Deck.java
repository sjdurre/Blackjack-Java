import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        refillDeck();
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            System.out.println("Reshuffling deck...");
            refillDeck();
            shuffle();
        }
        return cards.remove(0);
    }

    private void refillDeck() {
        cards.clear();
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","Jack","Queen","King","Ace"};
        int[] values   = { 2,  3,  4,  5,  6,  7,  8,  9,  10,    10,     10,     10,    11 };

        for (String suit : suits) {
            for (int i = 0; i < ranks.length; i++) {
                cards.add(new Card(suit, ranks[i], values[i]));
            }
        }
    }
}