import java.util.*;

public class Poker {
    private final String[] SUITS = { "C", "D", "H", "S" };
    private final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "T", "J", "Q", "K" };

    private final Player player;
    private List<Card> deck;
    private static Scanner in = new Scanner(System.in);
    private static int chips = 0;

    public Poker() {
        this.player = new Player();
    }

    public void play() {

        int bet = 0;
        if (chips == 0) {
            System.out.println("You have no chips. Enter the number of chips to buy in or stop the program to exit.");

            while (true) {
                try {
                    chips = Poker.in.nextInt();
                }
                catch (Exception e) {

                }
                if (chips > 0) {
                    break;
                }
                System.out.println("That is not a valid number of chips!");
                in.nextLine();
            }
        }
        System.out.println("Enter the number of chips to bet.");
        System.out.printf("Current chips: %d%n", chips);

        while (true) {
            try {
                bet = in.nextInt();
            }
            catch (Exception e) {

            }
            if (bet > 0 && bet < 26 && bet <= chips) {
                break;
            }

            System.out.println("That is not a valid number of chips! (must be between 1 and 25 and less than or equal to your current chips)");
            in.nextLine();
        }
        chips -= bet;
        // play the game until someone wins
        shuffleAndDeal();
        showHand();
        takeTurn();
        showHand();
        System.out.println("Winnings: " + bet*returnWinnings());
        chips += bet * returnWinnings();
    }

    public void shuffleAndDeal() {
        if (deck == null) {
            initializeDeck();
        }
        Collections.shuffle(deck);  // shuffles the deck
        while (player.getHand().size() < 5) {
            player.takeCard(deck.remove(0));
        }
    }

    ////////// PRIVATE METHODS /////////////////////////////////////////////////////

    private void initializeDeck() {
        deck = new ArrayList<>(52);

        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(new Card(rank, suit));     // adds 52 cards to the deck (13 ranks, 4 suits)
            }
        }
    }

    private void takeTurn() {

        System.out.printf("Enter the number of cards to trade. You may only trade up to three.%n");
        int choice = -1;
        while (true){
            try {
                choice = Poker.in.nextInt();
            }
            catch (Exception e) {

            }
            if (choice <= 3 && choice >= 0) {
                break;
            }
            System.out.println("Enter the number of cards to trade. You may only trade up to three.");
            Poker.in.nextLine();
        }
        if (choice != 0) {
            System.out.println("Enter the cards to trade. Separate each card entered with a space.");
            in.nextLine();
            while (true) {
                String cardstotrade = Poker.in.nextLine().toUpperCase();
                if (checkValidCards(cardstotrade, choice) == false || player.removeCards(cardstotrade) == false){
                    System.out.println("Invalid or too few cards. Enter the cards to trade. Separate each card entered with a space.");
                }
                else {
                    break;
                }
            }
            for (int i = 0; i < choice; i++) {
                player.takeCard(deck.remove(0));
            }
        }
    }

    private boolean checkValidCards(String cardstocheck, int choice) {
        String[] cardsArr = cardstocheck.split(" ");
        Card tempcard = new Card("2", "H"); //need this to use card method
        if (choice != cardsArr.length) {
            return false;
        }
        for (String card : cardsArr) { //checks for dupes
            for (String cardsCheck: cardsArr) {
                if (cardsCheck == card) {
                    continue;
                }
                else if (cardsCheck.equals(card)) {
                    return false;
                }
            }
        }
        for (String card : cardsArr) { //checks each card to see if it is actually a card
            if (card.length() != 2 || tempcard.getCardByRank(String.valueOf(card.charAt(0))) == null
            || tempcard.getOrderedSuit(String.valueOf(card.charAt(1))) == -1) {
                return false;
            }
        }
        return true;
    }

    private int returnWinnings() {
        boolean sameSuits = sameSuits();
        boolean straight = checkStraight();

        if (straight == true && sameSuits == true && player.getHand().get(0).getRank() == "T") {
            return 100;
        }
        else if (straight == true && sameSuits == true) {
            return 50;
        }
        else if (straight == true) {
            return 5;
        }
        else if (sameSuits == true) {
            return 10;
        }
        Map<String, Integer> matches = new HashMap<String, Integer>(); //this part checks and temp stores pairs
        for (Card card : player.getHand()) {
            int numberofmatches = 1;
            for (Card testcard : player.getHand()) {
                if (card == testcard) {
                    continue;
                }
                else if (card.getRank().equals(testcard.getRank())) {
                    numberofmatches++;
                }
            }
            if (numberofmatches > 1) {
                matches.put(card.getRank(), numberofmatches);
            }
        }

        if (matches.isEmpty()){
            return 0;
        }
        boolean iterated = false;
        for (String i : matches.keySet()) {
           if (matches.get(i) == 4){
               return 25;
           }
           else if (matches.get(i) == 3 && matches.size() == 2){
               return 15;
           }
           else if (matches.get(i) == 3 && matches.size() == 1){
               return 3;
           }
           else if (matches.get(i) == 2){
               if (matches.size() == 1 && Card.getOrderedRank(i) < 11){
                   return 0;
               }
               else if (matches.size() == 1) {
                   return 1;
               }
               else if (matches.size() == 2 && iterated == true){
                   return 2;
               }
           }
           iterated = true;
        }
        return 0;
    }

    private boolean checkStraight() {
        for (int i = 1; i < player.getHand().size(); i++) {
            if (Integer.parseInt(player.getHand().get(i).getRank()) == Integer.parseInt(player.getHand().get(i-1).getRank()) + 1){
                continue;
            }
            else {
                return false;
            }
        }
        return true;
    }
    private void showHand() {
        System.out.println("\nPLAYER hand: " + player.getHand());
    }

    private boolean sameSuits() {
        String suit = player.getHand().get(0).getSuit();
        for (Card card : player.getHand()) {
            if (suit.equals(card.getSuit())) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    ////////// MAIN METHOD /////////////////////////////////////////////////////////

    public static void main(String[] args) {
        System.out.println("Poker");
        System.out.println("Enter the number of chips to buy in: ");
        while (true) {
            try {
                chips = Poker.in.nextInt();
            }
            catch (Exception e) {

            }
            if (chips > 0) {
                break;
            }
            System.out.println("That is not a valid number of chips!");
            in.nextLine();
        }

        while (true) {
            new Poker().play();
        }
    }
}