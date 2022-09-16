package com.pisti.client.controller;

import com.pisti.client.constants.GameConstants;
import com.pisti.client.model.Card;
import com.pisti.client.model.Table;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;



public abstract class GameController implements Initializable {
    public ArrayList<ArrayList<Card>> cards = new ArrayList<>();
    public ArrayList<Card> deck = new ArrayList<>();
    Image faceDownCard = new Image("static/faceDown.png");

    @FXML
    public VBox mainBox;

    @FXML
    public HBox playerDeck;

    @FXML
    public HBox enemyDeck;

    @FXML
    public HBox table;

    Table gameTable;
    int ai;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * Initializes deck cards to be used to Card class, creates a
     * deck and shuffles it as long as there is no Jack at the bottom.
     */
    public void cardInitialize() {
        for(int typeNo = 0; typeNo < GameConstants.NO_OF_CARD_TYPES; typeNo++) {
            ArrayList<Card> newList = new ArrayList<>();
            String type = switch (typeNo) {
                case GameConstants.CLOVER -> "clover";
                case GameConstants.DIAMOND -> "diamond";
                case GameConstants.HEART -> "heart";
                case GameConstants.SPADE -> "spade";
                default -> "";
            };
            for(int cardNo = 1; cardNo < GameConstants.NO_OF_CARDS + 1; cardNo++) {
                Image image = new Image("static/" + type + "/card_" + cardNo + "_" + type + ".png");
                Card card = new Card(cardNo, type, image);
                newList.add(card);
            }
            cards.add(newList);
        }
        for (ArrayList<Card> cardList : cards) {
            deck.addAll(cardList);
        }
        Collections.shuffle(deck);
        while(deck.get(deck.size() - 1).getId() == GameConstants.JACK) {
            Collections.shuffle(deck);
        }
    }

    /**
     * Initializes table, player and ai; prepares player, table and ai cards;
     * implements cheat ctrl + 9
     * @param aiLevel AI level to be initialized
     */
    public void tableInitialize(int aiLevel) {
        gameTable = new Table(deck, aiLevel);
        gameTable.initializeTable();
        organizePlayerCards(4);
        organizeEnemyCards(4);
        mainBox.setOnKeyPressed(e -> {
            if(e.isControlDown() && e.getCode() == KeyCode.DIGIT9 && aiLevel < GameConstants.LEVEL3) {
                Stage stage = (Stage) ((Node)(e.getSource())).getScene().getWindow();
                try {
                    FXMLLoader fxmlLoader = switch (aiLevel) {
                        case GameConstants.LEVEL1 -> new FXMLLoader(getClass().getResource("/gameLevel2.fxml"));
                        case GameConstants.LEVEL2 -> new FXMLLoader(getClass().getResource("/gameLevel3.fxml"));
                        default -> new FXMLLoader();
                    };
                    Parent parent = fxmlLoader.load();
                    stage.setScene(new Scene(parent, 800, 600));
                    stage.setMinHeight(600);
                    stage.setMinWidth(800);
                    stage.show();
                } catch (IOException exception) {
                    throw new RuntimeException();
                }
            }
        });
        ai = aiLevel;
    }

    /**
     * Organizes player's hand graphics and buttons then organizes the table
     * @param noOfCards Number of cards at player's hand
     */
    public void organizePlayerCards(int noOfCards) {
        Button[] button = new Button[GameConstants.CARDS_TO_DRAW];
        ImageView[] image = new ImageView[noOfCards];
        playerDeck.getChildren().clear();
        for(int i = 0; i < GameConstants.CARDS_TO_DRAW; i++) {
            button[i] = new Button();
        }
        for(int i = 0; i < noOfCards; i++) {
            image[i] = new ImageView(gameTable.getPlayer().getHand().get(i).getImage());
            button[i].setGraphic(image[i]);
            playerDeck.getChildren().add(button[i]);
        }
        button[0].setOnAction(e -> buttonHandler(0, ai));
        button[1].setOnAction(e -> buttonHandler(1, ai));
        button[2].setOnAction(e -> buttonHandler(2, ai));
        button[3].setOnAction(e -> buttonHandler(3, ai));
        organizeTable();
    }

    /**
     * Organizes enemy's hand graphics and then organizes the table
     * @param noOfCards Number of cards at enemy's hand
     */
    public void organizeEnemyCards(int noOfCards) {
        ImageView[] image = new ImageView[noOfCards];
        enemyDeck.getChildren().clear();
        for(int i = 0; i < noOfCards; i++) {
            image[i] = new ImageView(faceDownCard);
            enemyDeck.getChildren().add(image[i]);
        }
        organizeTable();
    }

    /**
     * Organizes the table such that it shows the top card
     */
    public void organizeTable() {
        ImageView topCard;
        table.getChildren().clear();
        if(gameTable.getTableCards().size() == 0) {
            return;
        }
        topCard = new ImageView(gameTable.getTableCards().
                get(gameTable.getTableCards().size() - 1).getImage());
        table.getChildren().add(topCard);
    }

    /**
     * Event handler for buttons to choose a card. Plays the players card,
     * handles table accordingly, gets AI's decision and plays it, then
     * handles table again and then checks for the game ending conditions
     * @param cardIndex Which button player pressed
     * @param aiLevel Level of AI
     */
    private void buttonHandler(int cardIndex, int aiLevel) {
        Card playerCard;
        Card enemyCard;
        int chosenCard;
        playerCard = gameTable.getPlayer().playCard(cardIndex);
        gameTable.playCard("Player", playerCard);
        chosenCard = gameTable.getAI().chooseCardToPlay();
        enemyCard = gameTable.getAI().playCard(chosenCard);
        gameTable.playCard("Enemy", enemyCard);
        organizeEnemyCards(gameTable.getAI().getHand().size());
        organizePlayerCards(gameTable.getPlayer().getHand().size());
        if(gameTable.isOver() && aiLevel < GameConstants.LEVEL3) {
            Stage stage = (Stage) mainBox.getScene().getWindow();
            try {
                Parent parent;
                FXMLLoader fxmlLoader = switch (aiLevel) {
                    case GameConstants.LEVEL1 -> new FXMLLoader(getClass().getResource("/gameLevel2.fxml"));
                    case GameConstants.LEVEL2 -> new FXMLLoader(getClass().getResource("/gameLevel3.fxml"));
                    default -> new FXMLLoader();
                };
                parent = fxmlLoader.load();
                stage.setScene(new Scene(parent, 800, 600));
                stage.setMinHeight(600);
                stage.setMinWidth(800);
                stage.show();
            } catch (IOException exception) {
                throw new RuntimeException();
            }
        }
    }

}
