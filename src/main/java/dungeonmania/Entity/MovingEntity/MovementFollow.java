package dungeonmania.Entity.MovingEntity;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public class MovementFollow implements Movement {
    private Movement movementTowards = new MovementTowards();
    private Position playerPrevPosition;

    @Override
    public void move(MovingEntity movingEntity, Position playerPosition) {
        playerPrevPosition = Entity.getPlayer().getPrevPosition();

        if (Position.isAdjacent(movingEntity.getPosition(), playerPrevPosition) && !playerPosition.equals(playerPrevPosition)) {
            // When we can move onto player's previous position and it is not where player is
            movingEntity.setPosition(playerPrevPosition);
        } else if (!Position.isAdjacent(playerPosition, movingEntity.getPosition())) {
            // When we can't move onto player's previous position and it is not where player is
            movementTowards.move(movingEntity, playerPosition);
        } 
    }
}
    

