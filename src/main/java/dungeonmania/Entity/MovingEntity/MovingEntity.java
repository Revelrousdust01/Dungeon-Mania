package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Observer;
import dungeonmania.Entity.Subject;
import dungeonmania.Entity.StaticEntity.RigidEntity;
import dungeonmania.Entity.StaticEntity.Swamp;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public abstract class MovingEntity extends Entity implements Observer, Enemy {

    private Movement movementState;
    private Position playerPosition;
    private int ticker = 0;
    
    public MovingEntity(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    public void setMovementState(Movement movementState){
        this.movementState = movementState;
    }

    /**
     * Get movement state for testing purposes
     * @return
     */
    public Movement getMovementState(){
        return movementState;
    }

    // Returns the directions of entity that is not a rigid entity
    public static List<Direction> checkForREntity(Position position) {
        List<Direction> movableDirections = new ArrayList<Direction>();
        List<Direction> directions = new ArrayList<Direction>() {{
            add(Direction.UP);
            add(Direction.DOWN);
            add(Direction.LEFT);
            add(Direction.RIGHT);
        }};

        for (Direction direction: directions) {
            Position destinationPosition = position.translateBy(direction);
            Entity destinationEntity = findEntityAtPosition(destinationPosition);
            if (!(destinationEntity instanceof RigidEntity)) {
                // When it is not a rigid entity
                movableDirections.add(direction);
            } else {
                // When it is a rigid entity
                RigidEntity destinationRigid = (RigidEntity) destinationEntity;
                if (destinationRigid.isPassable()) {
                    // When it is a passable rigid entity i.e., opened doors
                    movableDirections.add(direction);
                }
            }
        }
        return movableDirections;
    }

    public void performMove() {
        // Check how long it needs to be stuck on the position
        if (ticker != 0) {
            ticker -= 1;
            return;
        }
        // Move
        movementState.move(this, playerPosition);

        // Check if the new position is a swamp
        if (Swamp.checkSwamp(position) != null) {
            // When it is a swamp
            ticker = Swamp.checkSwamp(position).getMovementFactor();
        }
    }

    @Override
    public void update(Subject subject) {
        this.playerPosition = subject.getPosition();
    }
}
