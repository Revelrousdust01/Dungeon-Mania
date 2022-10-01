package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovementSpider implements Movement {
    private List<Position> surroundPositions = new ArrayList<Position>();
    private boolean clockwise = true;

    @Override 
    public void move(MovingEntity movingEntity, Position playerPosition) {
        Boolean clearClockwise = false;
        Boolean clearAntiClockwise = false;
        if (clockwise) {
            // Go clockwise
            clearClockwise = checkClockwise(movingEntity);
            if (!clearClockwise) {
                // Boulder blocking clockwise, check anti-clockwise
                clearAntiClockwise = checkAntiClockwise(movingEntity);
            }
        } else {
            // Check anti-clockwise
            clearAntiClockwise = checkAntiClockwise(movingEntity);
            if (!clearAntiClockwise) {
                // Boulder blocking anti-clockwise, check clockwise
                clearClockwise = checkClockwise(movingEntity);
            }
        }
        if (!clearAntiClockwise && !clearClockwise) {
            // When spider is stuck between boulders
            return;
        }
    }

    // Checking if it can move clockwise
    private boolean checkClockwise(MovingEntity movingEntity) {
        Spider spider = (Spider) movingEntity;
        int tmpIndex = spider.getMovementIndex() + 1;
        if (tmpIndex == surroundPositions.size()) {
            tmpIndex = 0;
        }
        Position destination = surroundPositions.get(tmpIndex);
        Entity destinationEntity = Entity.findEntityAtPosition(destination);
        if (destinationEntity == null || destinationEntity.getType().compareTo("boulder") != 0) {
            // When it is not a boulder
            movingEntity.setPosition(destination);
            spider.setMovementIndex(tmpIndex);
            spider.setInitialMove(false);
            return true;
        } else if (destinationEntity.getType().compareTo("boulder") == 0 && spider.isInitialMove()) {
            // Initially, there is a boulder above it
            destinationEntity = Entity.findEntityAtPosition(surroundPositions.get(4));
            if (destinationEntity == null || destinationEntity.getType().compareTo("boulder") != 0) {
                // There is no boulder below it, start from below and reverse directions
                movingEntity.setPosition(surroundPositions.get(4));
                spider.setMovementIndex(4);
                spider.setInitialMove(false);
                clockwise = false;
                return true;
            } else {
                // Blocked by boulder above and bottom
                return false;
            }
        } else {
            clockwise = false;
            return false;
        }
    }

    // Checking if it can move anti-clockwise
    private boolean checkAntiClockwise(MovingEntity movingEntity) {
        Spider spider = (Spider) movingEntity;
        int tmpIndex = spider.getMovementIndex() - 1;
        if (tmpIndex < 0) {
            tmpIndex = surroundPositions.size() - 1;
        }
        Position destination = surroundPositions.get(tmpIndex);
        Entity destinationEntity = Entity.findEntityAtPosition(destination);

        if (spider.isInitialMove()) {
            // Only occurs when it maybe stuck
            destinationEntity = Entity.findEntityAtPosition(surroundPositions.get(4));
            return false;
        } else if (destinationEntity == null || destinationEntity.getType().compareTo("boulder") != 0) {
            // When it is not a boulder
            movingEntity.setPosition(destination);
            spider.setMovementIndex(tmpIndex);
            return true;
        } else {
            clockwise = true;
            return false;
        }
    }

    // Create the positions spider can move
    public void createSurroundPositions(Spider spider) {
        Position tmpPosition = spider.getSurroundPosition().translateBy(Direction.UP);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.RIGHT);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.DOWN);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.DOWN);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.LEFT);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.LEFT);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.UP);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
        tmpPosition = tmpPosition.translateBy(Direction.UP);
        surroundPositions.add(new Position(tmpPosition.getX(), tmpPosition.getY()));
    }
}
