package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestPortals {

    @Test
    @DisplayName("Tests a successful teleport with no obstacles")
    public void testSuccessfulPlayerTeleport() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_teleportPlayerNoObstructions", "simpleConfig");

        // Move towards portal
        dmc.tick(Direction.UP);
        DungeonResponse res = dmc.tick(Direction.LEFT);
        Position pos = getEntities(res, "player").get(0).getPosition();

        // Teleport and check change of position
        res = dmc.tick(Direction.LEFT);
        Position posNew = getEntities(res, "player").get(0).getPosition();
        assertFalse(pos.getX() == posNew.getX() && pos.getY() == posNew.getY());
        assertFalse(-2 == posNew.getX() && -1 == posNew.getY());

        // Check new position within sorroundings of exit
        // Implement to exit out the opposite side (entered up -> exit down, etc)
        assertTrue(1 == posNew.getX() && 1 == posNew.getY());
    }

    @Test
    @DisplayName("Tests unsuccessful teleport when the exit portal is somewhat obstructed")
    public void testHalfBlocked() {
        // Will not teleport if the exit is blocked (to any extent)
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_teleportPlayerFewObstructions", "simpleConfig");

        // Move to portal
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();

        // attempt to go through portal (does not teleport)
        res = dmc.tick(Direction.DOWN);
        Position posNew = getEntities(res, "player").get(0).getPosition();
        assertFalse(pos.getX() == posNew.getX() && pos.getY() == posNew.getY());
        assertTrue(2 == posNew.getX() && 1 == posNew.getY());
    }

    @Test
    @DisplayName("Tests unsuccessful teleport when the exit portal is fully obstructed")
    public void testBlocked() {
        // Surrounded by walls/boulder/door
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_teleportPlayerAllObstructions", "simpleConfig");

        // Move to portal
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position pos = getEntities(res, "player").get(0).getPosition();

        // attempt to go through portal (does not teleport)
        res = dmc.tick(Direction.DOWN);
        Position posNew = getEntities(res, "player").get(0).getPosition();
        assertFalse(pos.getX() == posNew.getX() && pos.getY() == posNew.getY());
        assertTrue(2 == posNew.getX() && 1 == posNew.getY());
    }

    /*
     * @Test
     * 
     * @DisplayName("Tests that a mercenary can teleport")
     * public void testSuccessfulMercenaryTeleport() {
     * // Test when bribed (the mercenary should go throught the portal)
     * DungeonManiaController dmc = new DungeonManiaController();
     * dmc.newGame("d_testMercenaryTeleport", "simpleConfig");
     * 
     * // Bribe the mercenary
     * DungeonResponse res = dmc.tick(Direction.UP);
     * String mercId = getEntities(res, "mercenary").get(0).getId();
     * assertDoesNotThrow(() -> dmc.interact(mercId));
     * 
     * // Enter the portal
     * dmc.tick(Direction.LEFT);
     * res = dmc.tick(Direction.LEFT);
     * Position mercPos = getEntities(res, "mercenary").get(0).getPosition();
     * 
     * // Check merc goes through portal
     * res = dmc.tick(Direction.LEFT);
     * assertNotEquals(mercPos, getEntities(res, "mercenary").get(0).getPosition());
     * mercPos = getEntities(res, "mercenary").get(0).getPosition();
     * assertTrue(mercPos.getX() == 1 && mercPos.getY() == 1);
     * }
     */

    @Test
    @DisplayName("Tests that spiders are not effected by portals")
    public void testSpiderNotTeleported() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_testSpiderPortal", "simpleConfig");

        Position spiPos = getEntities(res, "spider").get(0).getPosition();

        // Spider will move up into portal
        res = dmc.tick(Direction.UP);
        assertNotEquals(spiPos, getEntities(res, "spider").get(0).getPosition());
        spiPos = getEntities(res, "spider").get(0).getPosition();
        assertTrue(spiPos.getX() == 2 && spiPos.getY() == 1);
    }

    @Test
    @DisplayName("Tests that zombies are not effected by portals")
    public void testZombieNotTeleported() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_testZombiePortal", "simpleConfig");

        Position zomPos = getEntities(res, "zombie_toast").get(0).getPosition();
        res = dmc.tick(Direction.UP);
        assertNotEquals(zomPos, getEntities(res, "zombie_toast").get(0).getPosition());

        zomPos = getEntities(res, "zombie_toast").get(0).getPosition();
        int zomX = zomPos.getX();
        int zomY = zomPos.getY();
        assertTrue(2 <= zomX && zomX <= 4 && 1 <= zomY && zomY <= 3);
    }

    @Test
    @DisplayName("Tests multiple unobstructed portals and their teleporting behaviour")
    public void testMultiplePortals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_teleportPlayerAdvanced", "simpleConfig");

        // Go through portal
        Position InitPos = getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.UP);
        assertNotEquals(InitPos, getEntities(res, "player").get(0).getPosition());
        Position pos = getEntities(res, "player").get(0).getPosition();

        // Check end location
        assertTrue(pos.getX() == 3 && pos.getY() == 0);

        // Go through two portals in one tick
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        pos = getEntities(res, "player").get(0).getPosition();
        assertTrue(pos.getX() == 4 && pos.getY() == 1);
    }
}
