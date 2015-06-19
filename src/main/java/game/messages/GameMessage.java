package game.messages;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by lars on 15.02.15.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface GameMessage {
}
