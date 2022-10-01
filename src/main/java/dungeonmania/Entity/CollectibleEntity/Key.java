package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.util.Position;

public class Key extends CollectibleEntity {
    private int keyNum;

    public Key (boolean isInteractable, Position position, String id, String type, int keyNum) {
        super(isInteractable, position, id, type);
        this.keyNum = keyNum;
    }

    public int getKeyNum() {
        return keyNum;
    }
    
}