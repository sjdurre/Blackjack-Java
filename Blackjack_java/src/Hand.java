import java.util.ArrayList;
import java.util.List;


public class Hand {
    private final List<Card> cards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public int getTotal() {
        int total = 0;
        int aces = 0;

        for (Card card : cards) {
            total += card.getValue();
            if (card.toString().contains("Ace")) {
                aces++;
            }
        }

        // Adjust Aces (11 → 1) if needed
        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : cards) {
            sb.append(card).append(", ");
        }
        sb.append("Total: ").append(getTotal());
        return sb.toString();
    }

    public boolean isBust() {
        return getTotal() > 21;
    }

    public boolean isBlackjack() {
        return getTotal() == 21 && cards.size() == 2;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void clear() {
        cards.clear();
    }
}