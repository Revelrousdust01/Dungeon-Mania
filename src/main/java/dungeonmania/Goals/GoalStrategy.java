package dungeonmania.Goals;

import java.io.Serializable;

public interface GoalStrategy extends Serializable {
    public boolean isGoalFulfilled();
    public String getGoalString();
}

