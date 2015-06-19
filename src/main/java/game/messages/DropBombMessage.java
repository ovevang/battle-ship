package game.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.Coordinate;

/**
 * Created by lars on 01.02.15.
 */
public class DropBombMessage implements GameMessage {

    public final Coordinate coordinate;
    public final int player;

    @JsonCreator
    public DropBombMessage(
            @JsonProperty("coordinate") Coordinate coordinate,
            @JsonProperty("player") int player) {
        this.coordinate = coordinate;
        this.player = player;
    }
}
