package game.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 17.02.15.
 */
public class PlaceShipResult implements Result {

    private final boolean ok;
    public final int shipsLeftToPlace;

    @JsonCreator
    public PlaceShipResult(
            @JsonProperty("ok") boolean ok,
            @JsonProperty("shipsLeftToPlace") int shipsLeftToPlace) {
        this.ok = ok;
        this.shipsLeftToPlace = shipsLeftToPlace;
    }

    @Override
    public boolean isOk() {
        return this.ok;
    }
}
