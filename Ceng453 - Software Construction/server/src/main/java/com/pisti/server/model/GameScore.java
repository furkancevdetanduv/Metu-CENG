package com.pisti.server.model;

import lombok.*;
import java.util.Date;
import javax.persistence.*;
import javax.persistence.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class GameScore {

    /**
     * Primary key of the model game score.
     */
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long gameId;

    /**
     * Score of the given game score depending on the game played.
     */
    @Column
    private int score;

    /**
     * Date of the given game score given as "yyyy-MM-ddTHH:mm:ss".
     */
    @Column
    private Date date;

    /**
     * User object of the player who owns the game score.
     */
    @Column
    private long userId;

    @Override
    public String toString() {
        return String.format("Game ID is %d, score is %d, date is %s, User ID is %d", gameId, score, date, userId);
    }
}