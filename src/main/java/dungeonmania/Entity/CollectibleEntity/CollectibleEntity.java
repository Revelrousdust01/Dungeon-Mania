package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public abstract class CollectibleEntity extends Entity {
    public CollectibleEntity (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

}
