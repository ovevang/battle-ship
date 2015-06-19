package game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lars on 30.01.15.
 */
public class Ship {

    public final Coordinate[] coordinates;

    public Ship(int length, Coordinate[] coordinates) {
        this.coordinates = coordinates;
    }

    @JsonCreator
    public Ship(@JsonProperty("coordinates") Coordinate[] coordinates) {
        this.coordinates = coordinates;
    }
}
