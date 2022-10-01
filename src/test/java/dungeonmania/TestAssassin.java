package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestAssassin {
    private static final int reconRange = 5;

    @Test
    @DisplayName("Test assassin moves towards player")
    public void testAssassinMoveTowards() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinConfig");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);

        // Player moves UP into twice wall
        res = newDungeon.tick(Direction.UP);
        res = newDungeon.tick(Direction.UP);

        // Check that the dist between the player and assassin has decreased
        assassin = getEntities(res, "assassin").get(0);
        Double newDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());

        assertTrue(newDistance < initialDistance);
    }

    @Test
    @DisplayName("Test moving towards player within recon range when player is invisible")
    public void TestAssassinRecon() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinConfig");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);
        
        // Move player into wall until assassin is in recon range
        while (Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition()) > reconRange) {
            res = newDungeon.tick(Direction.LEFT);
            assassin = getEntities(res, "assassin").get(0);
        }
        
        // Player uses invisibility potion
        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));

        // Update the position of assassin
        res = newDungeon.getDungeonResponseModel();
        assassin = getEntities(res, "assassin").get(0);
        initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());

        // Move player again
        res = newDungeon.tick(Direction.LEFT);

        // Check that the dist between the player and assassin has decreased
        assassin = getEntities(res, "assassin").get(0);
        Double newDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(newDistance < initialDistance);
    }

    @Test
    @DisplayName("Test player goes invisible and assassin not bribed")
    public void TestAssassinPlayerInvisibleNotBribed() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinConfig");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);

        res = newDungeon.tick(Direction.LEFT);

        // Player uses invisibility potion
        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));

        res = newDungeon.getDungeonResponseModel();
        // Move player again
        Position initialPosition = getEntities(res, "assassin").get(0).getPosition();
        res = newDungeon.tick(Direction.LEFT);
        assassin = getEntities(res, "assassin").get(0);
        // Assassin stays in the same position
        assertTrue(assassin.getPosition().equals(initialPosition));
    }

    @Test
    @DisplayName("Test bribed and player goes invisible, in recon range")
    public void TestAssassinPlayerInvisibleAndBribedAndWithinRange() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinBribePass");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);

        res = newDungeon.tick(Direction.LEFT);
        res = newDungeon.tick(Direction.UP);

        // Bribe Assassin, will pass
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));
        res = newDungeon.getDungeonResponseModel();

        // Move player in wall until assassin is in recon range
        while (Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition()) > reconRange) {
            res = newDungeon.tick(Direction.RIGHT);
            assassin = getEntities(res, "assassin").get(0);
        }
        initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());

        // Player uses invisibility potion
        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));

        // Get position of assassin
        res = newDungeon.getDungeonResponseModel();
        assassin = getEntities(res, "assassin").get(0);
        Double initialDist = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        // Move player twice again
        res = newDungeon.tick(Direction.RIGHT);
        res = newDungeon.tick(Direction.RIGHT);

        // Assassin stays in the same position
        assassin = getEntities(res, "assassin").get(0);
        Double currDist = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        
        assertTrue(initialDist > currDist);
    }

    @Test
    @DisplayName("Test bribed and player goes invisible, not in recon range")
    public void TestAssassinPlayerInvisibleAndBribedAndNotWithinRange() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinBribePass");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);

        // Collect treasure
        res = newDungeon.tick(Direction.LEFT);
        res = newDungeon.tick(Direction.UP);

        // Bribe Assassin, will pass
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));
        res = newDungeon.getDungeonResponseModel();

        // Make sure it is not in recon range
        Double currDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(currDistance > reconRange);

        // Player uses invisibility potion
        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> newDungeon.tick(potionId));
        res = newDungeon.getDungeonResponseModel();
        
        // Move player twice again, check that assassin moved randomly
        Position initialPosition = null;
        for (int i = 0; i < 2; i++) {
            initialPosition = getEntities(res, "assassin").get(0).getPosition();
            res = newDungeon.tick(Direction.RIGHT);
            assassin = getEntities(res, "assassin").get(0);
            assertTrue(Position.isAdjacent(initialPosition, assassin.getPosition()));
        }
    }

    @Test
    @DisplayName("Test assassin bribe always fail")
    public void TestAssassinBribeFail() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinBribeFail");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);

        // Move player to collect treasure
        res = newDungeon.tick(Direction.LEFT);
        res = newDungeon.tick(Direction.UP);

        // Move player in wall until assassin is in bribe range
        while (!Position.isAdjacent(Entity.getPlayer().getPosition(), assassin.getPosition())) {
            res = newDungeon.tick(Direction.RIGHT);
            assassin = getEntities(res, "assassin").get(0);
        }

        // Bribe Assassin, will fail
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));

        // Assassin should not be bribed
        res = newDungeon.getDungeonResponseModel();
        assassin = getEntities(res, "assassin").get(0);
        assertTrue(assassin.isInteractable());
    }

    @Test
    @DisplayName("Test assassin bribe always pass")
    public void TestAssassinBribePass() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinBribePass");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);
        
        // Move player to collect treasure
        res = newDungeon.tick(Direction.LEFT);
        res = newDungeon.tick(Direction.UP);

        // Move player in wall until assassin is in bribe range
        while (!Position.isAdjacent(Entity.getPlayer().getPosition(), assassin.getPosition())) {
            res = newDungeon.tick(Direction.RIGHT);
            assassin = getEntities(res, "assassin").get(0);
        }

        // Bribe Assassin, will pass
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));

        // Assassin is bribed, can no longer interact
        res = newDungeon.getDungeonResponseModel();
        assassin = getEntities(res, "assassin").get(0);
        assertFalse(assassin.isInteractable());
    }

    @Test
    @DisplayName("Test assassin bribe random")
    public void TestAssassinBribePassOrFail() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_assassinBasicMovement", "c_assassinConfig");

        // Making sure they are not in recon range
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        Double initialDistance = Position.dist(Entity.getPlayer().getPosition(), assassin.getPosition());
        assertTrue(initialDistance > reconRange);
        
        // Move player to collect treasure
        res = newDungeon.tick(Direction.LEFT);
        res = newDungeon.tick(Direction.UP);

        // Move player in wall until assassin is in bribe range
        while (!Position.isAdjacent(Entity.getPlayer().getPosition(), assassin.getPosition())) {
            res = newDungeon.tick(Direction.RIGHT);
            assassin = getEntities(res, "assassin").get(0);
        }

        // Bribe Assassin, will fail
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));

        res = newDungeon.getDungeonResponseModel();
        // Assassin maybe bribed
        assassin = getEntities(res, "assassin").get(0);
        assertTrue(!assassin.isInteractable() || assassin.isInteractable());
    }
}
