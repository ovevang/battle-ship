package no.sonat.battleships.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.relativt.battlecodeships.bot.model.Coordinate;

/**
 * Created by lars on 20.06.15.
 */
public class ShootMessage {

    public final Coordinate coordinate;
    public final int player;
    public final @JsonProperty("class") String metaType = "game.messages.ShootMessage";

    public ShootMessage(Coordinate coordinate, int player) {
        this.coordinate = coordinate;
        this.player = player;
    }
}
