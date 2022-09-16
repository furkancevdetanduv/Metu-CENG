package com.pisti.server.services;

import com.pisti.server.model.GameScore;
import com.pisti.server.repository.GameScoreRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameScoreService {

    /**
     * Game score repository object that is autowired to use in service.
     */
    private final GameScoreRepository gameScoreRepository;


    /**
     * Game score service constructor with game score repository.
     * <p>
     * @param gameScoreRepository Game score repository to be used.
     */
    @Autowired
    public GameScoreService(GameScoreRepository gameScoreRepository) {
        this.gameScoreRepository = gameScoreRepository;
    }

    /**
     * Entity manager to be used in findGameScore method.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Adds game score to the repository.
     * <p>
     * @param gameScore Game score object to be added.
     * @return  Returns the response of the repository.
     */
    public GameScore addGameScore(GameScore gameScore) {
        return gameScoreRepository.save(gameScore);
    }

    /**
     * Gets all the game scores from the repository.
     * @return Returns the response of the repository.
     */
    public List<GameScore> getAllGameScores() {
        return gameScoreRepository.findAll();
    }

    /**
     * Deletes game scores with given game ids.
     * <p>
     * @param GameIdList List of the game scores to be deleted.
     */
    public void deleteGameScore(List<Long> GameIdList) {
         Iterable<GameScore> gameScores = this.gameScoreRepository.findAllById(GameIdList);
         for(GameScore gameScore : gameScores) this.gameScoreRepository.delete(gameScore);
    }

    /**
     * Finds the game score with the given object attributes.
     * <p>
     * @param jsonGameScore Attributes to be found.
     * @return  Returns the list of the results.
     */
    public List<GameScore> findGameScore(JSONObject jsonGameScore) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GameScore> criteriaQuery = criteriaBuilder.createQuery(GameScore.class);
        Root<GameScore> gameScoreRoot = criteriaQuery.from(GameScore.class);
        List<Predicate> predicates = new ArrayList<>();

        if (jsonGameScore.has("gameId")) {
            predicates.add(criteriaBuilder.equal(gameScoreRoot.get("gameId"), jsonGameScore.get("gameId")));
        }
        if (jsonGameScore.has("score")) {
            predicates.add(criteriaBuilder.equal(gameScoreRoot.get("score"), jsonGameScore.get("score")));
        }
        if (jsonGameScore.has("date")) {
            predicates.add(criteriaBuilder.equal(gameScoreRoot.get("date"), jsonGameScore.get("date")));
        }
        if (jsonGameScore.has("userId")) {
            predicates.add(criteriaBuilder.equal(gameScoreRoot.get("userId"), jsonGameScore.get("userId")));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
