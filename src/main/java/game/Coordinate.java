package game;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 30.01.15.
 */
public class Coordinate {

    public final int x;
    public final int y;

    @JsonCreator
    public Coordinate(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Coordinate))
            return false;

        Coordinate c = (Coordinate) obj;
        return c.x == this.x && c.y == this.y;
    }

    @Override
    public int hashCode() {
        return ("" + x + ":" + y).hashCode();
    }
}
