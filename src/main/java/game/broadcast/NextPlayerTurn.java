package game.broadcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.messages.GameMessage;

/**
 * Created by lars on 16.02.15.
 */
public class NextPlayerTurn implements GameMessage {

    public final int playerTurn;

    @JsonCreator
    public NextPlayerTurn(
            @JsonProperty("playerTurn") int playerTurn) {
        this.playerTurn = playerTurn;
    }
}
