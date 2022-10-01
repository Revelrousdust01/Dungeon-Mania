package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestTimePortal {
    /*
     * - after 30 ticks everything go back
        - check after time travel, everything is where it is
        - less than 30 ticks timetravel, initial state dungeon
        - check after time travel, old player exist. After x ticks, old player goes in portal and disappears (same tick)
     */
    @Test
    @DisplayName("Test that time travel portal goes back 30 ticks dungeon state")
    public void testTimePortalGoBack30Ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("timePortalTest", "c_timePortal");

        // Go to time portal that is 40 units to right
        DungeonResponse res30TicksAgo = null;
        DungeonResponse resAfterTimeTravel = null;
        for (int i = 1; i <= 40; i++) {
            if (i == 10) { // Inclusive (tick that time travels is in the rewind)
                res30TicksAgo = dmc.tick(Direction.RIGHT);
            } else if (i == 40) {
                // Time travels on the 40th tick
                resAfterTimeTravel = dmc.tick(Direction.RIGHT);
            } else {
                dmc.tick(Direction.RIGHT);
            }
        }

        // Test that zombie and spider position after time travel is same as position from 30 ticks ago
        Position zombie30TicksAgo = getEntities(res30TicksAgo, "zombie_toast").get(0).getPosition();
        Position zombieAfterTimeTravel = getEntities(resAfterTimeTravel, "zombie_toast").get(0).getPosition();
        assertEquals(zombie30TicksAgo, zombieAfterTimeTravel);

        Position spider30TicksAgo = getEntities(res30TicksAgo, "spider").get(0).getPosition();
        Position spiderAfterTimeTravel = getEntities(resAfterTimeTravel, "spider").get(0).getPosition();
        assertEquals(spider30TicksAgo, spiderAfterTimeTravel);

        // Test that collected wood reappears after time travel, and the one that was collected remains in inventory
        assertEquals(1, countEntityOfType(resAfterTimeTravel, "wood"));
        assertEquals(1, getInventory(resAfterTimeTravel, "wood").size());
        
    }

    @Test
    @DisplayName("Test that after time travelling with portal and 30 ticks pass, the dungeon back to before time travelling state")
    public void testTimePortalAfter30Ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("timePortalTest", "c_timePortal");

        // Go to time portal that is 40 units to right
        DungeonResponse resTimeTravelOver = null;
        DungeonResponse resBeforeTimeTravel = null;
        for (int i = 1; i <= 70; i++) {
            if (i == 69) {
                resTimeTravelOver = dmc.tick(Direction.RIGHT);
            } else if (i == 39) {
                resBeforeTimeTravel = dmc.tick(Direction.RIGHT);
            } else {
                dmc.tick(Direction.RIGHT);
            }
        }

        // Test that spider is same position as one tick before time travelling and 30 ticks after time travel
        Position spiderAfter30Ticks = getEntities(resTimeTravelOver, "spider").get(0).getPosition();
        Position spiderBeforeTimeTravel = getEntities(resBeforeTimeTravel, "spider").get(0).getPosition();
        assertEquals(spiderBeforeTimeTravel, spiderAfter30Ticks);

        // Test that wood that reappeared after time travel, will disappear after 30 ticks
        // This assumes old player is working as expected
        assertEquals(0, countEntityOfType(resTimeTravelOver, "wood"));

    }

    @Test
    @DisplayName("Test time travelling with portal with less than 30 ticks passed go back to dungeon's initial state")
    public void testTimePortalLessThan30Ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse resInitial = dmc.newGame("timePortalTest", "c_timePortal");

        // Go to time portal that is 10 units to left
        DungeonResponse resAfterTimeTravel = null;
        for (int i = 1; i <= 10; i++) {
            if (i == 10) {
                resAfterTimeTravel = dmc.tick(Direction.LEFT);
            } else {
                dmc.tick(Direction.LEFT);
            }
        }

        // Test that zombie and spider position after time travel is same as position as initial dungeon
        Position zombieInitial = getEntities(resInitial, "zombie_toast").get(0).getPosition();
        Position zombieAfterTimeTravel = getEntities(resAfterTimeTravel, "zombie_toast").get(0).getPosition();
        assertEquals(zombieInitial, zombieAfterTimeTravel);

        Position spiderInitial = getEntities(resInitial, "spider").get(0).getPosition();
        Position spiderAfterTimeTravel = getEntities(resAfterTimeTravel, "spider").get(0).getPosition();
        assertEquals(spiderInitial, spiderAfterTimeTravel);
        
    }

    @Test
    @DisplayName("Test time travelling with portal spawns old player, and they go back to portal after 30 ticks")
    public void testTimePortalOldPlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("timePortalTest", "c_timePortal");

        // Go to time portal that is 40 units to right
        DungeonResponse res30TicksAgo = null;
        DungeonResponse resAfterTimeTravel = null;
        DungeonResponse resTimeTravelOver = null;
        for (int i = 1; i <= 71; i++) {
            if (i == 10) { // Inclusive (tick that time travels is in the rewind)
                res30TicksAgo = dmc.tick(Direction.RIGHT);
            } else if (i == 40) {
                // Time travels on the 40th tick
                resAfterTimeTravel = dmc.tick(Direction.RIGHT);
            } else if (i == 71) {
                resTimeTravelOver = dmc.tick(Direction.RIGHT);
            } else {
                dmc.tick(Direction.RIGHT);
            }
        }

        // Test that old player appears after time travel
        Position oldPlayerExpected = getEntities(res30TicksAgo, "player").get(0).getPosition();
        Position oldPlayerActual = getEntities(resAfterTimeTravel, "older_player").get(0).getPosition();
        assertEquals(oldPlayerExpected, oldPlayerActual);

        // Test that old player disappears after time travel is over
        assertEquals(0, countEntityOfType(resTimeTravelOver, "older_player"));
        
    }
}
