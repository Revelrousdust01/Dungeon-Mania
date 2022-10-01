package dungeonmania.Entity.BuildableEntity;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import dungeonmania.DungeonManiaController;

public class MidnightArmour extends BuildableEntity {
    private int attackModifier;
    private int defenceModifier;

    public MidnightArmour() {
        super(DungeonManiaController.getCurrDungeon().getNewId(), "midnight_armour");
        this.attackModifier = DungeonManiaController.getCurrDungeon().getConfig("midnight_armour_attack");
        this.attackModifier = DungeonManiaController.getCurrDungeon().getConfig("midnight_armour_defence");
    }

    public int getMidnightAttack() {
        return this.attackModifier;
    }
    public int getMidnightDefence() {
        return this.defenceModifier;
    }

    @Override
    public int getBattleEffect() {
        // TODO Auto-generated method stub
        return 0;
    }

    static public boolean make() {
        List<String> method = checkAvailable();
        if (method != null) {
            new MidnightArmour();
            removeUsedItems(method);
            return true;
        }
        return false;
        
    }

    static public List<String> checkAvailable() {
        // No recipes available if zombie is on the map
        if (entityType("zombie_toast").size() != 0) {
            return null;
        }

        // Create lists for each recipe
        List<List<String>> requiredToCraft = new ArrayList<>();
        List<String> method1 = Arrays.asList("sword", "sun_stone");
        requiredToCraft.add(method1);

        // Craft the item if inventory has sufficient items
        for (List<String> method : requiredToCraft) {
            if (hasSufficientItems(method)) {
                return method;
            }
        }

        return null;

    }
}