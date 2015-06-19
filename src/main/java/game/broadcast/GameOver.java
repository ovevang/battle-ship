package game.broadcast;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.messages.GameMessage;

/**
 * Created by lars on 16.02.15.
 */
public class GameOver implements GameMessage {

    public final String andTheWinnerIs;

    @JsonCreator
    public GameOver(
            @JsonProperty("andTheWinnerIs") String andTheWinnerIs) {
        this.andTheWinnerIs = andTheWinnerIs;
    }
}
