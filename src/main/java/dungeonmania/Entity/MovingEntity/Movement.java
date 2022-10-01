package dungeonmania.Entity.MovingEntity;

import dungeonmania.util.Position;
import java.io.Serializable;

interface Movement extends Serializable {
    public void move(MovingEntity movingEntity, Position playerPosition);
}
