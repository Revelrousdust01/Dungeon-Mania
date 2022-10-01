package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import dungeonmania.Entity.MovingEntity.*;
import dungeonmania.Entity.CollectibleEntity.Potion;

public class TestPersistence {
    @Test
    @DisplayName("Test Persistence Basic Entities on Map")
    public void TestPersistenceBasicEntitiesOnMap() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_persistenceBasic", "c_assassinBribePass");
        
        // Move the player down by 2 position
        res = newDungeon.tick(Direction.DOWN);
        res = newDungeon.tick(Direction.DOWN);

        // save the same and store all the position of each entity
        newDungeon.saveGame("Saved Game0");
        List<EntityResponse> expectedEntities = res.getEntities();

        // Move the player again
        res = newDungeon.tick(Direction.UP);
        res = newDungeon.tick(Direction.UP);

        // Load the game and check if all the entities and position matches
        res = newDungeon.loadGame("Saved Game0");
        List<EntityResponse> actCurrEntities = res.getEntities();

        assertTrue(expectedEntities.equals(actCurrEntities));

    }

    @Test
    @DisplayName("Test Persistence Basic BattleResponse")
    public void TestPersistenceBattleResponse() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_persistenceBasic", "c_assassinConfig");
        
        // Collect potions and treasure
        for (int i = 0; i < 5; i++) {
            res = newDungeon.tick(Direction.RIGHT);
        }

        // Check that player has sword
        assertEquals(1, getInventory(res, "sword").size());

        // Await for battle
        res = newDungeon.tick(Direction.DOWN);
        res = newDungeon.tick(Direction.DOWN);
        res = newDungeon.tick(Direction.DOWN);

        // Engage in battle
        res = newDungeon.tick(Direction.DOWN);

        // save the game
        newDungeon.saveGame("Saved Game1");

        // Load the game and check if the battleResponse matches
        DungeonResponse res1 = newDungeon.loadGame("Saved Game1");

        assertTrue(res1.getBattles().equals(res.getBattles()));

    }

    @Test
    @DisplayName("Test Persistence Basic Inventory")
    public void TestPersistenceBasicInventory() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_persistenceBasic", "c_assassinBribePass");
        
        // Move the player right by 2 position to collect items
        res = newDungeon.tick(Direction.RIGHT);
        res = newDungeon.tick(Direction.RIGHT);

        // Check if it collected the items
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // save the same and store all the position of each entity
        newDungeon.saveGame("Saved Game2");
        List<ItemResponse> expectedInventory = res.getInventory();

        // Use invincibility potion
        String potionId = getInventory(res, "invincibility_potion").get(0).getId();
        res = assertDoesNotThrow(() -> newDungeon.tick(potionId));

        // Load the game and check if all the entities and position matches
        res = newDungeon.loadGame("Saved Game2");
        List<ItemResponse> actInventory = res.getInventory();

        List<String> actId = actInventory.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<String> expId = expectedInventory.stream().map(e -> e.getId()).collect(Collectors.toList());
        assertEquals(expId, actId);

        List<String> actType = actInventory.stream().map(e -> e.getType()).collect(Collectors.toList());
        List<String> expType = expectedInventory.stream().map(e -> e.getType()).collect(Collectors.toList());
        assertEquals(expType, actType);

    }

    @Test
    @DisplayName("Test Persistence Advanced Bribed And Potion Duration")
    public void TestPersistenceBribedAndPotionDuration() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse res = newDungeon.newGame("d_persistenceBasic", "c_assassinBribePass");
        
        // Collect potions and treasure
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.RIGHT);
        }

        // Check if it collected the items
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(1, getInventory(res, "treasure").size());

        // Await for bribe range
        for (int i = 0; i < 4; i++) {
            res = newDungeon.tick(Direction.DOWN);
        }

        // Bribe Assassin, will pass
        EntityResponse assassin = getEntities(res, "assassin").get(0);
        String assassinId = assassin.getId();
        assertDoesNotThrow(() -> newDungeon.interact(assassinId));
        res = newDungeon.getDungeonResponseModel();

        // Check that assassin is bribed
        Boolean expectedAssassinBribed = getEntities(res, "assassin").get(0).isInteractable();
        assertEquals(false, expectedAssassinBribed);

        // Use invincibility potion
        String potionId = getInventory(res, "invincibility_potion").get(0).getId();
        res = assertDoesNotThrow(() -> newDungeon.tick(potionId));

        // Save the same
        newDungeon.saveGame("Saved Game3");

        // Trigger 3 ticks and check that mercenary is moving away during potion duration
        Position mercPosition = getEntities(res, "mercenary").get(0).getPosition();
        Double initialDist = Position.dist(Entity.getPlayer().getPosition(), mercPosition);
        Double currDist = 0.0;
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.DOWN);
            mercPosition = getEntities(res, "mercenary").get(0).getPosition();
            currDist = Position.dist(Entity.getPlayer().getPosition(), mercPosition);
            assertTrue(currDist > initialDist);
        }

        // Move the player again
        res = newDungeon.tick(Direction.DOWN);
        res = newDungeon.tick(Direction.RIGHT);

        // Load the game
        res = newDungeon.loadGame("Saved Game3");

        // Check that assassin remains bribed
        Boolean actAssassinBribed = getEntities(res, "assassin").get(0).isInteractable();
        assertEquals(false, actAssassinBribed);

        // Check that mercenary is moving away for three more turns due to potion
        initialDist = currDist;
        for (int i = 0; i < 3; i++) {
            res = newDungeon.tick(Direction.DOWN);
            mercPosition = getEntities(res, "mercenary").get(0).getPosition();

            currDist = Position.dist(Entity.getPlayer().getPosition(), mercPosition);
            // assertTrue(currDist > initialDist);

            Potion pot = Entity.getPlayer().getCurrPotion();
            String mercId = getEntities(res, "mercenary").get(0).getId();
            Mercenary merc = (Mercenary) Entity.findEntity(mercId);
            assertEquals("invincibility_potion", pot.getType());
            assertTrue(merc.getMovementState() instanceof MovementAway);
        }
        
        // Player moves again, but duration should be over, thus mercenary should be approaching player
        initialDist = currDist;
        res = newDungeon.tick(Direction.DOWN);

        Potion pot = Entity.getPlayer().getCurrPotion();
        String mercId = getEntities(res, "mercenary").get(0).getId();
        Mercenary merc = (Mercenary) Entity.findEntity(mercId);
        assertEquals(null, pot);
        assertTrue(merc.getMovementState() instanceof MovementTowards);

        // assertTrue(currDist < initialDist);
        
    }
}
