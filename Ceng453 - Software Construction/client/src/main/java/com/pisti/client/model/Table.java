package com.pisti.client.model;

import com.pisti.client.constants.GameConstants;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Collections;

public class Table {
    private ArrayList<Card> tableCards;
    private ArrayList<Card> deckCards;
    private final ArrayList<Card> cardSet;

    private final User player;
    private AI ai;

    private boolean isLevelOver;

    /**
     * Table constructor that initializes the table
     * @param deckCards Deck of shuffled cards as Arraylist
     * @param AILevel Level of the AI
     */
    public Table(ArrayList<Card> deckCards, int AILevel) {
        this.deckCards = deckCards;
        tableCards = new ArrayList<>();
        cardSet = new ArrayList<>();
        player = new User(-1);
        if(AILevel == GameConstants.LEVEL1) {
            ai = new Level1AI();
        }
        else if (AILevel == GameConstants.LEVEL2) {
            ai = new Level2AI(this);
        }
        else if (AILevel == GameConstants.LEVEL3) {
            ai = new Level3AI(this);
        }
        cardSet.addAll(deckCards);
        isLevelOver = false;
    }

    /**
     * Table initializer that serves the cards to table,
     * player and AI
     */
    public void initializeTable() {
        for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
            tableCards.add(deckCards.get(0));
            deckCards.remove(0);
        }
        for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
            player.addCardToHand(deckCards.get(0));
            deckCards.remove(0);
        }
        for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
            ai.addCardToHand(deckCards.get(0));
            deckCards.remove(0);
        }
    }

    /**
     * Player getter
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * AI getter
     * @return AI
     */
    public AI getAI() {
        return ai;
    }

    /**
     * Table cards getter
     * @return ArrayList of table cards
     */
    public ArrayList<Card> getTableCards() {
        return tableCards;
    }

    /**
     * Plays the card to the table and checks if someone has won the cards
     * Checks the deck at the end
     * @param who Who played the card
     * @param card Card that is played
     */
    public void playCard(String who, Card card) {
        if(tableCards.size() == 0) {
            tableCards.add(card);
            checkAndDealCards();
            return;
        }
        if(card.getId() == GameConstants.JACK) {
            if(who.equals("Player")) {
                tableCards.add(card);
                player.winCards(tableCards);
                tableCards = new ArrayList<>();
            }
            else {
                tableCards.add(card);
                ai.winCards(tableCards);
                tableCards = new ArrayList<>();
            }
        }
        else if(card.getId() == tableCards.get(tableCards.size() - 1).getId()) {
            if(who.equals("Player")) {
                tableCards.add(card);
                player.winCards(tableCards);
                tableCards = new ArrayList<>();
            }
            else {
                tableCards.add(card);
                ai.winCards(tableCards);
                tableCards = new ArrayList<>();
            }
        }
        else {
            tableCards.add(card);
        }
        checkAndDealCards();
    }

    /**
     * Checks if the player and the AI has any cards left
     * and deals if none left; checks if the round is over
     */
    public void checkAndDealCards() {
        if(player.getHand().size() == 0 && ai.getHand().size() == 0) {
            if(deckCards.size() >= 8) {
                for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
                    player.addCardToHand(deckCards.get(0));
                    deckCards.remove(0);
                }
                for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
                    ai.addCardToHand(deckCards.get(0));
                    deckCards.remove(0);
                }
            }
            else {
                deckOver();
            }
        }
    }

    /**
     * Called when deck has no cards left
     * Either reserves the cards or ends the round based on the scores
     */
    public void deckOver() {
        player.winCards(deckCards);
        player.calculateScore();
        ai.calculateScore();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Scores");
        alert.setHeaderText("Current Scores");
        alert.setContentText("Your Score: " + player.getScore() + "\nEnemy Score: " + ai.getScore());
        alert.showAndWait();
        if(ai.getScore() < GameConstants.SCORE_TO_WIN && player.getScore() < GameConstants.SCORE_TO_WIN) {
            deckCards = new ArrayList<>();
            deckCards.addAll(cardSet);
            Collections.shuffle(deckCards);
            initializeTable();
        }
        else {
            isLevelOver = true;
        }
    }

    /**
     * Checks if the round is over
     * @return True if round is over, false otherwise
     */
    public boolean isOver() {
        return isLevelOver;
    }
}
