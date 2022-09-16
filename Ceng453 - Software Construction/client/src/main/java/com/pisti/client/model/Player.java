package com.pisti.client.model;

import com.pisti.client.constants.GameConstants;

import java.util.ArrayList;

public class Player {
    private final ArrayList<Card> hand;
    private final ArrayList<ArrayList<Card>> collectedCards;
    int score;

    /**
     * Player constructor that initializes hand, collected cards and score
     */
    public Player() {
        hand = new ArrayList<>();
        collectedCards = new ArrayList<>();
        score = 0;
    }

    /**
     * Adds a card to the player's hand
     * @param card Card to add
     */
    public void addCardToHand(Card card) {
        hand.add(card);
    }

    /**
     * Plays a card from player's hand to the table
     * @param cardIndex Index of the card to be played
     * @return The card that is played
     */
    public Card playCard(int cardIndex) {
        Card card = hand.get(cardIndex);
        hand.remove(cardIndex);
        return card;
    }

    /**
     * Adds cards that are won to the collected cards
     * @param cardsWon ArrayList of cards that are won
     */
    public void winCards(ArrayList<Card> cardsWon) {
        collectedCards.add(cardsWon);
    }

    /**
     * Hand getter
     * @return List of Cards at the hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Score getter
     * @return Current score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Calculates the score from won cards and adds it to the player's score
     */
    public void calculateScore() {
        int scoreToAdd = 0;
        int amountOfCards = 0;
        for (ArrayList<Card> cardList : collectedCards) {
            for (Card card : cardList) {
                scoreToAdd += card.getPoints();
            }
            if(cardList.size() == 2 && cardList.get(0).getId() == cardList.get(1).getId()) {
                if(cardList.get(0).getId() == GameConstants.JACK) {
                    scoreToAdd += 20;
                }
                else {
                    scoreToAdd += 10;
                }
            }
            amountOfCards += cardList.size();
        }
        if(amountOfCards > 26) {
            scoreToAdd += 3;
        }
        score += scoreToAdd;
    }
}
