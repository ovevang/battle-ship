package game.results;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by lars on 07.02.15.
 */
public class RuleBreakResult extends DefResult {

    private String message;

    public RuleBreakResult(
            @JsonProperty("ok") boolean ok,
            @JsonProperty("message") String message) {
        super(ok);

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
