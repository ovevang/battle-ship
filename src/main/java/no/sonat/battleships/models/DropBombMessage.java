package no.sonat.battleships.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 20.06.15.
 */
public class DropBombMessage {

    public final Coordinate coordinate;
    public final int player;
    public final @JsonProperty("class") String metaType = "game.messages.DropBombMessage";

    public DropBombMessage(Coordinate coordinate, int player) {
        this.coordinate = coordinate;
        this.player = player;
    }
}
