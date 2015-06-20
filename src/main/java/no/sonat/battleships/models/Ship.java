package no.sonat.battleships.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import no.sonat.battleships.models.Coordinate;

/**
 * Created by lars on 30.01.15.
 */
public class Ship {

    public final Coordinate[] coordinates;

    public Ship(Coordinate[] coordinates) {
        this.coordinates = coordinates;
    }
}
