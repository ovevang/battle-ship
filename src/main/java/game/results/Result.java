package game.results;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * Created by lars on 03.02.15.
 */
@JsonTypeInfo(use = Id.CLASS, include = As.PROPERTY, property = "class")
public interface Result {

    boolean isOk();
}
