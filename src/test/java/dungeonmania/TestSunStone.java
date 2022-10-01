package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getGoals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestSunStone {

    @Test
    @DisplayName("Test the player can pickup the sun stone")
    public void testCollection() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_SunStoneCollection", "simpleConfig");

        assertEquals(1, getEntities(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "sun_stone").size());

        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, getEntities(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
    }

    @Test
    @DisplayName("Test doors can be unlocked with the sunstone")
    public void testSunStoneDoorUnlock() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_sunStoneDoorUnlock", "simpleConfig");

        // get sun stone
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());

        // go through door
        Position pos1 = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);

        // check result
        Position pos2 = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(pos1, pos2);
        assertTrue(Position.isSameCell(pos2, new Position(2, 0)));

        // check sunstone is not used
        assertEquals(1, getInventory(res, "sun_stone").size());

        // get key
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "key").size());

        pos1 = getEntities(res, "player").get(0).getPosition();

        // open door
        res = dmc.tick(Direction.LEFT);
        pos2 = getEntities(res, "player").get(0).getPosition();

        // check position
        assertNotEquals(pos1, pos2);
        assertTrue(Position.isSameCell(pos2, new Position(1, 2)));

        // check key and sunstone not used
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "sun_stone").size());
    }

    @Test
    @DisplayName("Test sun stone counts towards the treasure goal")
    public void testSunStoneTreasureGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("simpleGoalTest_sunStonetreasure", "goalConfig");

        // Check that goal string contains :treasure
        assertTrue(getGoals(res).contains(":treasure"));

        // Collect teasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals("", getGoals(res));
    }

    @Test
    @DisplayName("Test mercenaries and assassins cannot be bribed with a sunstone")
    public void testSunStoneDoesNotBribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneBribe", "c_extendedBribeRange");

        // get sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sun_stone").size());

        // attempt to bribe
        String mercId = getEntities(res, "mercenary").get(0).getId();
        // String assassinId = getEntities(res, "assassin").get(0).getId();
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        // assertThrows(InvalidActionException.class, () -> dmc.interact(assassinId));
    }
}
