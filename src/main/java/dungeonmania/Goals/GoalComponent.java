package dungeonmania.Goals;

import java.io.Serializable;

public interface GoalComponent extends Serializable {
    public String prettyPrint();
    public String prettyPrintFirstTick();
}
