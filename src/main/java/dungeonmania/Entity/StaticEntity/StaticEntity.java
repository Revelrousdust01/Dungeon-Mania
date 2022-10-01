package dungeonmania.Entity.StaticEntity;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public abstract class StaticEntity extends Entity {
    public StaticEntity(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }
    
}
