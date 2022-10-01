package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestSimpleGoals {
    @Test
    @DisplayName("Test simple exit goal")
    public void testSimpleExitGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_exit", "goalConfig");

        // Check that goal string contains :exit
        assertTrue(getGoals(res).contains(":exit"));

        // Reach exit
        res = dmc.tick(Direction.RIGHT);
        Position player = getEntities(res, "player").get(0).getPosition();
        Position exit = getEntities(res, "exit").get(0).getPosition();
        assertTrue(player.equals(exit));
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test goal evaluated after first tick")
    public void testGoalAfterFirstTick() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_exitFirstTick", "goalConfig");

        // Check that goal string contains :exit even when 0th tick is on the exit
        assertTrue(getGoals(res).contains(":exit"));
    }

    @Test
    @DisplayName("Test simple treasure goal")
    public void testSimpleTreasureGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_treasure", "goalConfig");

        // Check that goal string contains :treasure
        assertTrue(getGoals(res).contains(":treasure"));

        // Collect teasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test simple kill enemy goal, no spawner")
    public void testSimpleEnemyNoSpawnerGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_enemyNoSpawner", "goalConfig");

        // Check that goal string contains :enemies
        assertTrue(getGoals(res).contains(":enemies"));

        // Mercenary would follow the player 
        // So moving right would result them in same square and battle

        // Test that killing the mercenary fulfills the enemies goal
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, countEntityOfType(res, "mercenary"));
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test simple kill enemy goal, has spawner")
    public void testSimpleEnemyHasSpawnerGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_enemySpawner", "goalConfig");

        // Check that goal string contains :enemies
        assertTrue(getGoals(res).contains(":enemies"));

        // Mercenary would follow the player 
        // So moving right would result them in same square and battle
        // Test that enemy is killed but zombie spawner still exist so goal isn't fulfilled
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, countEntityOfType(res, "mercenary"));
        assertEquals(getGoals(res), ":enemies");

        res = dmc.tick(Direction.UP);
        String spawnerId = getEntities(res, "zombie_toast_spawner").get(0).getId();
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, countEntityOfType(res2, "zombie_toast_spawner"));
        assertEquals("", getGoals(res2));
        
    }

    @Test
    @DisplayName("Test simple switch goal")
    public void testSimpleSwitchGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_switch", "goalConfig");

        // Check that goal string contains :boulders
        assertTrue(getGoals(res).contains(":boulders"));

        // Push the boulder onto the switch
        res = dmc.tick(Direction.RIGHT);
        Position floorSwitch = getEntities(res, "switch").get(0).getPosition();
        Position boulder = getEntities(res, "boulder").get(0).getPosition();
        assertTrue(floorSwitch.equals(boulder));
        assertEquals("", getGoals(res));
    }
}
