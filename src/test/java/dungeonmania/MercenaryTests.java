package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MercenaryTests {
    @Test
    @DisplayName("Test Mercenary Spawns")
    public void testMercenarySpawn() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_battleTest_basicMercenary", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test Mercenary Moving Towards Player")
    public void testMercenaryMovingTowards() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryBasicTowards", "simpleConfig");
        for (int i = 0; i < 7; i++) {
            response = newDungeon.tick(Direction.UP);
        }
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position playerPosition = new Position(0, 1);
        assertTrue(Position.isAdjacent(entities.get(0).getPosition(), playerPosition));
    }

    @Test
    @DisplayName("Test hostile mercenary Go through portal")
    public void testMercenaryPortal() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryHostilePortal", "simpleConfig");
        for (int i = 0; i < 3; i++) {
            response = newDungeon.tick(Direction.RIGHT);
        }
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position playerPosition = new Position(10, 8);
        assertTrue(Position.isAdjacent(entities.get(0).getPosition(), playerPosition));
    }

    @Test
    @DisplayName("Test bribed merc follows player")
    public void testMercenaryBribeFollow() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_movementBribeAndFollow", "simpleConfig");

        // Collect treasure
        res = newDungeon.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "treasure").size());

        // Check in range
        EntityResponse merc = getEntities(res, "mercenary").get(0);
        

        // Await until merc on the right of player (next to player)
        while (!Position.isAdjacent(merc.getPosition(), Entity.getPlayer().getPosition())) {
            res = newDungeon.tick(Direction.UP);
            merc = getEntities(res, "mercenary").get(0);
        }

        // Bribe
        String mercId = merc.getId();
        assertDoesNotThrow(() -> newDungeon.interact(mercId));

        // Test that merc doesn't collide inside player
        Position expectedPosition = Entity.getPlayer().getPosition();
        res = newDungeon.tick(Direction.UP);
        assertNotEquals(expectedPosition, getEntities(res, "mercenary").get(0).getPosition());

        res = newDungeon.tick(Direction.LEFT);
        expectedPosition = Entity.getPlayer().getPosition();
        res = newDungeon.tick(Direction.UP);
        
        // Test that merc is at player's previous position
        merc = getEntities(res, "mercenary").get(0);
        Position actualPosition = merc.getPosition();
        assertEquals(expectedPosition, actualPosition);
    }

    @Test
    @DisplayName("Test bribed merc goes random position when player is invisible")
    public void testMercenaryMovementGoesRandom() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_mercenaryPotionMovement", "c_usePotionTest_duration");

        // Check in range
        EntityResponse merc = null;
        try {
            merc = getEntities(res, "mercenary").get(0);
        } catch (Exception e) {
            return;
        }
        
        // Await until merc on the right of player (next to player)
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.UP);
            merc = getEntities(res, "mercenary").get(0);
        }

        // Bribe
        String mercId = merc.getId();
        assertDoesNotThrow(() -> newDungeon.interact(mercId));

        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));

        res = newDungeon.getDungeonResponseModel();
        // Player moves to the LEFT, check that mercenary is moving randomly
        for (int i = 0; i < 4 ; i++) {
            Position prevPosition = getEntities(res, "mercenary").get(0).getPosition();
            res = newDungeon.tick(Direction.LEFT);
            Position mercP = getEntities(res, "mercenary").get(0).getPosition();
            assertTrue(Position.isAdjacent(prevPosition, mercP));
        }
    }

    @Test
    @DisplayName("Test merc random when player is invisible")
    public void testMercenaryMovementInvisRandom() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_mercenaryPotionMovement", "c_usePotionTest_duration");
        
        // Await until merc on the right of player (next to player)
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.UP);
        }

        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        res = assertDoesNotThrow(() -> newDungeon.tick(potionId));
        
        // Player moves to the LEFT, check that mercenary moving randomly
        for (int i = 0; i < 4 ; i++) {
            Position prevPosition = getEntities(res, "mercenary").get(0).getPosition();
            res = newDungeon.tick(Direction.LEFT);
            Position mercP = getEntities(res, "mercenary").get(0).getPosition();
            assertTrue(Position.isAdjacent(prevPosition, mercP) || prevPosition.equals(mercP));
        }
    }

    @Test
    @DisplayName("Test bribed merc follows when player is invincible")
    public void testMercenaryMovementStay() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_mercenaryPotionMovement", "c_usePotionTest_duration");

        // Check in range
        EntityResponse merc = getEntities(res, "mercenary").get(0);
        
        // Await until merc on the right of player (next to player)
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.UP);
            merc = getEntities(res, "mercenary").get(0);
        }

        // Bribe
        String mercId = merc.getId();
        assertDoesNotThrow(() -> newDungeon.interact(mercId));

        String potionId = getInventory(res, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));

        res = newDungeon.getDungeonResponseModel();
        Position initPosition = getEntities(res, "mercenary").get(0).getPosition();

        // Player moves to the UP into a wall, check that mercenary is staying still
        for (int i = 0; i < 4 ; i++) {
            res = newDungeon.tick(Direction.UP);
            merc = getEntities(res, "mercenary").get(0);
            assertEquals(initPosition, merc.getPosition());
        }
    }

    @Test
    @DisplayName("Test bribed merc cannot be in the same position as player")
    public void testMercenaryCannotBeSameAsPlayer() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_mercenaryPotionMovement", "simpleConfig");

        // Check in range
        EntityResponse merc = getEntities(res, "mercenary").get(0);
        
        // Await until merc on the right of player (next to player)
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.UP);
            merc = getEntities(res, "mercenary").get(0);
        }

        // Bribe
        String mercId = merc.getId();
        assertDoesNotThrow(() -> newDungeon.interact(mercId));

        res = newDungeon.getDungeonResponseModel();

        Position expectPosition = new Position(0, -2);

        res = newDungeon.tick(Direction.RIGHT);
        merc = getEntities(res, "mercenary").get(0);
        assertEquals(expectPosition, merc.getPosition());
    }
}

