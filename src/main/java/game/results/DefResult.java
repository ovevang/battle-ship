package game.results;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 03.02.15.
 */
public class DefResult implements Result {

    private final boolean ok;

    @JsonCreator
    public DefResult(@JsonProperty("ok") boolean ok) {
        this.ok = ok;
    }

    @Override
    public boolean isOk() {
        return ok;
    }
}
