package game.broadcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 16.02.15.
 */
public class GameIsStarted extends NextPlayerTurn {

    @JsonCreator
    public GameIsStarted(@JsonProperty("playerTurn") int playerTurn) {
        super(playerTurn);
    }
}
