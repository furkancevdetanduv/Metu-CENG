package com.pisti.client.controller;

import com.pisti.client.constants.GameConstants;

import java.net.URL;
import java.util.ResourceBundle;

public class Level3Controller extends GameController {

    /**
     * Initializes level three
     * @param url URL to initialize
     * @param resourceBundle Resources to use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cardInitialize();
        tableInitialize(GameConstants.LEVEL3);
    }
}
