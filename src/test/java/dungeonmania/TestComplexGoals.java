package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestComplexGoals {
    @Test
    @DisplayName("Test exit has to be completed last in AND goal")
    public void testComplexExitANDGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_exitAND", "goalConfig");

        // Check that goal string contains :exit AND :treasure
        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains("AND"));
        assertTrue(getGoals(res).contains(":treasure"));

        // Test that exit is reached first, then treasure is collected 
        // But goal is not complete since player moved off exit
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals("", getGoals(res));

        // Check that going back to exit completes the goal
        res = dmc.tick(Direction.LEFT);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test OR first evaluated goal")
    public void testComplexORFirstGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_OR", "goalConfig");

        // Check that goal string contains :boulders OR :treasure
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains("OR"));
        assertTrue(getGoals(res).contains(":treasure"));

        // Test that completing boulders OR goal wins the dungeon
        res = dmc.tick(Direction.RIGHT);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test OR second evaluated goal")
    public void testComplexORSecondGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_OR", "goalConfig");

        // Check that goal string contains :boulders OR :treasure
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains("OR"));
        assertTrue(getGoals(res).contains(":treasure"));

        // Test that completing treasure OR goal wins the dungeon
        res = dmc.tick(Direction.LEFT);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test AND goal")
    public void testComplexTreasureANDSwitchGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_AND", "goalConfig");

        // Check that goal string contains :boulders AND :treasure
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains("AND"));
        assertTrue(getGoals(res).contains(":treasure"));

        // Check that completing just boulders doesn't result in win
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals("", getGoals(res));

        // Test that completing both the boulders and treasure result in win
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test unachieve switch AND treasure goal")
    public void testComplexUnachieveSwitchGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_AND", "goalConfig");

        // Check that goal string contains :boulders AND :treasure
        assertTrue(getGoals(res).contains(":boulders"));
        assertTrue(getGoals(res).contains("AND"));
        assertTrue(getGoals(res).contains(":treasure"));

        // Push the boulders onto the switch then off
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        // Test that collecting the treasure with unachieved boulders does not result in win
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertNotEquals("", getGoals(res));

    }

    @Test
    @DisplayName("Test complex goal evaluated after first tick")
    public void testComplexFirstTickGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("complexGoalTest_firstTick", "goalConfig");

        // Check that goal string contains :exit OR :treasure, even when 0th tick is on the exit
        assertTrue(getGoals(res).contains(":exit"));
        assertTrue(getGoals(res).contains("OR"));
        assertTrue(getGoals(res).contains(":treasure"));
    }
}
