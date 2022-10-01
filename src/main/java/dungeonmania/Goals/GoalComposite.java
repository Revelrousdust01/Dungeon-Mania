package dungeonmania.Goals;

import java.util.ArrayList;

public class GoalComposite implements GoalComponent {
    private String operator;

    ArrayList<GoalComponent> subgoals = new ArrayList<GoalComponent>();
    
    public GoalComposite(String operator) {
        this.operator = operator;
    }

    public void addSubgoal(String subgoal) {
        GoalLeaf leaf = new GoalLeaf(subgoal);
        subgoals.add(leaf);
    }

    public void addSubgoal(GoalComponent composite) {
        subgoals.add(composite);
    }

    @Override
    public String prettyPrint() {
        // 1 goal
        if (subgoals.size() == 1) {
            return subgoals.get(0).prettyPrint();
        }

        // 2 subgoals
        String goal1 = subgoals.get(0).prettyPrint();
        String goal2 = subgoals.get(1).prettyPrint();

        // Checking for if either of subgoal is completed (null)
        if ((goal1 == null || goal2 == null) && operator.equals("OR")) {
            return null;
        }
        if (goal1 == null && goal2 != null) {
            return goal2;
        } 
        if (goal1 != null && goal2 == null) {
            return goal1;
        }
        if (goal1 == null && goal2 == null) {
            return null;
        }
        return "(" + goal1 + " " + operator + " " + goal2 + ")";
    }

    @Override
    public String prettyPrintFirstTick() {
        // 1 goal
        if (subgoals.size() == 1) {
            return subgoals.get(0).prettyPrintFirstTick();
        }

        // 2 subgoals
        String goal1 = subgoals.get(0).prettyPrintFirstTick();
        String goal2 = subgoals.get(1).prettyPrintFirstTick();
        return "(" + goal1 + " " + operator + " " + goal2 + ")";
    }
}
