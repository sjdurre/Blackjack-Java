public class Dealer {
    private final Hand hand = new Hand();

    public Hand getHand() {
        return hand;
    }

    public void play(Deck deck) {
        while (hand.getTotal() < 17) {
            hand.addCard(deck.dealCard());
        }
    }

    public void clearHand() {
        hand.clear();
    }
}