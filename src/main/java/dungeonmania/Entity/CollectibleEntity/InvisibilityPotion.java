package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.DungeonManiaController;
import dungeonmania.util.Position;

public class InvisibilityPotion extends Potion {
    public InvisibilityPotion (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.remainingDuration = DungeonManiaController.getCurrDungeon().getConfig("invisibility_potion_duration");
    }
    
}
