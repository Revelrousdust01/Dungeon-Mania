package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestInteract {

    @Test
    @DisplayName("Tests an exception is thrown when an invalid entity is given")
    public void testInvalidEntity() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_noEntities", "simpleConfig");

        assertThrows(IllegalArgumentException.class, () -> dmc.interact(getEntities(res, "player").get(0).getId() + "helloxyz"));
    }
    
    @Test
    @DisplayName("Tests that a mercenary is able to be bribed")
    public void testMercenaryBribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bribeSingleMercenary", "simpleConfig");

        // Collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());

        // Check in range
        EntityResponse merc = getEntities(res, "mercenary").get(0);
        Position mercPos = merc.getPosition();
        String mercId = merc.getId();
        assertTrue(mercPos.getX() == 2 && mercPos.getY() == 0);

        // Bribe
        assertDoesNotThrow(() -> dmc.interact(mercId));

        // Check inventory
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "treasure").size());

        // Check bribed
        Mercenary mercEntity = (Mercenary) DungeonManiaController.getCurrDungeon().getAllEntities().stream().filter(ent -> ent.getType().equals("mercenary")).findFirst().orElse(null);
        assertFalse(mercEntity.isInteractable());
    }

    @Test
    @DisplayName("Tests the range of bribes")
    public void testBribeRange() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_bribeRangeMercenary", "c_bribeRangeMercenary");

        // Collect treasure
        DungeonResponse res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "treasure").size());
        
        // Attempt to bribe
        List<String> mercIds = getEntities(res, "mercenary").stream().map(ent -> ent.getId()).collect(Collectors.toList());
        for (String mercId: mercIds) {
            assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));
        }

        // Move in range
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.interact(mercIds.get(2)));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Tests if the player does not have enough gold and the gold cost")
    public void testBribeGoldCost() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bribeCostMercenary", "c_bribeCostMercenary");

        // Attempt to bribe and check
        String mercId = getEntities(res, "mercenary").get(0).getId();
        assertEquals(0, getInventory(res, "treasure").size());
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, getInventory(res, "treasure").size());
        assertThrows(InvalidActionException.class, () -> dmc.interact(mercId));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, getInventory(res, "treasure").size());
        assertDoesNotThrow(() -> dmc.interact(mercId));

        Mercenary mercEntity = (Mercenary) DungeonManiaController.getCurrDungeon().getAllEntities().stream().filter(ent -> ent.getType().equals("mercenary")).findFirst().orElse(null);
        assertFalse(mercEntity.isInteractable());
    }

    @Test
    @DisplayName("Tests exception when a spawner is out of range")
    public void testOutOfRangeSpawnerDistruction() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_zombieSpawnerOutOfRange", "simpleConfig");
        DungeonResponse res = dmc.tick(Direction.UP);
        List<EntityResponse> zombieToastSpawners = getEntities(res, "zombie_toast_spawner");
        assertEquals(1, zombieToastSpawners.size());

        assertThrows(InvalidActionException.class, () -> dmc.interact(zombieToastSpawners.get(0).getId()));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, countEntityOfType(res, "zombie_toast_spawner"));
    }

    /*
    @Test
    @DisplayName("Tests that a spawner is destoryed")
    public void testSpawnerDistruction() {
        //
    }

    @Test
    @DisplayName("Tests exception when the player does not have a weapon and attempts to destroy a spawner")
    public void testNoWeaponSpawnerDistruction() {
        //
    }
    */
}
