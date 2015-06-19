package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Coordinate;
import game.Ship;
import game.messages.SetShipMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by lars on 16.06.15.
 */
public class VeryDumbRobot extends AbstractRobot {


    public VeryDumbRobot(String gameId, int player) throws Exception {
        super(gameId, player);
    }

    @Override
    public void placeShips() {
        SetShipMessage ship1 = new SetShipMessage(new Ship(2, new Coordinate[] {new Coordinate(2,2), new Coordinate(2,3)}), player);
        SetShipMessage ship2 = new SetShipMessage(new Ship(3, new Coordinate[] {new Coordinate(4,4), new Coordinate(4,5), new Coordinate(4,6)}), player);
        SetShipMessage ship3 = new SetShipMessage(new Ship(3, new Coordinate[] {new Coordinate(8,1), new Coordinate(9,1), new Coordinate(10, 1)}), player);
        SetShipMessage ship4 = new SetShipMessage(new Ship(4, new Coordinate[] {new Coordinate(1,1), new Coordinate(2,1), new Coordinate(3,1), new Coordinate(4,1)}), player);
        SetShipMessage ship5 = new SetShipMessage(new Ship(4, new Coordinate[] {new Coordinate(8,5), new Coordinate(9,5), new Coordinate(10,5), new Coordinate(11,5)}), player);
        SetShipMessage ship6 = new SetShipMessage(new Ship(5, new Coordinate[] {new Coordinate(11,7), new Coordinate(11,8),new Coordinate(11,9), new Coordinate(11,10), new Coordinate(11, 11)}), player);

        ObjectMapper om = new ObjectMapper();

        try {
            wsClient.send(om.writeValueAsString(ship1));
            wsClient.send(om.writeValueAsString(ship2));
            wsClient.send(om.writeValueAsString(ship3));
            wsClient.send(om.writeValueAsString(ship4));
            wsClient.send(om.writeValueAsString(ship5));
            wsClient.send(om.writeValueAsString(ship6   ));
        } catch (Exception e) {
            throw new RuntimeException("marshalling failure", e);
        }

    }
}
