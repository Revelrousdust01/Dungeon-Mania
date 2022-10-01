package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.DungeonManiaController;
import dungeonmania.util.Position;

public class InvincibilityPotion extends Potion {
    public InvincibilityPotion (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.remainingDuration = DungeonManiaController.getCurrDungeon().getConfig("invincibility_potion_duration");
    }
    
}
