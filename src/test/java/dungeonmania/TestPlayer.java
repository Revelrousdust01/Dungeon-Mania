package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestPlayer {
    @Test
    @DisplayName("Test player movement in all directions")
    public void testPlayerMovementDirections() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("playerTest_movementWall", "simpleConfig");

        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // Testing movement down
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 1), false);
        DungeonResponse actualDungonRes = dmc.tick(Direction.DOWN);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);

        // Testing movement left
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(-1, 1), false);
        actualDungonRes = dmc.tick(Direction.LEFT);
        actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);

        // Testing movement up
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(-1, 0), false);
        actualDungonRes = dmc.tick(Direction.UP);
        actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);

        // Testing movement right
        expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(0, 0), false);
        actualDungonRes = dmc.tick(Direction.RIGHT);
        actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);
        
    }

    @Test
    @DisplayName("Test player movement into wall")
    public void testPlayerMovementWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("playerTest_movementWall", "simpleConfig");

        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // Wall is two units to right
        // Test player stops before the wall
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 0), false);
        dmc.tick(Direction.RIGHT);
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        EntityResponse actualPlayer = getPlayer(actualDungonRes).get();
        assertEquals(expectedPlayer, actualPlayer);
        
    }

    @Test
    @DisplayName("Test player push boulder")
    public void testPlayerMovementPushBoulder() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("playerTest_movementBoulder", "simpleConfig");

        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // Test that the boulder moves when pushed
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(4, 0), false);
        Position expectedBoulder = new Position(3, 0);

        DungeonResponse res = dmc.tick(Direction.LEFT);
        Position actualBoulder = getEntities(res, "boulder").get(0).getPosition();
        EntityResponse actualPlayer = getPlayer(res).get();
        assertEquals(expectedPlayer, actualPlayer);
        assertEquals(expectedBoulder, actualBoulder);
        
    }

    @Test
    @DisplayName("Test player push boulder into wall")
    public void testPlayerMovementPushBoulderWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("playerTest_movementBoulder", "simpleConfig");

        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // Test that the boulder doesn't move when pushed into a wall
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(5, 0), false);
        Position expectedBoulder = new Position(6, 0);

        DungeonResponse res = dmc.tick(Direction.RIGHT);
        Position actualBoulder = getEntities(res, "boulder").get(1).getPosition();
        EntityResponse actualPlayer = getPlayer(res).get();
        assertEquals(expectedPlayer, actualPlayer);
        assertEquals(expectedBoulder, actualBoulder);
        
    }

    @Test
    @DisplayName("Test player push boulder off switch")
    public void testPlayerPushOffSwitch() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("playerTest_movementPushOffSwitch", "simpleConfig");

        EntityResponse initPlayer = getPlayer(initDungonRes).get();

        // Test that the boulder can be pushed off the switch
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 0), false);
        Position expectedBoulder = new Position(3, 0);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        Position actualBoulder = getEntities(res, "boulder").get(0).getPosition();
        EntityResponse actualPlayer = getPlayer(res).get();
        assertEquals(expectedPlayer, actualPlayer);
        assertEquals(expectedBoulder, actualBoulder);
        
    }
    
}


