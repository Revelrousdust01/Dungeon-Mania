package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class TestTimeTravel {
    
    @Test
    public void testSimpleTimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingSimple", "milestone3SimpleConfig");

        // move around for some ticks
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.RIGHT);
        }
        Position playerPos = getEntities(res, "player").get(0).getPosition();
        assertTrue(Position.isSameCell(playerPos, new Position(10, 0)));
        assertEquals(1, getInventory(res, "time_turner").size());
        
        // time travel (using time turner)
        res = dmc.rewind(5);
        Position newPlayerPos = getEntities(res, "player").get(0).getPosition();
        assertTrue(Position.isSameCell(playerPos, newPlayerPos));

        // check that older self is present
        assertEquals(1, getEntities(res, "older_player").size());
        Position oldPlayerPosition = getEntities(res, "older_player").get(0).getPosition();
        assertTrue(Position.isSameCell(oldPlayerPosition, new Position(5, 0)));

        // check the position and path of the older self
        res = dmc.tick(Direction.DOWN);
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(6,0)));
        res = dmc.tick(Direction.DOWN);
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(7,0)));
        res = dmc.tick(Direction.RIGHT);
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(8,0)));
        res = dmc.tick(Direction.UP);
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(9,0)));
        res = dmc.tick(Direction.LEFT);
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(10,0)));

        // check old player despawns
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    public void testIllegalTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingSimple", "milestone3SimpleConfig");

        for (int i = 0; i < 32; i++) {
            dmc.tick(Direction.RIGHT);
        }

        res = dmc.rewind(3);
        assertEquals(0, getEntities(res, "older_player").size());
        res = dmc.rewind(8);
        assertEquals(0, getEntities(res, "older_player").size());
        res = dmc.rewind(25);
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    public void testOldPlayerCollection_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingCollection", "milestone3SimpleConfig");

        // collect some items
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getInventory(res, "time_turner").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        assertEquals(0, getEntities(res, "time_turner").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "sword").size());
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        
        // time travel
        dmc.tick(Direction.DOWN);
        res = dmc.rewind(5);
        
        // check items are back
        assertEquals(1, getInventory(res, "time_turner").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        assertEquals(1, getEntities(res, "time_turner").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "sword").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());
        
        // check the old player picks up these items
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "time_turner").size());
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "treasure").size());
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "sword").size());
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "invisibility_potion").size());
    }

    @Test
    public void testNewPlayerCollection_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingCollection", "milestone3SimpleConfig");

        // collect some items
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getInventory(res, "time_turner").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        assertEquals(0, getEntities(res, "time_turner").size());
        assertEquals(0, getEntities(res, "treasure").size());
        assertEquals(0, getEntities(res, "sword").size());
        assertEquals(0, getEntities(res, "invisibility_potion").size());
        
        // time travel
        dmc.tick(Direction.DOWN);
        res = dmc.rewind(5);

        // check items are back
        assertEquals(1, getInventory(res, "time_turner").size());
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        assertEquals(1, getEntities(res, "time_turner").size());
        assertEquals(1, getEntities(res, "treasure").size());
        assertEquals(1, getEntities(res, "sword").size());
        assertEquals(1, getEntities(res, "invisibility_potion").size());

        // check that these items can be collected
        res = dmc.tick(Direction.UP);
        assertEquals(2, getInventory(res, "invisibility_potion").size());
        res = dmc.tick(Direction.UP);
        assertEquals(2, getInventory(res, "sword").size());
    }

    @Test
    public void testMovingEntities_TimeTravel() {
        // NOTE undefined behaviour
        // spawn moving entities
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingMovingEntities", "c_timeTravelBattles");

        // battle some enemies
        for (int i = 0; i < 4; i++) {
            dmc.tick(Direction.RIGHT);
        }
        res = dmc.tick(Direction.DOWN); // kill spider
        assertEquals(0, getEntities(res, "spider").size());

        // time travel
        res = dmc.rewind(5);

        // check enemies are there
        assertEquals(1, getEntities(res, "spider").size());
        assertEquals(1, getEntities(res, "mercenary").size());
        assertEquals(1, getEntities(res, "zombie_toast").size());
        
        assertTrue(Position.isSameCell(getEntities(res, "spider").get(0).getPosition(), new Position(4, 0)));
        assertTrue(Position.isSameCell(getEntities(res, "mercenary").get(0).getPosition(), new Position(0, -4)));
        assertTrue(Position.isSameCell(getEntities(res, "zombie_toast").get(0).getPosition(), new Position(-3, -3)));
        
        // battle spider again
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertEquals(0, getEntities(res, "spider").size());
        assertEquals(1, getEntities(res, "mercenary").size());
        assertEquals(1, getEntities(res, "zombie_toast").size());
    }

    @Test
    public void testOldPlayerBribe_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingBribe", "c_timeTravelBattles");

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        
        // bribe mercenary
        assertEquals(1, getEntities(res, "mercenary").size());
        String mercId = getEntities(res, "mercenary").get(0).getId();
        assertDoesNotThrow(() -> dmc.interact(mercId));
        
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);

        // time travel
        res = dmc.rewind(5);
        assertEquals(1, getEntities(res, "mercenary").size());
        assertTrue(Position.isSameCell(getEntities(res, "mercenary").get(0).getPosition(), new Position(2, -3)));
        
        // check that old player bribes mercenary
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        
        // check that merc pos is the older players' last position
        Position olderPlayerPos = getEntities(res, "older_player").get(0).getPosition();
        Position mercPos = getEntities(res, "mercenary").get(0).getPosition();
        assertTrue(Position.isSameCell(olderPlayerPos, mercPos));
    }

    @Test
    public void testOldPlayerZombieSpawnerDestruction_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingSpawnerDestruction", "milestone3SimpleConfig");

        // destroy spawner
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());
        String spawnerId = getEntities(res, "zombie_toast_spawner").get(0).getId();
        assertDoesNotThrow(() -> dmc.interact(spawnerId));
        
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getEntities(res, "zombie_toast_spawner").size());
        
        // time travel
        res = dmc.rewind(5);
        assertEquals(1, getEntities(res, "zombie_toast_spawner").size());
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(0, 0)));
        
        // check that old player destroys spawner
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getEntities(res, "zombie_toast_spawner").size());
    }

    @Test
    public void testOldPlayerBombPlacement_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingBombUse", "milestone3SimpleConfig");
        assertEquals(2, getEntities(res, "wall").size());


        // do switch
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.UP);
        
        // collect bomb
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN);

        //
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        
        // place and activate bomb
        String bombId = getInventory(res, "bomb").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(bombId));
        res = dmc.getDungeonResponseModel();
        
        // check walls are broken
        res = dmc.tick(Direction.LEFT);
        assertEquals(0, getEntities(res, "wall").size());
        
        // time travel
        res = dmc.rewind(5);
        assertEquals(2, getEntities(res, "wall").size());
        
        // check that old player uses their bomb
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(0, getEntities(res, "wall").size());
    }

    @Test
    public void testOldPlayerBattleEncounter_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingBattles", "milestone3SimpleConfig");

        // move
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);

        // time travel
        res = dmc.rewind(5);
        assertEquals(1, getEntities(res, "older_player").size());
        assertTrue(Position.isSameCell(getEntities(res, "older_player").get(0).getPosition(), new Position(0, 0)));
        
        // battle older player
        dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(0, getEntities(res, "older_player").size());
    }

    @Test
    public void testOldPlayerSunStoneEncounter_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingSunStoneEncounter", "milestone3SimpleConfig");

        // collect sunstone
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertEquals(1, getInventory(res, "sun_stone").size());

        // time travel
        dmc.rewind(5);

        // encounter older player (should not battle)
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(1, getEntities(res, "player").size());
    }
    @Test
    public void testOldPlayerMidnightArmourEncounter_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingMidnightArmourEncounter", "milestone3SimpleConfig");

        // collect ingredients
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // craft armour
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "midnight_armour").size());

        // time travel
        dmc.rewind(5);

        // encounter older player (should not battle)
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(1, getEntities(res, "player").size());
    }
    @Test
    public void testOldPlayerInvisibleEncounter_TimeTravel() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTravellingInvisibilityPotionEncounter", "c_timeTravelBattles");

        // collect invisibility potion
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(1, getInventory(res, "invisibility_potion").size());

        // use potion
        String potionId = getInventory(res, "invisibility_potion").get(0).getId();
        assertDoesNotThrow(() -> dmc.tick(potionId));

        // time travel
        res = dmc.rewind(5);

        // encounter older player (should not battle)
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, getEntities(res, "older_player").size());
        assertEquals(1, getEntities(res, "player").size());
    }
}
