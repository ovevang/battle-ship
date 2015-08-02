package no.sonat.battleships;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.relativt.battlecodeships.bot.Bot;
import net.relativt.battlecodeships.bot.GameResult;
import net.relativt.battlecodeships.bot.model.*;
import no.sonat.battleships.models.DirectionStrategy;
import no.sonat.battleships.models.ExhaustedDirectionsException;
import no.sonat.battleships.models.FiringState;
import no.sonat.battleships.models.ShootMessage;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lars on 16.06.15.
 */
public class VerySmartRobot extends Bot {

    final public static ArrayList<Coordinate> hitCoordinates = new ArrayList<>();
    final ObjectMapper json = new ObjectMapper();
    final public static List<Coordinate> availableCoordinates = new ArrayList<>();

    WebSocketClient wsClient;
    private Coordinate lastFired;
    private FiringState state = FiringState.SEEKING;
    private DirectionStrategy ds;
    private String lastFiredResult;
    private Logger logger = LoggerFactory.getLogger(VerySmartRobot.class);


    private final Random random = new Random();


    private void handleResult(ShotResult result) {
        if( result == ShotResult.MISS){
            System.out.println(lastFired + " Miss");
            lastFiredResult = "MISS";
        } else if(result == ShotResult.HIT){
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






    public void shoot() {

        if (state == FiringState.SEEKING){
            int idx = 0;
            Coordinate coord = null;

            do {
                idx = random.nextInt(availableCoordinates.size());
                coord = availableCoordinates.get(idx);
                System.out.print(".");
            }while(!(coord.y % 2 == 0 && coord.x % 2 ==0 || coord.y % 2 != 0 && coord.x % 2 != 0));

            availableCoordinates.remove(idx);
            shoot(coord);
            lastFired = coord;


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
                coord = availableCoordinates.get(random.nextInt(availableCoordinates.size())); // TODO: fix
                System.out.printf("Exhausted all possibilities ;/");
                state = FiringState.SEEKING;
                ds = null;
            }
            availableCoordinates.remove(coord);
            shoot(coord);
            lastFired = coord;

        }


    }

    @Override
    public void placeShips(List<Integer> ships) {
        int x = 0;

        for (Integer shipSize : ships) {
            final Coordinate coordinate = new Coordinate(x++, random.nextInt(6));
            placeShip(coordinate, Ship.Orientation.VERTICAL, shipSize);
            logger.info("Placing a ship of size {} in position {}", shipSize, coordinate);
        }
    }

    @Override
    public void initializeBoard(int width, int height) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                availableCoordinates.add(new Coordinate(x, y));
            }
        }

    }

    @Override
    public void onShot(net.relativt.battlecodeships.bot.model.Coordinate coordinate, ShotResult shotResult) {
          handleResult(shotResult);
    }

    @Override
    public void onShotAvailable() {
        shoot();
    }

    @Override
    public void onGameCompletion(GameResult gameResult) {
        System.out.println(gameResult);

    }
}
