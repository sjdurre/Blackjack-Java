public class Player {
    private final String name;
    private double bankroll;
    private final Hand hand = new Hand();

    public Player(String name, double startingBalance) {
        this.name = name;
        this.bankroll = startingBalance;
    }

    public String getName() {
        return name;
    }

    public double getBankroll() {
        return bankroll;
    }

    public Hand getHand() {
        return hand;
    }

    public void win(double amount) {
        bankroll += amount;
    }

    public void lose(double amount) {
        bankroll -= amount;
    }

    public void push() {
        // no change on push
    }

    public void clearHand() {
        hand.clear();
    }
}