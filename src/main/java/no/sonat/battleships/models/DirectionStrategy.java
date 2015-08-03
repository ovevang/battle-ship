package no.sonat.battleships.models;

import net.relativt.battlecodeships.bot.model.Coordinate;
import no.sonat.battleships.VerySmartRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectionStrategy {

    private final Logger logger = LoggerFactory.getLogger(DirectionStrategy.class);
    private Coordinate initialHit;
    private Coordinate currentShot;
    private Direction currentDirection;
    private enum Direction { NORTH, SOUTH, EAST, WEST };

    public DirectionStrategy(Coordinate initialHit) {
        logger.info("Creating new DS with initialHit " + initialHit);
        this.initialHit = initialHit;
        currentShot = initialHit;
        currentDirection = Direction.NORTH;
    }

    public Coordinate nextHit() throws ExhaustedDirectionsException {

        Coordinate proposedCoordinate = null;
        proposedCoordinate = getNextCoordinate();
        if (!isInsideBoard(proposedCoordinate)){
            if (tryNextDirection() == true) {
                currentShot = initialHit;
                logger.debug("changed direction to " + currentDirection);
                return nextHit();
            }else{
                throw new ExhaustedDirectionsException();
            }
        }else{
            if (VerySmartRobot.availableCoordinates.contains(proposedCoordinate)){
                currentShot = proposedCoordinate;
                return proposedCoordinate;
            }else if (VerySmartRobot.hitCoordinates.contains(proposedCoordinate)){
                logger.debug(proposedCoordinate + " already hit, trying further ahead");
                currentShot = proposedCoordinate;
                return nextHit();
            }else{
                if (tryNextDirection() == true){
                    logger.debug("changed direction to " + currentDirection);
                    currentShot = initialHit;
                    return nextHit();
                }else{
                    throw new ExhaustedDirectionsException();
                }
            }
        }

    }

    private boolean tryNextDirection() {
        switch (currentDirection){
            case NORTH: currentDirection=Direction.SOUTH;
                return true;
            case SOUTH: currentDirection=Direction.EAST;
                return true;
            case EAST: currentDirection=Direction.WEST;
                return true;
            case WEST: return false;
        }
        return false;
    }

    private boolean isInsideBoard(Coordinate proposedCoordinate) {
        return proposedCoordinate.x>=0 && proposedCoordinate.x <12 && proposedCoordinate.y>=0 && proposedCoordinate.y<12;
    }

    public Coordinate nextMiss() throws ExhaustedDirectionsException {
        System.out.println(currentShot + " called nextMiss");
        currentShot = initialHit;
        Coordinate proposedCoordinate = null;
        if (tryNextDirection() == true) {
            proposedCoordinate = getNextCoordinate();
        }else{
            throw new ExhaustedDirectionsException();
        }
        if (!isInsideBoard(proposedCoordinate)){
                currentShot = initialHit;
                return nextMiss();
        }else{
            if (VerySmartRobot.availableCoordinates.contains(proposedCoordinate)){
                currentShot = proposedCoordinate;
                return proposedCoordinate;
            }else if (VerySmartRobot.hitCoordinates.contains(proposedCoordinate)){
                currentShot = proposedCoordinate;
                return nextHit();
            }else{
                currentShot = initialHit;
                return nextMiss();
                }
            }
        }


    private Coordinate getNextCoordinate() {
        Coordinate proposedCoordinate = null;
        if (currentDirection == Direction.NORTH) {
            proposedCoordinate = new Coordinate(currentShot.x, currentShot.y - 1);
        } else if (currentDirection == Direction.SOUTH) {
            proposedCoordinate = new Coordinate(currentShot.x, currentShot.y + 1);

        } else if (currentDirection == Direction.EAST) {
            proposedCoordinate = new Coordinate(currentShot.x + 1, currentShot.y);
        } else if (currentDirection == Direction.WEST) {
            proposedCoordinate = new Coordinate(currentShot.x - 1, currentShot.y);
        }
        return proposedCoordinate;
    }


}
