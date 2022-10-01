package dungeonmania.Entity.BuildableEntity;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.BattleItem;

public class Shield extends BuildableEntity {
    private int shieldDefence;
    
    @Override
    public int getBattleEffect() {
        return this.shieldDefence;
    }

    public Shield() {
        super(DungeonManiaController.getCurrDungeon().getNewId(), "shield");
        this.remainingDurability = DungeonManiaController.getCurrDungeon().getConfig("shield_durability");

    }

    static public boolean make() {
        List<String> method = Shield.checkAvailable();
        if (method != null) {
            new Shield();
            removeUsedItems(method);
            return true;
        }
        return false;
        
    }

    static public List<String> checkAvailable() {
        // Create lists for each recipe
        List<List<String>> requiredToCraft = new ArrayList<>();
        List<String> method1 = Arrays.asList("wood", "wood", "treasure");
        requiredToCraft.add(method1);
        List<String> method2 = Arrays.asList("wood", "wood", "key");
        requiredToCraft.add(method2);

        // Craft the item if inventory has sufficient items
        for (List<String> method : requiredToCraft) {
            if (hasSufficientItems(method)) {
                return method;
            }
        }

        List<String> alternativeRecipe = sunStoneReplacement(requiredToCraft);
        if (alternativeRecipe != null) return alternativeRecipe;

        return null;

    }
}