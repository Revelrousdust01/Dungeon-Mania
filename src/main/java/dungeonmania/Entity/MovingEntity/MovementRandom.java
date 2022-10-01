package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovementRandom implements Movement{
    @Override 
    public void move(MovingEntity movingEntity, Position playerPosition) {
        // Put the directions into a list (unless there is a better way to get the directions)
        List<Direction> directions = new ArrayList<Direction>();
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
        directions.add(Direction.LEFT);
        directions.add(Direction.RIGHT);
        
        // Select a random direction and move
        Random rand = new Random();
        List<Direction> notRigid = MovingEntity.checkForREntity(movingEntity.getPosition());
        Direction randDirection = notRigid.get(rand.nextInt(notRigid.size()));
        movingEntity.setPosition(movingEntity.getPosition().translateBy(randDirection));
    }
}
    

