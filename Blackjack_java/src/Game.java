import java.util.Scanner;
import java.io.*;


public class Game {
    private final Scanner scanner = new Scanner(System.in);
    private final Deck deck = new Deck();
    private final Dealer dealer = new Dealer();
    private Player player;
    private boolean running = true;

    // ======== GAME START ========
    public void start() {
        System.out.print("Enter your player name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "Player";
        }

        double savedBank = loadBank(name);
        player = new Player(name, savedBank);

        System.out.println("\nWelcome to Blackjack, " + player.getName() + "!");
        System.out.println("Type 'exit' anytime to quit.");
        System.out.println("Loaded balance: $" + player.getBankroll());

        while (running && player.getBankroll() > 0) {
            playRound();
            if (!running) break; // player exited during round

            System.out.print("\nPlay another round? (y/n/exit): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("exit") || choice.equals("n")) {
                exitGame();
                break;
            }
        }

        if (player.getBankroll() <= 0) {
            System.out.println("\nYou’ve run out of money!");
            exitGame();
        }
    }

    // ======== MAIN ROUND ========
    private void playRound() {
        player.clearHand();
        dealer.clearHand();

        double bet = getBetAmount();
        if (!running) return; // player exited at bet prompt

        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());
        player.getHand().addCard(deck.dealCard());
        dealer.getHand().addCard(deck.dealCard());

        System.out.println("\nDealer's Hand: " + dealer.getHand().getCards().get(0) + " and [Hidden]");
        System.out.println(player.getName() + "'s Hand: " + player.getHand());

        if (player.getHand().isBlackjack()) {
            System.out.println("Blackjack! You win 1.5x your bet!");
            player.win(bet * 1.5);
            saveBank(player.getName(), player.getBankroll());
            return;
        }

        // ===== Player turn =====
        while (true) {
            System.out.print("Hit or Stand (h/s/exit): ");
            String choice = scanner.nextLine().trim().toLowerCase();

            if (choice.equals("exit")) {
                exitGame();
                return;
            }

            if (choice.equals("h")) {
                player.getHand().addCard(deck.dealCard());
                System.out.println(player.getName() + "'s Hand: " + player.getHand());
                if (player.getHand().isBust()) {
                    System.out.println("Bust! You lose $" + bet);
                    player.lose(bet);
                    saveBank(player.getName(), player.getBankroll());
                    return;
                }
            } else if (choice.equals("s")) {
                break;
            }
        }

        // ===== Dealer turn =====
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

        saveBank(player.getName(), player.getBankroll());
    }

    // ======== BETTING ========
    private double getBetAmount() {
        while (true) {
            System.out.print("\nYour balance: $" + player.getBankroll() + " | Enter bet amount: ");
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("exit")) {
                exitGame();
                return 0;
            }

            try {
                double bet = Double.parseDouble(input);
                if (bet > 0 && bet <= player.getBankroll()) {
                    return bet;
                } else {
                    System.out.println("Invalid bet amount.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number or type 'exit'.");
            }
        }
    }

    // ======== BANK FILE MANAGEMENT ========
    private double loadBank(String playerName) {
        String filename = getBankFilename(playerName);
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine();
            if (line != null) {
                return Double.parseDouble(line);
            }
        } catch (IOException | NumberFormatException e) {
            // No existing file, start new
        }
        return 1000.0; // Default starting bankroll
    }

    private void saveBank(String playerName, double amount) {
        String filename = getBankFilename(playerName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.valueOf(amount));
        } catch (IOException e) {
            System.out.println("Error saving bankroll: " + e.getMessage());
        }
    }

    private String getBankFilename(String playerName) {
        // Sanitize name to avoid invalid filename characters
        String safeName = playerName.replaceAll("[^a-zA-Z0-9_-]", "_");
        return safeName + "_bank.txt";
    }

    // ======== EXIT ========
    private void exitGame() {
        running = false;
        saveBank(player.getName(), player.getBankroll());
        System.out.println("\nSaving bankroll...");
        System.out.println("Goodbye, " + player.getName() + "! Your balance of $" + player.getBankroll() + " has been saved.");
        System.exit(0);
    }
}
