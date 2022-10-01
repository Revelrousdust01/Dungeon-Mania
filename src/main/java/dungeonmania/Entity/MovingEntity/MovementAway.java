package dungeonmania.Entity.MovingEntity;

import java.util.List;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovementAway implements Movement {
    @Override
    public void move(MovingEntity movingEntity, Position playerPosition) {
        // Calculate the position relative to the movingEntity
        Position relative = Position.calculatePositionBetween(movingEntity.getPosition(), playerPosition);
        List<Direction> notRigid = MovingEntity.checkForREntity(movingEntity.getPosition());

        // Try to move out of the way
        // NOTE: You can easily corner the entity this way because if it can't move away from the player, it will not move at all
        for (Direction direction: notRigid) {
            // Going through all the possible direction and seeing which one is considered moving away from the player
            if (direction.equals(Direction.UP) && relative.getY() > 0) {
                // Player below, move UP
                movingEntity.setPosition(movingEntity.getPosition().translateBy(Direction.UP));
                return;
            } else if (direction.equals(Direction.DOWN) && relative.getY() < 0) {
                // Player above, move DOWN
                movingEntity.setPosition(movingEntity.getPosition().translateBy(Direction.DOWN));
                return;
            } else if (direction.equals(Direction.LEFT) && relative.getX() > 0) {
                // Player on the right, move left
                movingEntity.setPosition(movingEntity.getPosition().translateBy(Direction.LEFT));
                return;
            } else if (direction.equals(Direction.RIGHT) && relative.getX() < 0) {
                // Player on the left, move right
                movingEntity.setPosition(movingEntity.getPosition().translateBy(Direction.RIGHT));
                return;
            }
        }
    }
}
    
