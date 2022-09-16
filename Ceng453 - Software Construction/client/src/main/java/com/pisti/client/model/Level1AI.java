package com.pisti.client.model;

public class Level1AI extends AI{

    /**
     * AI chooses which card to play
     * @return Chosen card's index
     */
    @Override
    public int chooseCardToPlay() {
        int numberOfCardsInHand = getHand().size();
        return (int) (Math.random() * (numberOfCardsInHand - 1));
    }
}
