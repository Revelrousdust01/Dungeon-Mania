package dungeonmania.Entity.StaticEntity;

import dungeonmania.util.Position;

public class Wall extends StaticEntity implements RigidEntity {
    public Wall(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    public boolean isPassable() {
        return false;
    }
}
