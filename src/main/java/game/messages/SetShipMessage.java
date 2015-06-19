package game.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.Ship;

/**
 * Created by lars on 02.02.15.
 */
public class SetShipMessage implements GameMessage {

    public final Ship ship;
    public final int player;

    @JsonCreator
    public SetShipMessage(
            @JsonProperty("ship") Ship ship,
            @JsonProperty("player") int player) {
        this.ship = ship;
        this.player = player;
    }
}
