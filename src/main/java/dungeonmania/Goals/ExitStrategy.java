package dungeonmania.Goals;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public class ExitStrategy implements GoalStrategy {
    @Override
    public boolean isGoalFulfilled() {
        // Check to ensure there is an exit on the map
        if (Entity.entityType("exit").size() < 1) {
            return false;
        }
        if(Entity.getPlayer()!= null){
            Position player = Entity.getPlayer().getPosition();
            Position exit = Entity.entityType("exit").get(0).getPosition();
            if(player.equals(exit)) {
                return true;
            }
        }
       
        return false;
    }

    @Override
    public String getGoalString() {
        return ":exit";
    }
}
