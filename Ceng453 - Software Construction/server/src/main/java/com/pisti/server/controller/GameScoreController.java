package com.pisti.server.controller;

import com.pisti.server.model.GameScore;
import com.pisti.server.services.GameScoreService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("api/gameScores")
@RestController
public class GameScoreController {

    /**
     * Game score service object that is autowired to use in controller.
     */
    private final GameScoreService gameScoreService;

    /**
     * Game score controller constructor from game score service.
     * <p>
     * @param gameScoreService Game score service to be used by the constructor.
     */
    @Autowired
    public GameScoreController(GameScoreService gameScoreService) {
        this.gameScoreService = gameScoreService;
    }

    /**
     * Adds a game score to the database.
     * <p>
     * @param gameScore Game score object to be added.
     * @return Returns the added game score as response
     */
    @PostMapping("/add")
    public ResponseEntity<GameScore> addGameScore(@RequestBody GameScore gameScore) {
        return ResponseEntity.ok().body(this.gameScoreService.addGameScore(gameScore));
    }

    /**
     * Finds the given game score and displays it.
     * <p>
     * @param jsonGameScore Game score object to be found.
     * @return Returns the found game score as response.
     */
    @PostMapping("/find")
    public ResponseEntity<List<GameScore>> findGameScore(@RequestBody String jsonGameScore) {
        return ResponseEntity.ok().body(this.gameScoreService.findGameScore(new JSONObject(jsonGameScore)));
    }

    /**
     * Gets all the game scores.
     * @return Returns all the game scores as a list.
     */
    @GetMapping("/getAll")
    public ResponseEntity<List<GameScore>> getAllGameScores() {
        return ResponseEntity.ok().body(this.gameScoreService.getAllGameScores());
    }

    /**
     * Deletes all the game scores with given game ids.
     * <p>
     * @param gameIds List of game ids to be deleted.
     * @return Returns API response.
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteGameScore(@RequestBody String gameIds) {
        List<Long> gameIdList = new ArrayList<>();
        for(Object object : new JSONArray(gameIds)) gameIdList.add(Long.valueOf(String.valueOf(object)));
        this.gameScoreService.deleteGameScore(gameIdList);
        return ResponseEntity.noContent().build();
    }
}
