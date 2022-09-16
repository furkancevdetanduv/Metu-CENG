package com.pisti.client.model;

import com.pisti.client.constants.GameConstants;
import javafx.scene.image.Image;

public class Card {

    private final int id;
    private final String type;
    private int points = 0;
    private final Image image;

    /**
     * Card constructor that constructs it's id, type and image
     * Initializes it's points according to the card
     * @param id ID of the card
     * @param type Type of the card
     * @param image Image of the card
     */
    public Card(int id, String type, Image image) {
        this.id = id;
        this.type = type;
        switch (id) {
            case GameConstants.JACK:
                this.points = GameConstants.JACK_POINTS;
                break;
            case GameConstants.ACE:
                this.points = GameConstants.ACE_POINTS;
                break;
            case GameConstants.TWO:
                if(type.equals("clover")) {
                    this.points = GameConstants.TWO_OF_CLOVER_POINTS;
                }
                break;
            case GameConstants.TEN:
                if(type.equals("diamond")) {
                    this.points = GameConstants.TEN_OF_DIAMONDS_POINTS;
                }
        }
        this.image = image;
    }

    /**
     * Getter for id
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for points of the card
     * @return Points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Getter for image of the card
     * @return Image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Overridden toString implementation of the card
     * @return Name of the card
     */
    @Override
    public String toString() {
        String name = switch (id) {
            case GameConstants.ACE -> "ACE";
            case GameConstants.TWO -> "TWO";
            case GameConstants.THREE -> "THREE";
            case GameConstants.FOUR -> "FOUR";
            case GameConstants.FIVE -> "FIVE";
            case GameConstants.SIX -> "SIX";
            case GameConstants.SEVEN -> "SEVEN";
            case GameConstants.EIGHT -> "EIGHT";
            case GameConstants.NINE -> "NINE";
            case GameConstants.TEN -> "TEN";
            case GameConstants.JACK -> "JACK";
            case GameConstants.QUEEN -> "QUEEN";
            case GameConstants.KING -> "KING";
            default -> "";
        };
        return name + " OF " + type.toUpperCase();
    }
}
