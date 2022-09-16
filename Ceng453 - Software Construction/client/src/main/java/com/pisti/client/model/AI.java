package com.pisti.client.model;

public abstract class AI extends Player{

    /**
     * AI Constructor that uses Player's constructor
     */
    AI() {
        super();
    }

    /**
     * AI chooses which card to play
     * @return Chosen card's index
     */
    public int chooseCardToPlay() {
        return -1;
    }
}
