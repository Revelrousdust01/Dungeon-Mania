package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestNewGame {
    @Test
    @DisplayName("Test invalid dungeon json")
    public void testInvalidDungeonJson() {
        DungeonManiaController dmc1 = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc1.newGame("invalid", "c_movementTest_testMovementDown"));

    }

    @Test
    @DisplayName("Test invalid config json")
    public void testInvalidConfigJson() {
        DungeonManiaController dmc1 = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc1.newGame("newGameTest_createStaticEntities", "invalid"));

    }

    @Test
    @DisplayName("Test static entities are created successfully")
    public void testCreateStaticEntities() {
        DungeonManiaController dmc1 = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc1.newGame("newGameTest_createStaticEntities",
                "c_movementTest_testMovementDown");

        ArrayList<EntityResponse> entities = new ArrayList<EntityResponse>();
        entities.add(new EntityResponse("0", "wall", new Position(1, 0), false));
        entities.add(new EntityResponse("1", "zombie_toast_spawner", new Position(2, 0), true));
        entities.add(new EntityResponse("2", "exit", new Position(0, 1), false));
        entities.add(new EntityResponse("3", "portal", new Position(0, 2), false));
        entities.add(new EntityResponse("4", "player", new Position(10, 0), false));

        List<EntityResponse> eResList = initDungonRes.getEntities();
        assertEquals(eResList.containsAll(entities), entities.containsAll(eResList));
    }

    @Test
    @DisplayName("Test collectible entity is created successfully")
    public void testCreateCollectibleEntity() {
        DungeonManiaController dmc2 = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc2.newGame("newGameTest_createCollectibleEntities",
                "c_movementTest_testMovementDown");

        ArrayList<EntityResponse> entities = new ArrayList<EntityResponse>();
        entities.add(new EntityResponse("0", "treasure", new Position(1, 0), false));
        entities.add(new EntityResponse("1", "key", new Position(2, 0), false));
        entities.add(new EntityResponse("2", "player", new Position(10, 0), false));

        List<EntityResponse> eResList = initDungonRes.getEntities();
        assertEquals(eResList.containsAll(entities), entities.containsAll(eResList));
    }

    @Test
    @DisplayName("Test moving entities is created successfully")
    public void testCreateMovingEntities() {
        DungeonManiaController dmc3 = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc3.newGame("newGameTest_createMovingEntities",
                "c_movementTest_testMovementDown");

        ArrayList<EntityResponse> entities = new ArrayList<EntityResponse>();
        entities.add(new EntityResponse("0", "player", new Position(1, 0), false));
        entities.add(new EntityResponse("1", "mercenary", new Position(2, 0), true));
        entities.add(new EntityResponse("3", "spider", new Position(3, 0), false));
        entities.add(new EntityResponse("4", "zombie_toast", new Position(4, 0), false));

        List<EntityResponse> eResList = initDungonRes.getEntities();
        assertEquals(eResList.containsAll(entities), entities.containsAll(eResList));
    }

    @Test
    @DisplayName("Test config is read correctly")
    public void testConfigReadCorrectly() {
        DungeonManiaController dmc4 = new DungeonManiaController();
        dmc4.newGame("newGameTest_createStaticEntities", "c_movementTest_testMovementDown");

        Dungeon config = DungeonManiaController.getCurrDungeon();
        assertEquals(config.getConfig("bomb_radius"), 1);
        assertEquals(config.getConfig("invisibility_potion_duration"), 1);
        assertEquals(config.getConfig("player_attack"), 10);
        assertEquals(config.getConfig("spider_spawn_rate"), 0);
        assertEquals(config.getConfig("zombie_spawn_rate"), 0);

    }

    @Test
    @DisplayName("Test milestone 3 config is read correctly")
    public void testConfigReadCorrectlyMilestone3() {
        DungeonManiaController dmc4 = new DungeonManiaController();
        dmc4.newGame("newGameTest_createStaticEntities", "milestone3SimpleConfig");

        Dungeon config = DungeonManiaController.getCurrDungeon();
        assertEquals(config.getConfig("mind_control_duration"), 1);
        assertEquals(config.getConfig("assassin_attack"), 1);
        assertEquals(config.getConfig("player_attack"), 5);
        assertEquals(config.getConfig("hydra_spawn_rate"), 0);

    }

}
