package dungeonmania;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.Swamp;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestMovement {
    @Test
    @DisplayName("Test movementTowards")
    public void testMovementTowards() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_movementTowardsTest", "simpleConfig");
        response = newDungeon.tick(Direction.RIGHT);
        response = newDungeon.tick(Direction.RIGHT);
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position expectedPosition = new Position(6, 7);
        assertEquals(expectedPosition, entities.get(0).getPosition());
    }

    @Test
    @DisplayName("Test movementAway to left")
    public void testMovementAwayLeft() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_movementAwayLeftTest", "c_usePotionTest_duration");
        response = newDungeon.tick(Direction.RIGHT);
        String entityId = getInventory(response, "invincibility_potion").get(0).getId();
        response = assertDoesNotThrow(() -> newDungeon.tick(entityId));
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position expectedPosition = new Position(4, 4);
        assertEquals(expectedPosition, entities.get(0).getPosition());
    }

    @Test
    @DisplayName("Test movementAway to right")
    public void testMovementAwayRight() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_movementAwayRightTest", "c_usePotionTest_duration");
        response = newDungeon.tick(Direction.LEFT);
        String entityId = getInventory(response, "invincibility_potion").get(0).getId();
        response = assertDoesNotThrow(() -> newDungeon.tick(entityId));
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position expectedPosition = new Position(6, 4);
        assertEquals(expectedPosition, entities.get(0).getPosition());
    }

    @Test
    @DisplayName("Test Mercenary Moving around walls")
    public void testMercenaryMovingAround() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryMovementAround", "simpleConfig");
        for (int i = 0; i < 7; i++) {
            response = newDungeon.tick(Direction.DOWN);
        }
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position playerPosition = new Position(5, 5);
        assertTrue(Position.isAdjacent(entities.get(0).getPosition(), playerPosition));
    }

    @Test
    @DisplayName("Test don't move when it cannot reach player")
    public void testMercenaryStop() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_movementTowardsNoPath", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position mercPosition = entities.get(0).getPosition();
        for (int i = 0; i < 5; i++) {
            response = newDungeon.tick(Direction.DOWN);
        }
        entities = getEntities(response, "mercenary");
        assertEquals(entities.get(0).getPosition(), mercPosition);
    }

    @Test
    @DisplayName("Test movement follow takes previous place")
    public void testFollowPrevious() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_movementFollowGoesToPrevious", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Position belowPlayer = new Position(0, -1);

        // Gets the coin
        response = newDungeon.tick(Direction.UP);

        // Bribe
        EntityResponse merc = getEntities(response, "mercenary").get(0);
        String mercId = merc.getId();
        assertDoesNotThrow(() -> newDungeon.interact(mercId));

        response = newDungeon.tick(Direction.UP);

        entities = getEntities(response, "mercenary");
        assertEquals(belowPlayer, entities.get(0).getPosition());
    }

    ////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Starting of swamp testing ////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("Test spider affected by swamp")
    public void testSwampSpider() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_swampSpider", "simpleConfig");

        // Get the swamp entity
        Entity swampEntity = Entity.findEntityAtPosition(new Position(6, 4));
        assertTrue(swampEntity.getType().equals("swamp_tile"));
        Swamp swamp = (Swamp) swampEntity;

        response = newDungeon.tick(Direction.RIGHT);
        response = newDungeon.tick(Direction.RIGHT);
        EntityResponse spider = getEntities(response, "spider").get(0);
        Position stuckPosition = spider.getPosition();

        // Check the spider is on swamp
        assertTrue(stuckPosition.equals(new Position(6, 4)));

        // Check that the position of spider does not change for the duration of movement factor
        for (int i = 0; i < swamp.getMovementFactor(); i++) {
            response = newDungeon.tick(Direction.RIGHT);
            spider = getEntities(response, "spider").get(0);
            assertTrue(spider.getPosition().equals(stuckPosition));
        }
        
        // Check that spider now moved out of swamp
        response = newDungeon.tick(Direction.RIGHT);
        spider = getEntities(response, "spider").get(0);
        Position newPosition = stuckPosition.translateBy(Direction.DOWN);
        assertTrue(spider.getPosition().equals(newPosition));
    }

    @Test
    @DisplayName("Test zombie affected by swamp")
    public void testSwampZombie() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_swampZombie", "simpleConfig");
        
        // Get the zombie onto a swamp
        response = newDungeon.tick(Direction.RIGHT);
        EntityResponse zombie = getEntities(response, "zombie").get(0);
        Position stuckPosition = zombie.getPosition();

        // Get the swamp entity
        Swamp swamp = Swamp.checkSwamp(stuckPosition);
        assertTrue(swamp.getType().equals("swamp_tile"));

        // Check that the position of zombie does not change for the duration of movement factor
        for (int i = 0; i < swamp.getMovementFactor(); i++) {
            response = newDungeon.tick(Direction.RIGHT);
            zombie = getEntities(response, "zombie").get(0);
            assertTrue(zombie.getPosition().equals(stuckPosition));
        }
        
        // Check that zombie no longer in the same swamp
        response = newDungeon.tick(Direction.RIGHT);
        zombie = getEntities(response, "zombie").get(0);
        assertTrue(!zombie.getPosition().equals(stuckPosition));
    }

    @Test
    @DisplayName("Test mercenary affected by swamp")
    public void testSwampMercenary() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_swampDirectTowards", "simpleConfig");

        response = newDungeon.tick(Direction.UP);
        EntityResponse mercenary = getEntities(response, "mercenary").get(0);
        Position stuckPosition = mercenary.getPosition();

        // Get the swamp entity
        Swamp swamp = Swamp.checkSwamp(stuckPosition);
        assertTrue(swamp.getType().equals("swamp_tile"));

        // Check that the position of mercenary does not change for the duration of movement factor
        for (int i = 0; i < swamp.getMovementFactor(); i++) {
            response = newDungeon.tick(Direction.UP);
            mercenary = getEntities(response, "mercenary").get(0);
            assertTrue(mercenary.getPosition().equals(stuckPosition));
        }
        
        // Check that mercenary should be next to player
        response = newDungeon.tick(Direction.UP);
        mercenary = getEntities(response, "mercenary").get(0);
        assertTrue(Position.isAdjacent(mercenary.getPosition(), Entity.getPlayer().getPosition()));
    }

    @Test
    @DisplayName("Test move smart around swamp")
    public void testSwampSmart() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_swampSmart", "c_assassinConfig");

        Position initialPosition = getEntities(response, "assassin").get(0).getPosition();
        // Check that the position of mercenary does not change for the duration of movement factor

        // Get the swamp entity
        Swamp swamp = Swamp.checkSwamp(initialPosition.translateBy(Direction.UP));
        assertTrue(swamp.getType().equals("swamp_tile"));
        // Set the movement factor so it takes longer through the swamp
        swamp.setMovementFactor(5);

        // Check that assassin doesn't go through swamp because there is another path to player
        // since the swamp tile would take up addition ticks
        response = newDungeon.tick(Direction.DOWN);
        EntityResponse assassin = getEntities(response, "assassin").get(0);
        assertEquals(initialPosition.translateBy(Direction.RIGHT), assassin.getPosition());
    }
}
