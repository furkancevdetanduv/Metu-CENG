package com.pisti.client.model;

import com.pisti.client.constants.GameConstants;

public class Level3AI extends AI{
    Table gameTable;

    /**
     * Level 3 AI constructor with table
     * @param table Game table
     */
    Level3AI(Table table) {
        super();
        gameTable = table;
    }

    /**
     * AI chooses which card to play
     * @return Chosen card's index
     */
    @Override
    public int chooseCardToPlay() {
        int numberOfCardsInHand = getHand().size();
        int randomNumber = (int) (Math.random() * (numberOfCardsInHand - 1));
        for(int i = 0; i < getHand().size(); i++) {
            if(gameTable.getTableCards().size() > 0) {
                if(getHand().get(i).getId() == gameTable.getTableCards().get(
                        gameTable.getTableCards().size() - 1).getId()) {
                    return i;
                }
            }
            else if(getHand().get(i).getId() == GameConstants.JACK){
                for(Card card : gameTable.getTableCards()) {
                    if(card.getPoints() > 0) {
                        return i;
                    }
                }
            }
        }
        return randomNumber;
    }
}