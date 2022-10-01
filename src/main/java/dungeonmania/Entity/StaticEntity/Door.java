package dungeonmania.Entity.StaticEntity;

import dungeonmania.Entity.CollectibleEntity.Key;
import dungeonmania.Entity.CollectibleEntity.SunStone;
import dungeonmania.util.Position;

public class Door extends StaticEntity implements RigidEntity {
    private boolean open;
    private int keyNum;

    public Door(boolean isInteractable, Position position, String id, String type, int keyNum) {
        super(isInteractable, position, id, type);
        this.open = false;
        this.keyNum = keyNum;
    }

    public boolean isPassable() {
        return open;
    }

    public boolean unlock(Key key) {
        if (key != null && key.getKeyNum() == this.keyNum) {
            open = true;
            return true;
        } else {
            return false;
        }
    }

    public void unlock(SunStone stone) {
        open = true;
    }
}
