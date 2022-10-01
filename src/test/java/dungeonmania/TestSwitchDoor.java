package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestSwitchDoor {
    
    // Some similar basic test from door
    @Test
    @DisplayName("Test the door cannnot be passed without its key")
    public void testSwitchDoorCollision() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_blocksPlayer", "milestone3SimpleConfig");

        // get postion
        res = dmc.tick(Direction.RIGHT);
        Position initialPos = getEntities(res, "player").get(0).getPosition();
        
        // Move right and hit door
        res = dmc.tick(Direction.RIGHT);
        Position newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);
    }

    @Test
    @DisplayName("Check that a switch door can be opened with is corresponding key")
    public void testSwitchDoorKeyUnlock() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_keyOpen", "milestone3SimpleConfig");

        // pick up key
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "key").size());
        Position initialPos = getEntities(res, "player").get(0).getPosition();

        // walk through door and check key is gone
        res = dmc.tick(Direction.LEFT);
        Position newPos =  getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(-2, 0)));
        assertEquals(0, getInventory(res, "key").size());

        // Move off the door and check that the player can move back
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(newPos, getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.LEFT);
        assertEquals(newPos, getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Tests that a switch door can be opened with or logic")
    public void testSwitchDoorOrUnlock() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_orLogic", "milestone3SimpleConfig");

        // test none
        res = dmc.tick(Direction.DOWN);
        Position initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        Position newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test single adjacent wire active
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));

        // test two
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));

        // test three
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));

        // test four
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        
        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.LEFT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));
    }

    @Test
    @DisplayName("Tests that a switch door can be opened with and logic (with two connected wires/switches)")
    public void testSwitchDoorAndUnlockDouble() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_andLogicDouble", "milestone3SimpleConfig");

        // test none
        res = dmc.tick(Direction.DOWN);
        Position initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        Position newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test single adjacent wire active out of two
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test two out of two
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));
    }

    @Test
    @DisplayName("Tests that a switch door can be opened with xor logic")
    public void testSwitchDoorXorUnlock() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_xorLogic", "milestone3SimpleConfig");

        // test none
        res = dmc.tick(Direction.DOWN);
        Position initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        Position newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test single adjacent wire active
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));

        // test two
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test three
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.LEFT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test four
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);

        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);
        
        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.LEFT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);
    }

    @Test
    @DisplayName("Tests that a switch door can be opened with co_and logic")
    public void testSwitchDoorCoAndUnlock() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_CoAndLogicDouble", "milestone3SimpleConfig");

        // test none
        res = dmc.tick(Direction.DOWN);
        Position initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        Position newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test single adjacent wire active out of two
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);

        // test two out of two
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);

        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertEquals(initialPos, newPos);
        
        // deactivate switches
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);

        // test double simultanious
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        initialPos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.RIGHT);
        newPos = getEntities(res, "player").get(0).getPosition();
        assertNotEquals(initialPos, newPos);
        assertTrue(Position.isSameCell(newPos, new Position(0, 0)));
    }

    @Test
    @DisplayName("Tests that a switch door that is unlocked by a key will always be unlocked (even with switch pulse)")
    public void testSwitchDoorKeyUnlockOverride() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_keyOpen_staysOpen", "milestone3SimpleConfig");

        // get key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        
        // unlock door
        Position initPos = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(initPos, getEntities(res, "player").get(0).getPosition());
        assertEquals(0, getInventory(res, "key").size());

        // send signal to door
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);

        // remove signal
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        // go through door
        initPos = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(initPos, getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Tests that a switch door that is opened by a switch will not consume a key")
    public void testSwitchDoorUnlockPriority() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_switchDoor_keyOpen_doesNotConsume", "milestone3SimpleConfig");

        // send signal to door
        dmc.tick(Direction.RIGHT);
        
        // get key
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "key").size());
        
        
        // go through door
        Position initPos = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(initPos, getEntities(res, "player").get(0).getPosition());

        assertEquals(1, getInventory(res, "key").size());
    }
}
