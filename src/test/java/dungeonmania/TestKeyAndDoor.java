package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestKeyAndDoor {

    @Test
    @DisplayName("Test the door cannnot be passed without its key")
    public void testDoorCollision() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_singleDoor_blocksPlayer", "simpleConfig");

        // Move left and hit door
        res = dmc.tick(Direction.LEFT);
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(new Position(3, 3), pos);
    }

    @Test
    @DisplayName("Check that the key can unlock its corresponding door")
    public void testDoorUnlock() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();
        assertEquals(1, getInventory(res, "key").size());

        // walk through door and check key is gone
        res = dmc.tick(Direction.RIGHT);
        Position newPos =  getEntities(res, "player").get(0).getPosition();
        assertEquals(0, getInventory(res, "key").size());
        assertNotEquals(pos, newPos);

        // Move off the door and check that the player can move back
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(newPos, getEntities(res, "player").get(0).getPosition());

        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Checks the player can only have one key in their inventory")
    public void testMaxOneKey() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_multipleKeysAndDoors", "simpleConfig");

        // Pick up one key
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "key").size());
        
        // Goto another key
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getEntities(res, "key").size());
    }

    @Test
    @DisplayName("Checks that a door cannot be unlocked after crafting with its key")
    public void testForeverLocked() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_craftUsingKey_DoorIsLocked", "simpleConfig");

        // Collect the 2 wood + 1 key
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        DungeonResponse res1 = dmc.tick(Direction.UP);

        assertEquals(2, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "key").size());

        // Test that shield is built successfully
        assertDoesNotThrow(() -> dmc.build("shield"));
        DungeonResponse res2 = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "key").size());
        assertEquals(1, getInventory(res2, "shield").size());

        // Attempt to go through door
        Position pos = getEntities(res2, "player").get(0).getPosition();
        DungeonResponse res3 = dmc.tick(Direction.UP);
        assertEquals(pos, getEntities(res3, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Tests that a key will only unlock its corresponding door")
    public void testMultipleDoorsAndKeys() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_multipleKeysAndDoors", "simpleConfig");

        // Get key 1
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "key").size());

        // Move to door 2
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();

        res = dmc.tick(Direction.DOWN);
        assertEquals(pos, getEntities(res, "player").get(0).getPosition());

        // Go through door 1
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.LEFT);
        Position pos2 = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos2, getEntities(res, "player").get(0).getPosition());
    }
}
