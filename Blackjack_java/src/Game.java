import java.util.Scanner;
import java.io.*;

public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Deck deck = new Deck();
    private final Dealer dealer = new Dealer();
    private final String BANK_FILE = "bank.txt";
    private final Player player = new Player("Player", 0);

    // Load and Save Bank
    private double loadBank() {
        try (BufferedReader reader = new BufferedReader(new FileReader(BANK_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                return Double.parseDouble(line);
            }
        } catch (IOException | NumberFormatException e) {
            // File not found or invalid format
        }
        return 1000.0; // default starting balance
    }

    private void saveBank(double amount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BANK_FILE))) {
            writer.write(String.valueOf(amount));
        } catch (IOException e) {
            System.out.println("Error saving bankroll: " + e.getMessage());
        }
    }

    // ======== GAME LOOP ========
    public void start() {
        double savedBank = loadBank();
        player.win(savedBank);
        System.out.println("Welcome to Blackjack!");
        System.out.println("Loaded balance: $" + player.getBankroll());

        boolean playing = true;
        while (playing && player.getBankroll() > 0) {
            playRound();
            System.out.print("\nPlay another round? (y/n): ");
            playing = scanner.nextLine().trim().equalsIgnoreCase("y");
        }

        saveBank(player.getBankroll());
        System.out.println("Game over! Final balance saved: $" + player.getBankroll());
    }

    private void playRound() {
        player.clearHand();
        dealer.clearHand();

        double bet = getBetAmount();
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());

        System.out.println("\nDealer's Hand: " + dealer.getHand().getCards().get(0) + " and [Hidden]");
        System.out.println("Your Hand: " + player.getHand());

        if (player.getHand().isBlackjack()) {
            System.out.println("Blackjack! You win 1.5x your bet!");
            player.win(bet * 1.5);
            return;
        }

        // Player turn
        while (true) {
            System.out.print("Hit or Stand (h/s): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("h")) {
                player.getHand().addCard(deck.dealCard());
                System.out.println("Your Hand: " + player.getHand());
                if (player.getHand().isBust()) {
                    System.out.println("Bust! You lose $" + bet);
                    player.lose(bet);
                    return;
                }
            } else if (choice.equals("s")) {
                break;
            }
        }

        // Dealer turn
        dealer.play(deck);
        System.out.println("\nDealer's Hand: " + dealer.getHand());

        int playerTotal = player.getHand().getTotal();
        int dealerTotal = dealer.getHand().getTotal();

        if (dealer.getHand().isBust()) {
            System.out.println("Dealer busts! You win $" + bet);
            player.win(bet);
        } else if (playerTotal > dealerTotal) {
            System.out.println("You win $" + bet + "!");
            player.win(bet);
        } else if (playerTotal < dealerTotal) {
            System.out.println("Dealer wins! You lose $" + bet);
            player.lose(bet);
        } else {
            System.out.println("Push! It's a tie.");
            player.push();
        }
    }

    private double getBetAmount() {
        while (true) {
            System.out.print("\nYour balance: $" + player.getBankroll() + " | Enter bet amount: ");
            try {
                double bet = Double.parseDouble(scanner.nextLine());
                if (bet > 0 && bet <= player.getBankroll()) {
                    return bet;
                } else {
                    System.out.println("Invalid bet amount.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
