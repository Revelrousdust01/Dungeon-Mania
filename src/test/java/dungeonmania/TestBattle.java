package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static dungeonmania.TestUtils.*;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Battle.Battle;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.BuildableEntity.Bow;
import dungeonmania.Entity.BuildableEntity.MidnightArmour;
import dungeonmania.Entity.BuildableEntity.Sceptre;
import dungeonmania.Entity.BuildableEntity.Shield;
import dungeonmania.Entity.CollectibleEntity.InvincibilityPotion;
import dungeonmania.Entity.CollectibleEntity.InvisibilityPotion;
import dungeonmania.Entity.CollectibleEntity.Sword;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestBattle {
    @Test
    @DisplayName("Test Battle without weapon")
    public void testBattleWithoutWeapon() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "zombie_toast");
        assertEquals(0, entities.size());
    }

    @Test
    @DisplayName("Test battle player dies")
    public void testPlayerDies() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "c_playerDies");
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "player");
        assertEquals(0, entities.size());
    }

    @Test
    @DisplayName("Test Battle against ZombieToast with Sword")
    public void testBattleZombieToastSword() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "simpleConfig");
        Sword sword = new Sword(true, new Position(1, 1), "10", "sword");
        Entity.getPlayer().addToInventory(sword);
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "zombie_toast");
        assertEquals(0, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test Battle against Spider with Bow")
    public void testBattleSpiderBow() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_battleSpiderBow", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "spider");
        Bow bow = new Bow();
        Entity.getPlayer().addToInventory(bow);
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        List<RoundResponse> rounds = response.getBattles().get(0).getRounds();
        // playeratk = 1, zombieatk = .5
        RoundResponse firstRound = rounds.get(1);
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
        entities = getEntities(response, "spider");
        assertEquals(0, entities.size());
    }

    @Test
    @DisplayName("Test battle without midnight armour")
    public void testBattleNoMidnightArmour() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryBattle", "c_midnightArmour");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());

        response = newDungeon.tick(Direction.RIGHT);
        // player will get killed without midnight armour
        entities = getEntities(response, "player");
        assertEquals(0, entities.size());
        entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test battle with midnight armour")
    public void testBattleMidnightArmour() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryBattle", "c_midnightArmour");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());

        MidnightArmour armour = new MidnightArmour();
        Entity.getPlayer().addToInventory(armour);
        
        response = newDungeon.tick(Direction.RIGHT);
        // player will get killed without midnight armour so player wins with it
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
        entities = getEntities(response, "mercenary");
        assertEquals(0, entities.size());
    }

    @Test
    @DisplayName("Test Battle against Mercenary with Sword and Shield")
    public void testBattleMercenarySwordAndShield() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_battleMercenarySwordAndShield", "simpleConfig");
        List<EntityResponse> entities = getEntities(response, "mercenary");
        Shield shield = new Shield();
        Entity.getPlayer().addToInventory(shield);
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "mercenary");
        assertEquals(0, entities.size());
    }

    @Test
    @DisplayName("Test Battle with Invincibility Potion")
    public void testBattleInvincibilityPotion() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_battleInvincibilityPotion", "c_battleInvincibilityPotion");

        InvincibilityPotion potion = new InvincibilityPotion(true, new Position(0, 0), "10", "invincibility_potion");
        Entity.getPlayer().addToInventory(potion);

        try {
            Entity.getPlayer().use("10");
        } catch (Exception e) {
            System.out.println("caught exception: " + e);
        }

        List<EntityResponse> entities = getEntities(response, "spider");
        assertEquals(1, entities.size());
        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "spider");
        assertEquals(0, entities.size());

        // Check that battle ended in one round and player health didn't decrease
        BattleResponse b = response.getBattles().get(0);
        assertEquals(1, b.getRounds().size());
        assertEquals(0, b.getRounds().get(0).getDeltaCharacterHealth());

    }

    @Test
    @DisplayName("Test no battle while invisible")
    public void testNoBattleWhileInvisible() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "c_playerDies");
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());
        
        InvisibilityPotion potion = new InvisibilityPotion(true, new Position(0, 0), "10", "invisibility_potion");
        Player player = Player.getPlayer();
        player.addToInventory(potion);

        try {
            player.use("10");
        } catch (Exception e) {
            System.out.println("caught exception: " + e);
        }

        response = newDungeon.tick(Direction.RIGHT);
        // make sure both are still alive due to invisibility status effect
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
        entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());
    }
    @Test
    @DisplayName("Test battle with Ally")
    public void testBattleWithAlly() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "c_playerDies");
        new Mercenary(false, new Position(2, -1), "11", "mercenary");

        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());

        response = newDungeon.tick(Direction.RIGHT);
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test battle details")
    public void testBattleDetails() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_simpleBattle", "simpleConfig");
        response = newDungeon.tick(Direction.RIGHT);
        List<RoundResponse> rounds = response.getBattles().get(0).getRounds();
        // playeratk = 1, zombieatk = .5
        RoundResponse firstRound = rounds.get(0);
        assertEquals(-1.0, firstRound.getDeltaEnemyHealth());
        assertEquals(-0.5, firstRound.getDeltaCharacterHealth());
    }

    @Test
    @DisplayName("Test mind control stops battle")
    public void testMindControlStopsBattle() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryBattle", "c_sceptreMedium");

        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());

        Sceptre sceptre = new Sceptre();
        Entity.getPlayer().addToInventory(sceptre);
        assertEquals("sceptre", Entity.getPlayer().getInventory().get(0).getType());
        String sceptreId = Entity.getPlayer().getInventory().get(0).getId();

        assertDoesNotThrow(() -> newDungeon.tick(sceptreId));
        response = newDungeon.tick(Direction.RIGHT);
        response = newDungeon.tick(Direction.RIGHT);
        // player will not battle since the mercenary is an ally
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
        entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test mind control works on multiple mercenaries")
    public void testMindControlWorksMultipleMercenaries() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_multipleMercenaries", "c_sceptreMedium");

        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(3, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());
        
        Sceptre sceptre = new Sceptre();
        Entity.getPlayer().addToInventory(sceptre);
        assertEquals("sceptre", Entity.getPlayer().getInventory().get(0).getType());
        String sceptreId = Entity.getPlayer().getInventory().get(0).getId();

        assertDoesNotThrow(() -> newDungeon.tick(sceptreId));
        response = newDungeon.tick(Direction.LEFT);
        // player will not battle since the mercenary is an ally
        for(Mercenary merc : Entity.getAllEntities()
            .stream()
            .filter(e -> e.getType().equals("mercenary"))
            .map(e -> (Mercenary) e)
            .collect(Collectors.toList())) {
                assertTrue(!merc.isInteractable());
            }
    }
    
    @Test
    @DisplayName("Test mind control runs out")
    public void testMindControlRunsOut() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_mercenaryBattle", "c_sceptreShort");

        List<EntityResponse> entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
        entities = getEntities(response, "player");
        assertEquals(1, entities.size());

        Sceptre sceptre = new Sceptre();
        Entity.getPlayer().addToInventory(sceptre);
        assertEquals("sceptre", Entity.getPlayer().getInventory().get(0).getType());
        String sceptreId = Entity.getPlayer().getInventory().get(0).getId();

        assertDoesNotThrow(() -> newDungeon.tick(sceptreId));
        response = newDungeon.tick(Direction.LEFT);
        response = newDungeon.tick(Direction.LEFT);
        // mind control runs out and player dies
        entities = getEntities(response, "player");
        assertEquals(0, entities.size());
        entities = getEntities(response, "mercenary");
        assertEquals(1, entities.size());
    }

    @Test
    @DisplayName("Test hydra health will increase according to health increase amount and rate")
    public void testBattleHydraRegrowHead() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("battleTest_hydra", "c_battleHydra");
        Battle.setHydraSeed(3); 
        // Bound 100 sequence is: 34, 60, 10, 81, 28
        // Health increase in first, third, fifth
        
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        List<RoundResponse> rounds = res.getBattles().get(0).getRounds();

        assertEquals(5, rounds.get(0).getDeltaEnemyHealth());
        assertTrue(rounds.get(1).getDeltaEnemyHealth() < 0);
        assertEquals(5, rounds.get(2).getDeltaEnemyHealth());
        assertTrue(rounds.get(3).getDeltaEnemyHealth() < 0);
        assertEquals(5, rounds.get(4).getDeltaEnemyHealth());
        
    }
}
