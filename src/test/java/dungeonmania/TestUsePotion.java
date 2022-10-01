package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.response.models.*;
import dungeonmania.util.*;
import dungeonmania.Entity.MovingEntity.*;

public class TestUsePotion {
    @Test
    @DisplayName("Test invisibility potion use")
    public void testInvisibilityPotionUse() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("usePotionTest", "simpleConfig");
        
        // Check that test invisibility potion is collected
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res1, "invisibility_potion").size());
        assertEquals(0, countEntityOfType(res1, "invisibility_potion"));

        // Test that the invisibility potion is used successfully
        String potionId = getInventory(res1, "invisibility_potion").get(0).getId();
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.tick(potionId));
        assertEquals(0, getInventory(res2, "invisibility_potion").size());
    }

    @Test
    @DisplayName("Test invincibility potion use")
    public void testInvincibilityPotionUse() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("usePotionTest", "simpleConfig");
        
        // Check that test invincibility potion is collected
        DungeonResponse res1 = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res1, "invincibility_potion").size());
        assertEquals(0, countEntityOfType(res1, "invincibility_potion"));

        // Test that the invincibility potion is used successfully
        String potionId = getInventory(res1, "invincibility_potion").get(0).getId();
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.tick(potionId));
        assertEquals(0, getInventory(res2, "invincibility_potion").size());
        
    }

    @Test
    @DisplayName("Test potion 1 duration")
    public void testPotion1Duration() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("usePotionTest", "simpleConfig");

        // Duration is 1 tick
        // Test that the potion queue is empty on the same tick invis potion is used
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);
        String potionId = getInventory(res1, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));
        assertEquals(null, Entity.getPlayer().getCurrPotion());

        // Test that the potion queue is empty on the same tick invinc potion is used
        dmc.tick(Direction.LEFT);
        res1 = dmc.tick(Direction.LEFT);
        String potionId2 = getInventory(res1, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId2));
        assertEquals(null, Entity.getPlayer().getCurrPotion());

    }

    @Test
    @DisplayName("Test potion 5 duration")
    public void testPotion5Duration() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("usePotionTest", "c_usePotionTest_duration");

        // Duration is 5 ticks
        // Test that the potion queue is empty after 5 ticks
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);
        String potionId = getInventory(res1, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));
        
        int duration = 1;
        while (Entity.getPlayer().getCurrPotion() != null) {
            dmc.tick(Direction.RIGHT);
            duration++;
        }
        assertEquals(5, duration);

    }

    @Test
    @DisplayName("Test potion queueing")
    public void testPotionQueue() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("usePotionTest", "c_usePotionTest_duration");

        // Duration is 5 ticks
        // Test that the potion queue is invincibility potion after 5 ticks
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        DungeonResponse res1 = dmc.tick(Direction.LEFT);
        String potionId1 = getInventory(res1, "invisibility_potion").get(0).getId();
        String potionId2 = getInventory(res1, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId1));
        assertDoesNotThrow(() -> dmc.tick(potionId2));

        // 2 ticks have already passed due to consuming two potions
        int duration = 2;
        while (Entity.getPlayer().getCurrPotion() != null) {
            dmc.tick(Direction.RIGHT);
            duration++;

            // On the 5th tick, invis potion wears off and invinc potion takes effect
            if (duration == 5) {
                assertEquals("invincibility_potion", Entity.getPlayer().getCurrPotion().getType());
            }
        }
        assertEquals(10, duration);
        
    }

    @Test
    @DisplayName("Test invisibility potion duration + mercenary reaction")
    public void testPotionInvisibilityDurationMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("usePotionTest", "c_usePotionTest_duration");

        String mercId = getEntities(res, "mercenary").get(0).getId();
        Mercenary merc = (Mercenary) Entity.findEntity(mercId);

        // Duration is 5 ticks

        // Check that mercenary is on their default movement - towards
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);
        assertTrue(merc.getMovementState() instanceof MovementTowards);

        // Check that mercenary changes to random when using invis potion
        String potionId = getInventory(res1, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));
        assertTrue(merc.getMovementState() instanceof MovementRandom);

        // Test that mercenary changes back to towards after duration is over
        while (Entity.getPlayer().getCurrPotion() != null) {
            dmc.tick(Direction.RIGHT);
        }
        assertTrue(merc.getMovementState() instanceof MovementTowards);
        
    }

    @Test
    @DisplayName("Test invincibility potion duration + zombie reaction")
    public void testPotionInvincibilityDurationZombie() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("usePotionTest", "c_usePotionTest_duration");

        String zombieId = getEntities(res, "zombie_toast").get(0).getId();
        MobEntity zombie = (MobEntity) Entity.findEntity(zombieId);

        // Duration is 5 ticks

        // Check that zombie is on their default movement - random
        DungeonResponse res1 = dmc.tick(Direction.LEFT);
        assertTrue(zombie.getMovementState() instanceof MovementRandom);

        // Check that zombie changes to away when using invinc potion
        String potionId = getInventory(res1, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));
        assertTrue(zombie.getMovementState() instanceof MovementAway);

        // Test that zombie changes back to random after duration is over
        while (Entity.getPlayer().getCurrPotion() != null) {
            dmc.tick(Direction.RIGHT);
        }
        assertTrue(zombie.getMovementState() instanceof MovementRandom);
        
    }

    @Test
    @DisplayName("Test invincibility potion duration + mercenary reaction")
    public void testPotionInvincibilityDurationMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("usePotionTest", "c_usePotionTest_duration");

        String mercId = getEntities(res, "mercenary").get(0).getId();
        Mercenary merc = (Mercenary) Entity.findEntity(mercId);

        // Duration is 5 ticks

        // Check that mercenary is on their default movement - towards
        DungeonResponse res1 = dmc.tick(Direction.LEFT);
        assertTrue(merc.getMovementState() instanceof MovementTowards);

        // Check that mercenary changes to away when using invinc potion
        String potionId = getInventory(res1, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));
        assertTrue(merc.getMovementState() instanceof MovementAway);

        // Test that mercenary changes back to towards after duration is over
        while (Entity.getPlayer().getCurrPotion() != null) {
            dmc.tick(Direction.RIGHT);
        }
        assertTrue(merc.getMovementState() instanceof MovementTowards);
        
    }
}
