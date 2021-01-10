import java.util.ArrayList;
import java.util.List;

public class Player {

    private final List<Card> hand;

    public Player() {
        this.hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

    public void takeCard(Card card) {
        hand.add(card);
        sortHand();
    }

    private int findCard(Card card) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getRank().equals(card.getRank()) && hand.get(i).getSuit().equals(card.getSuit())) {     // find card
                return i;
            }
        }

        return -1;
    }

    private void sortHand() {
        hand.sort((a, b) -> {
            if (Card.getOrderedRank(a.getRank()) == Card.getOrderedRank(b.getRank())) {
                return Card.getOrderedSuit(a.getSuit()) - Card.getOrderedSuit(b.getSuit());     // order by suit if
            }                                                                                   // ranks are the same

            return Card.getOrderedRank(a.getRank()) - Card.getOrderedRank(b.getRank());         // otherwise, by rank
        });
    }

    public boolean removeCards(String cardstoremove) {
        String[] cardArr = cardstoremove.split(" ");
        for (String card : cardArr) {
            Card tempcard = new Card(String.valueOf(card.charAt(0)), String.valueOf(card.charAt(1))); //converts string to card
            int temp = findCard(tempcard); //for cpu efficiency
            if (findCard(tempcard) != -1) {
                hand.remove(findCard(tempcard));
            }else {
                return false;
            }
        }
        return true;
    }
}