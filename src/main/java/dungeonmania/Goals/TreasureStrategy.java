package dungeonmania.Goals;

import java.util.Arrays;
import java.util.List;
import dungeonmania.Entity.Entity;
import dungeonmania.DungeonManiaController;

public class TreasureStrategy implements GoalStrategy {
    private List<String> allTreasureTypes = Arrays.asList(new String[]{"treasure", "sun_stone"});

    @Override
    public boolean isGoalFulfilled() {
        int treasureGoal = DungeonManiaController.getCurrDungeon().getConfig("treasure_goal");
        long treasureInventory = 0;
        try {
            treasureInventory = Entity.getPlayer().getInventory().stream()
                                    .filter(e -> allTreasureTypes.contains(e.getType()))
                                    .count();
        } catch(Exception e) {
            System.out.println("Player does not exist, cannot do treasure goal");
        }
        
        if (treasureInventory >= treasureGoal) {
            return true;
        }
        return false;
    }

    @Override
    public String getGoalString() {
        return ":treasure";
    }
}
