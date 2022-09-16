package com.pisti.client.model;

public class Level2AI extends AI{
    Table gameTable;

    /**
     * Level 2 AI constructor with table
     * @param table Game table
     */
    Level2AI(Table table) {
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
        }
        return randomNumber;
    }
}
