package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public abstract class Potion extends CollectibleEntity {
    protected int remainingDuration;
    
    public Potion (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    public void use() {
        Entity.getPlayer().removeFromInventory(this);
        Entity.getPlayer().addPotionToQueue(this);
    }

    static public void decreaseDuration() {
        Potion currPotion = Entity.getPlayer().getCurrPotion();
        if (currPotion == null) {
            return;
        }

        currPotion.remainingDuration--;
        if (currPotion.remainingDuration <= 0) {
            Entity.getPlayer().removePotionFromQueue(currPotion);
        }
    }

    public int getRemainingDuration() {
        return remainingDuration;
    }
}
