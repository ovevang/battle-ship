package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.Coordinate;
import game.Ship;
import game.broadcast.GameIsStarted;
import game.messages.DropBombMessage;
import game.messages.SetShipMessage;

import java.util.*;

/**
 * Created by lars on 16.06.15.
 */
public class VeryDumbRobot extends AbstractRobot {

    final ObjectMapper json = new ObjectMapper();

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

        try {
            wsClient.send(json.writeValueAsString(ship1));
            wsClient.send(json.writeValueAsString(ship2));
            wsClient.send(json.writeValueAsString(ship3));
            wsClient.send(json.writeValueAsString(ship4));
            wsClient.send(json.writeValueAsString(ship5));
            wsClient.send(json.writeValueAsString(ship6));
        } catch (Exception e) {
            throw new RuntimeException("marshalling failure", e);
        }

    }

    final List<Coordinate> availableCoordinates = new ArrayList<>();
    final Random rand = new Random();

    @Override
    public void onGameStart(GameIsStarted msg) {
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                availableCoordinates.add(new Coordinate(x, y));
            }
        }

        // if it's my turn, fire!
        if (msg.playerTurn == player) {
            shoot();
        }
    }

    @Override
    public void shoot() {
        int idx = rand.nextInt(availableCoordinates.size());
        Coordinate coord = availableCoordinates.get(idx);
        availableCoordinates.remove(idx);
        DropBombMessage dropBombMessage = new DropBombMessage(coord, player);

        try {
            wsClient.send(json.writeValueAsString(dropBombMessage));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error marshalling object", e);
        }
    }
}
