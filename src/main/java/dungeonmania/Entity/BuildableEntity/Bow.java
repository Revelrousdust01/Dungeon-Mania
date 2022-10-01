package dungeonmania.Entity.BuildableEntity;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.BattleItem;

public class Bow extends BuildableEntity {
    private int bowMultiplier;

    @Override
    public int getBattleEffect() {
        return this.bowMultiplier;
    }
    
    public Bow() {
        super(DungeonManiaController.getCurrDungeon().getNewId(), "bow");
        this.remainingDurability = DungeonManiaController.getCurrDungeon().getConfig("bow_durability");
    }

    static public boolean make() {
        List<String> method = Bow.checkAvailable();
        if (method != null) {
            new Bow();
            removeUsedItems(method);
            return true;
        }
        return false;
        
    }

    static public List<String> checkAvailable() {
        // Create lists for each recipe
        ArrayList<List<String>> requiredToCraft = new ArrayList<>();
        List<String> method1 = Arrays.asList("wood", "arrow", "arrow", "arrow");
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