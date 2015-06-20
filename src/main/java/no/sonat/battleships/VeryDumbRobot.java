package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sonat.battleships.models.Coordinate;
import no.sonat.battleships.models.Ship;
import no.sonat.battleships.models.DropBombMessage;
import no.sonat.battleships.models.SetShipMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by lars on 16.06.15.
 */
public class VeryDumbRobot {

    final ObjectMapper json = new ObjectMapper();

    final String gameId;
    final int player;

    WebSocketClient wsClient;

    public VeryDumbRobot(String gameId, int player) throws Exception {
        this.gameId = gameId;
        this.player = player;
    }

    public void initiate() throws URISyntaxException {

        this.wsClient = new WebSocketClient(new URI("ws://localhost:9000/game/" + gameId + "/socket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("onOpen");

                placeShips();
            }

            @Override
            public void onMessage(String s) {

                System.out.println(String.format("message received: %1$s", s));

                JsonNode msg;
                try {
                    msg = json.readTree(s);
                } catch (IOException e) {
                    throw new RuntimeException("error reading json", e);
                }
                String type = msg.get("class").asText();

                switch (type) {
                    case "game.broadcast.GameIsStarted":
                        onGameStart(msg);
                        break;
                    case "game.broadcast.NextPlayerTurn":
                        if (msg.get("playerTurn").asInt() == player) {
                            shoot();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                System.out.println("onclose");
            }

            @Override
            public void onError(Exception e) {
                System.out.println("onError");
                System.out.println(e.getMessage());
            }
        };

        wsClient.connect();
    }

    public void placeShips() {
        SetShipMessage ship1 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(2,2), new Coordinate(2,3)}), player);
        SetShipMessage ship2 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(4,4), new Coordinate(4,5), new Coordinate(4,6)}), player);
        SetShipMessage ship3 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(8,1), new Coordinate(9,1), new Coordinate(10, 1)}), player);
        SetShipMessage ship4 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(1,1), new Coordinate(2,1), new Coordinate(3,1), new Coordinate(4,1)}), player);
        SetShipMessage ship5 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(8,5), new Coordinate(9,5), new Coordinate(10,5), new Coordinate(11,5)}), player);
        SetShipMessage ship6 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(11,7), new Coordinate(11,8),new Coordinate(11,9), new Coordinate(11,10), new Coordinate(11, 11)}), player);

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

    public void onGameStart(JsonNode msg) {
        for (int x = 0; x < 12; x++) {
            for (int y = 0; y < 12; y++) {
                availableCoordinates.add(new Coordinate(x, y));
            }
        }

        // if it's my turn, fire!
        if (msg.get("playerTurn").asInt() == player) {
            shoot();
        }
    }

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
