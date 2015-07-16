package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.sonat.battleships.models.*;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lars on 16.06.15.
 */
public class VerySmartRobot {

    final public static ArrayList<Coordinate> hitCoordinates = new ArrayList<>();
    final ObjectMapper json = new ObjectMapper();
    final public static List<Coordinate> availableCoordinates = new ArrayList<>();
    final String gameId;
    final int player;

    WebSocketClient wsClient;
    private Coordinate lastFired;
    private FiringState state = FiringState.SEEKING;
    private DirectionStrategy ds;
    private String lastFiredResult;

    public VerySmartRobot(String gameId, int player) throws Exception {
        this.gameId = gameId;
        this.player = player;
    }

    public void initiate() throws URISyntaxException {

        this.wsClient = new WebSocketClient(new URI("ws://10.0.0.4:9000/game/" + gameId + "/socket")) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                System.out.println("onOpen");

                //placeShipsDefault();
                placeShipsForReal();
            }

            @Override
            public void onMessage(String s) {

             //   System.out.println(String.format("message received: %1$s", s));

                final JsonNode msg;
                try {
                    msg = json.readTree(s);
                } catch (IOException e) {
                    throw new RuntimeException("error reading json", e);
                }
                final String type = msg.get("class").asText();

                switch (type) {
                    case "game.broadcast.GameIsStarted":
                        onGameStart(msg);
                        break;
                    case "game.broadcast.NextPlayerTurn":
                        if (msg.get("playerTurn").asInt() == player) {
                            shoot();
                        }
                        break;
                    case "game.result.ShootResult":
                        handleResult(msg);
                        break;
                    case "game.broadcast.GameOver":
                        boolean weWon = msg.get("andTheWinnerIs").asText().equals("Player " + player);
                        System.out.println(weWon ? "We Won!!!" : "Wo lost!!!");
                        System.exit(0);
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
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        };

        wsClient.connect();
    }

    private void handleResult(JsonNode msg) {
        String hitResult = msg.get("hit").asText();
        if( hitResult.equalsIgnoreCase("SPLASH")){
            System.out.println(lastFired + " Miss");
            lastFiredResult = "MISS";
        } else if(hitResult.equalsIgnoreCase("BANG")){
            System.out.println("HIT: " + lastFired);
            state = FiringState.SINKING;
            hitCoordinates.add(lastFired);
            lastFiredResult = "HIT";
        } else {
            System.out.println("SUNK: " + lastFired);
            state = FiringState.SEEKING;
            hitCoordinates.add(lastFired);
            lastFiredResult = "SUNK";
            ds = null;
        }
    }

    public void placeShipsDefault() {
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

    public void placeShipsForReal() {
        SetShipMessage ship1 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(5,5), new Coordinate(6,5)}), player);
        SetShipMessage ship2 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(7,4), new Coordinate(7,5), new Coordinate(7,6)}), player);
        SetShipMessage ship3 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(4,6), new Coordinate(4,7), new Coordinate(4, 8)}), player);
        SetShipMessage ship4 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(8,5), new Coordinate(9,5), new Coordinate(10,5), new Coordinate(11,5)}), player);
        SetShipMessage ship5 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(8,6), new Coordinate(8,7), new Coordinate(8,8), new Coordinate(8,9)}), player);


        SetShipMessage ship6 = new SetShipMessage(new Ship(new Coordinate[] {new Coordinate(0,2), new Coordinate(0,3),new Coordinate(0,4), new Coordinate(0,5), new Coordinate(0, 6)}), player);

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

        if (state == FiringState.SEEKING){
            int idx = 0;
            Coordinate coord = null;

            do {
                idx = rand.nextInt(availableCoordinates.size());
                coord = availableCoordinates.get(idx);
                System.out.print(".");
            }while(!(coord.y % 2 == 0 && coord.x % 2 ==0 || coord.y % 2 != 0 && coord.x % 2 != 0));

            availableCoordinates.remove(idx);
            ShootMessage shootMessage = new ShootMessage(coord, player);

            try {
                wsClient.send(json.writeValueAsString(shootMessage));
                lastFired = coord;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error marshalling object", e);
            }

        }else{
            if (ds == null){
                ds = new DirectionStrategy(lastFired);
            }
            Coordinate coord = null;
            try {
                if( lastFiredResult.equalsIgnoreCase("HIT")) {
                    coord = ds.nextHit();
                } else {
                    coord = ds.nextMiss();
                }
            } catch (ExhaustedDirectionsException e) {
                coord = availableCoordinates.get(rand.nextInt(availableCoordinates.size())); // TODO: fix
                System.out.printf("Exhausted all possibilities ;/");
                state = FiringState.SEEKING;
                ds = null;
            }
            availableCoordinates.remove(coord);
            ShootMessage shootMessage = new ShootMessage(coord, player);

            try {
                wsClient.send(json.writeValueAsString(shootMessage));
                lastFired = coord;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error marshalling object", e);
            }
        }


    }
}
