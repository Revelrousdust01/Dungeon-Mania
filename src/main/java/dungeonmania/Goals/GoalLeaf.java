package dungeonmania.Goals;

public class GoalLeaf implements GoalComponent {
    private GoalStrategy goal;

    public GoalLeaf(String goal) {
        switch(goal) {
            case("enemies"):
                this.goal = new EnemiesStrategy();
                break;
            case("boulders"):
                this.goal = new BouldersStrategy();
                break;
            case("exit"):
                this.goal = new ExitStrategy();
                break;
            case("treasure"):
                this.goal = new TreasureStrategy();
                break;
        }
    }

    @Override
    public String prettyPrint() {
        if (goal.isGoalFulfilled()) {
            return null;
        }
        return goal.getGoalString();
    }

    @Override
    public String prettyPrintFirstTick() {
        return goal.getGoalString();
    }
}
