package no.sonat.battleships;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.relativt.battlecodeships.bot.Bot;
import net.relativt.battlecodeships.bot.GameResult;
import net.relativt.battlecodeships.bot.model.Coordinate;
import net.relativt.battlecodeships.bot.model.Ship;
import net.relativt.battlecodeships.bot.model.ShotResult;
import no.sonat.battleships.models.DirectionStrategy;
import no.sonat.battleships.models.ExhaustedDirectionsException;
import no.sonat.battleships.models.FiringState;
import org.java_websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lars on 16.06.15.
 */
public class VerySmartRobot extends Bot {

    final public static ArrayList<Coordinate> hitCoordinates = new ArrayList<>();
    final ObjectMapper json = new ObjectMapper();
    public static List<Coordinate> availableCoordinates = new ArrayList<>();
    private static int CURRENT_PARITY;
    WebSocketClient wsClient;
    private Coordinate lastFired;
    private FiringState state = FiringState.SEEKING;
    private DirectionStrategy ds;
    private String lastFiredResult;
    private Logger logger = LoggerFactory.getLogger(VerySmartRobot.class);


    private final Random random = new Random();


    private void handleResult(ShotResult result) {
        if( result == ShotResult.MISS){
            logger.info(lastFired + " Miss");
            lastFiredResult = "MISS";
        } else if(result == ShotResult.HIT){
            logger.info("HIT: " + lastFired);
            state = FiringState.SINKING;
            hitCoordinates.add(lastFired);
            lastFiredResult = "HIT";
        } else {
            logger.info("SUNK: " + lastFired);
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

            if (availableCoordinates.size()<90 && CURRENT_PARITY != 2){ // TODO: magic number? When do we "know" that
                                                                        // remaining boat is not to be found when shooting 3 apart?
                logger.info("Swapping parity to 2");
                CURRENT_PARITY = 2;
            }

            if (CURRENT_PARITY == 2) {
                do {
                    idx = random.nextInt(availableCoordinates.size());
                    coord = availableCoordinates.get(idx);
                    System.out.print(".");
                } while (!(coord.y % 2 == coord.x % 2)); // every other square in a lattice
            }else{
                do {
                    idx = random.nextInt(availableCoordinates.size());
                    coord = availableCoordinates.get(idx);
                    System.out.print(".");
                } while (!((coord.x + (coord.y%3)) % 3 == 0)); // every third square, offset by one per vertical row
            }

            availableCoordinates.remove(idx);
            logger.info("Shooting at {}. Remaining coordinates {}", coord, availableCoordinates);
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
        List<Coordinate> availablePlacementCoordinate = new ArrayList<>();
        availablePlacementCoordinate.addAll(availableCoordinates);
        Coordinate coordinate = null;
        Ship.Orientation o;
        for (Integer shipSize : ships) {
            boolean placementValid = false;
            List<Coordinate> currentShip = new ArrayList<>();
            do {
                o = random.nextInt(2) >= 1 ? Ship.Orientation.VERTICAL : Ship.Orientation.HORIZONTAL;

                outer: switch (o) {
                    case VERTICAL:
                        coordinate = new Coordinate(random.nextInt(12), Math.max(random.nextInt(12) - shipSize, 0));
                        for (int yy = coordinate.y; yy < coordinate.y + shipSize; yy++) {
                            if (!availablePlacementCoordinate.contains(new Coordinate(coordinate.x, yy))) {
                                placementValid = false;
                                break outer;
                            }else{
                                currentShip.add(new Coordinate(coordinate.x, yy));
                            }
                        }
                        placementValid = true;
                        logger.debug("Valid vertical placement. Removing coordinates: {}", currentShip);
                        availablePlacementCoordinate.removeAll(currentShip);
                        break;
                    case HORIZONTAL:
                        coordinate = new Coordinate(Math.max(random.nextInt(12) - shipSize, 0), random.nextInt(12));
                        for (int xx = coordinate.x; xx < coordinate.x + shipSize; xx++) {
                            if (!availablePlacementCoordinate.contains(new Coordinate(xx, coordinate.y))) {
                                placementValid = false;
                                break outer;
                            } else{
                                currentShip.add(new Coordinate(xx, coordinate.y));
                            }
                        }
                        placementValid = true;
                        logger.info("Valid horizontal placement. Removing coordinates: {}", currentShip);
                        availablePlacementCoordinate.removeAll(currentShip);
                        break;
                }
            }while(placementValid==false);



            try{
                placeShip(coordinate, o, shipSize);
                logger.info("Placed a ship of size {} in position {}", shipSize, coordinate);
            }catch(Exception e){
                logger.error("Some error occurred, gitt", e);
            }
        }
    }

    @Override
    public void initializeBoard(int width, int height) {
        CURRENT_PARITY = 3;
        availableCoordinates = new ArrayList<>();
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
        logger.info(gameResult.toString());

    }
}
