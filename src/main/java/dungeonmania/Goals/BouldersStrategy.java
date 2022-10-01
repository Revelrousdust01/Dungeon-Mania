package dungeonmania.Goals;

import java.util.List;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.FloorSwitch;

public class BouldersStrategy implements GoalStrategy {
    @Override
    public boolean isGoalFulfilled() {
        List<Entity> switches = Entity.entityType("switch");

        // Check if there is any switches that doesn't have a boulder ontop
        for(Entity e : switches) {
            FloorSwitch s = (FloorSwitch) e;
            if(!s.isTriggered()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getGoalString() {
        return ":boulders";
    }
}
