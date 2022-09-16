package com.pisti.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pisti.server.model.GameScore;
import org.springframework.stereotype.Repository;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, Long> {
}
