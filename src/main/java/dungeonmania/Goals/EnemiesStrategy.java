package dungeonmania.Goals;

import dungeonmania.Entity.Entity;
import dungeonmania.DungeonManiaController;

public class EnemiesStrategy implements GoalStrategy {
    @Override
    public boolean isGoalFulfilled() {
        int enemyGoal = DungeonManiaController.getCurrDungeon().getConfig("enemy_goal");

        // Assuming number of things in battle response = enemies killed
        int enemiesKilled = DungeonManiaController.getCurrDungeon().getBattles().size();

        if (enemiesKilled >= enemyGoal &&
                Entity.entityType("zombie_toast_spawner").size() == 0) {
            return true;
        }

        return false;
    }

    @Override
    public String getGoalString() {
        return ":enemies";
    }

}
