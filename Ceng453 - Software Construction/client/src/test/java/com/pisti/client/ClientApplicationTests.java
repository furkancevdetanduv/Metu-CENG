package com.pisti.client;

import com.pisti.client.constants.GameConstants;
import com.pisti.client.model.*;
import javafx.scene.image.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ClientApplicationTests {

	private Table table;
	private Player player;

	private ArrayList<Card> organizedDeck;
	private ArrayList<Card> deck;

	@BeforeEach
	public void gameSetUp() {
		organizedDeck = new ArrayList<>();
		deck = new ArrayList<>();
		for(int typeNo = 0; typeNo < GameConstants.NO_OF_CARD_TYPES; typeNo++) {
			String type = switch (typeNo) {
				case GameConstants.CLOVER -> "clover";
				case GameConstants.DIAMOND -> "diamond";
				case GameConstants.HEART -> "heart";
				case GameConstants.SPADE -> "spade";
				default -> "";
			};
			for(int cardNo = 1; cardNo < GameConstants.NO_OF_CARDS + 1; cardNo++) {
				Card card = new Card(cardNo, type, null);
				organizedDeck.add(card);
			}
		}
		deck.addAll(organizedDeck);
		player = new Player();
		table = new Table(deck, 1);
	}

	@Test
	void checkCalculatedPointsForFourAces() {
		ArrayList<Card> cards = new ArrayList<>();
		cards.add(organizedDeck.get(0));
		cards.add(organizedDeck.get(13));
		cards.add(organizedDeck.get(26));
		cards.add(organizedDeck.get(39));
		player.winCards(cards);
		player.calculateScore();
		assertEquals(4, player.getScore());
	}

	@Test
	void checkCalculatedPointsForPistiOfTwosOneCloverOneDiamond() {
		ArrayList<Card> cards = new ArrayList<>();
		cards.add(organizedDeck.get(1));
		cards.add(organizedDeck.get(14));
		player.winCards(cards);
		player.calculateScore();
		assertEquals(12, player.getScore());
	}

	@Test
	void checkCalculatedPointsForDoublePisti() {
		ArrayList<Card> cards = new ArrayList<>();
		cards.add(organizedDeck.get(10));
		cards.add(organizedDeck.get(23));
		player.winCards(cards);
		player.calculateScore();
		assertEquals(22, player.getScore());
	}

	@Test
	void checkCalculatedPointsForTenOfDiamonds() {
		ArrayList<Card> cards = new ArrayList<>();
		cards.add(organizedDeck.get(22));
		player.winCards(cards);
		player.calculateScore();
		assertEquals(3, player.getScore());
	}

}
