package dungeonmania.Entity.CollectibleEntity;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.BattleItem;
import dungeonmania.util.Position;

public class Sword extends CollectibleEntity implements BattleItem {
    private int remainingDurability;
    private int swordDamage;
    
    public Sword (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.remainingDurability = DungeonManiaController.getCurrDungeon().getConfig("sword_durability");
    }

    @Override
    public int getBattleEffect() {
        return this.swordDamage;
    }

    @Override
    public void decreaseDurability() {
        remainingDurability--;
        if (remainingDurability <= 0 && getPlayer() != null) {
            getPlayer().removeFromInventory(this);
        }
    }
    
    @Override
    public int remainingDurability() {
        return this.remainingDurability;
    }

}