package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestSwitchBomb {
    @Test
    @DisplayName("Test activating bomb with switch")
    public void testBombActivation() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Boulder is one unit to right and switch is two unit to right
        // Bomb is one unit above the boulder's original position

        // Check boulder is on the switch
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position expectedBoulder = new Position(2, 0);
        Position expectedSwitch = new Position(2, 0);

        Position actualBoulder = getEntities(res, "boulder").get(0).getPosition();
        Position actualSwitch = getEntities(res, "switch").get(0).getPosition();
        assertEquals(expectedBoulder, actualBoulder);
        assertEquals(expectedSwitch, actualSwitch);

        // Check bomb is in inventory and not on map
        res = dmc.tick(Direction.UP);

        assertEquals(0, getEntities(res, "bomb").size());
        assertEquals(1, getInventory(res, "bomb").size());
        
        // Test bomb explodes when placed to already active switch
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }

        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, countEntityOfType(res, "bomb"));
        assertEquals(1, countEntityOfType(res, "player"));
        assertEquals(0, countEntityOfType(res, "boulder"));
        assertEquals(0, countEntityOfType(res, "switch"));
        
    }

    @Test
    @DisplayName("Test bomb behaviour after it is placed")
    public void testBombPlaced() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Check bomb is placed on same tile as player
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }
        Position expectedPlayer = new Position(1, -1);
        Position expectedBomb = new Position(1, -1);

        Position actualBomb = getEntities(res, "bomb").get(0).getPosition();
        Position actualPlayer = getEntities(res, "player").get(0).getPosition();
        assertEquals(expectedBomb, actualBomb);
        assertEquals(expectedPlayer, actualPlayer);

        // Test bomb cannot be picked up or stepped onto 
        // after it is placed and then player leaves bomb square
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        expectedPlayer = new Position(1, -2);

        actualBomb = getEntities(res, "bomb").get(0).getPosition();
        actualPlayer = getEntities(res, "player").get(0).getPosition();
        assertEquals(expectedBomb, actualBomb);
        assertEquals(expectedPlayer, actualPlayer);
        assertEquals(0, getInventory(res, "bomb").size());
        
    }

    @Test
    @DisplayName("Test active switch does not explode inadjacent bomb")
    public void testSwitchActiveBombUnadjacent() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Test that bomb placed inadjacent to active switch does not explode
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.UP);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }
        Position expectedBomb = new Position(1, -1);

        Position actualBomb = getEntities(res, "bomb").get(0).getPosition();
        assertEquals(expectedBomb, actualBomb);
    }

    @Test
    @DisplayName("Test inactive switch does not explode bomb")
    public void testSwitchInactive() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Test that bomb placed adjacent to inactive switch does not explode
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }
        Position expectedBomb = new Position(2, -1);

        Position actualBomb = getEntities(res, "bomb").get(0).getPosition();
        assertEquals(expectedBomb, actualBomb);
        
    }

    @Test
    @DisplayName("Test deactivated switch does not explode bomb")
    public void testSwitchDeactivate() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Deactivate means the boulder is rolled off the switch
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.DOWN);

        // Check that the boulder is actually pushed off
        DungeonResponse res = dmc.tick(Direction.RIGHT); // push the boulder off
        Position expectedBoulder = new Position(3, 0);

        Position actualBoulder = getEntities(res, "boulder").get(0).getPosition();
        assertEquals(expectedBoulder, actualBoulder);

        // Test that bomb placed adjacent to deactivated switch does not explode
        res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }
        Position expectedBomb = new Position(3, 0);

        Position actualBomb = getEntities(res, "bomb").get(0).getPosition();
        assertEquals(expectedBomb, actualBomb);

        
    }

    @Test
    @DisplayName("Test switch activated later explode bomb")
    public void testSwitchActivatedLater() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_explode", "simpleConfig");

        // Test that switch is activated after bomb is placed, bomb will still explode
        dmc.tick(Direction.UP);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, countEntityOfType(res, "bomb"));
        assertEquals(1, countEntityOfType(res, "player"));
        assertEquals(0, countEntityOfType(res, "boulder"));
        assertEquals(0, countEntityOfType(res, "switch"));
        
    }

    @Test
    @DisplayName("Test bomb explosion radius 2, square coverage")
    public void testBombExplosionRadius() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_radius", "c_switchBombTest_radius");

        // Test that everything in the 5x5 square (bomb is centre) is destroyed 
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }

        assertEquals(0, countEntityOfType(res, "bomb"));
        assertEquals(1, countEntityOfType(res, "player"));
        assertEquals(0, countEntityOfType(res, "boulder"));
        assertEquals(0, countEntityOfType(res, "switch"));
        assertEquals(0, countEntityOfType(res, "wall"));
        
    }

    @Test
    @DisplayName("Test bomb destroy types of entities")
    public void testBombExplodeDestroyEntityType() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("switchBombTest_entity", "simpleConfig");

        // Currently doesn't include test for moving entities - unsure how to stop them from moving 

        // Test that all static, collectible entites are destroyed
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String bombId = getInventory(res, "bomb").get(0).getId();
        try {
            res = dmc.tick(bombId);
        } catch(Exception e) {
            return;
        }

        assertEquals(0, countEntityOfType(res, "bomb"));
        assertEquals(0, countEntityOfType(res, "wood"));
        assertEquals(1, countEntityOfType(res, "player"));
        assertEquals(0, countEntityOfType(res, "boulder"));
        assertEquals(0, countEntityOfType(res, "switch"));
        assertEquals(0, countEntityOfType(res, "zombie_toast_spawner"));

        
    }
}
