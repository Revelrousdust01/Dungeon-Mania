package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import java.lang.IllegalArgumentException;
import java.util.List;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestBuild {
    @Test
    @DisplayName("Test collect use bomb no error")
    public void testCollectUseBombNoError() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_use", "simpleConfig");

        // Pick up bomb
        DungeonResponse res1 = assertDoesNotThrow(() -> dmc.tick(Direction.RIGHT));
        assertEquals(0, countEntityOfType(res1, "bomb"));
        assertEquals(1, getInventory(res1, "bomb").size());

        // Use bomb
        String bombId = assertDoesNotThrow(() -> getInventory(res1, "bomb").get(0).getId());
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.tick(bombId));
        assertEquals(1, countEntityOfType(res2, "bomb"));
        assertEquals(0, getInventory(res2, "bomb").size());
    }

    @Test
    @DisplayName("Test use item not allowed")
    public void testUseIllegalArgumentException() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Test that using arrow or another other unallowed objects throws illegal argument exception
        DungeonResponse res1 = assertDoesNotThrow(() -> dmc.tick(Direction.RIGHT));
        assertEquals(1, getInventory(res1, "arrow").size());

        String arrowId = getInventory(res1, "arrow").get(0).getId();
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(arrowId));

    }

    @Test
    @DisplayName("Test use item not exist")
    public void testUseInvalidActionException() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_use", "simpleConfig");

        // Test that using an nonexistent object throws invalid action exception
        assertThrows(InvalidActionException.class, () -> dmc.tick("notexist"));

    }

    @Test
    @DisplayName("Test collecting collectible entity when it's overlapping with a moving entity")
    public void testCollectOverlapMovingEntity() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_overlapCollect", "c_durability");

        // Test that moving onto square that has overlapping entity with collectible
        // the collectible will still be collected
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        assertEquals(1, getInventory(res, "sword").size());

    }

    @Test
    @DisplayName("Test collecting collectible entity when it's overlapping with a static entity")
    public void testCollectOverlapStaticEntity() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_overlapCollect", "c_durability");

        // Test that moving onto square that has overlapping entity with collectible
        // the collectible will still be collected
        DungeonResponse res = dmc.tick(Direction.LEFT);
        assertEquals(1, getInventory(res, "sword").size());

    }

    @Test
    @DisplayName("Test build bow")
    public void testBuildBow() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Collect the 1 wood + 3 arrow
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res1, "wood").size());
        assertEquals(3, getInventory(res1, "arrow").size());
        List<String> expectedBuildables = List.of("bow");
        assertEquals(expectedBuildables, res1.getBuildables());

        // Test that bow is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("bow"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "arrow").size());
        assertEquals(1, getInventory(res2, "bow").size());

    }

    @Test
    @DisplayName("Test shield with treasure")
    public void testShieldTreasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Collect the 2 wood + 1 treasure
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        DungeonResponse res1 = dmc.tick(Direction.DOWN);

        assertEquals(2, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "treasure").size());
        List<String> expectedBuildables = List.of("shield");
        assertEquals(expectedBuildables, res1.getBuildables());

        // Test that shield is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "treasure").size());
        assertEquals(1, getInventory(res2, "shield").size());

    }

    @Test
    @DisplayName("Test shield with key")
    public void testShieldKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Collect the 2 wood + 1 key
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        DungeonResponse res1 = dmc.tick(Direction.UP);

        assertEquals(2, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "key").size());
        List<String> expectedBuildables = List.of("shield");
        assertEquals(expectedBuildables, res1.getBuildables());

        // Test that shield is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "key").size());
        assertEquals(1, getInventory(res2, "shield").size());

    }

    @Test
    @DisplayName("Test shield with key + treasure, only one of them is used")
    public void testShieldKeyTreasureOneUsed() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Collect the 2 wood + 1 key + 1 treasure
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.LEFT);
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.UP);
        DungeonResponse res1 = dmc.tick(Direction.UP);

        assertEquals(2, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "key").size());
        assertEquals(1, getInventory(res1, "treasure").size());

        // Test that shield is built successfully and either key or treasure is used
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(1, getInventory(res2, "key").size() + getInventory(res2, "treasure").size());
        assertEquals(1, getInventory(res2, "shield").size());

    }

    @Test
    @DisplayName("Test build insufficient items")
    public void testBuildInvalidActionException() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Test for insufficient items for bow
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.RIGHT);
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));

        // Test for insufficient items for shield
        assertThrows(InvalidActionException.class, () -> dmc.build("shield"));
    }

    @Test
    @DisplayName("Test build invalid item")
    public void testBuildIllegalArgumentException() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_build", "simpleConfig");

        // Test that building item that is not bow or shield throws illegal argument exception
        assertThrows(IllegalArgumentException.class, () -> dmc.build("wood"));
        assertThrows(IllegalArgumentException.class, () -> dmc.build("notexist"));

    }

    @Test
    @DisplayName("Test build sceptre: wood, key, sun_stone")
    public void testBuildSceptreWoodKeysun_stone() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");

        // Collect the 1 wood + 1 key + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN); // wood collected
        dmc.tick(Direction.UP); 
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);  // key collected
        dmc.tick(Direction.DOWN);
        DungeonResponse res1 = dmc.tick(Direction.RIGHT); // sun_stone collected

        assertEquals(1, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "key").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that sceptre is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "key").size());
        assertEquals(0, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre: arrow, key, sun_stone")
    public void testBuildSceptreArrowKeysun_stone() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");

        // Collect the 2 arrow + 1 key + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP); 
        dmc.tick(Direction.UP); // 2 arrow collected
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN); 
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP);  // key collected
        dmc.tick(Direction.DOWN);
        DungeonResponse res1 = dmc.tick(Direction.RIGHT); // sun_stone collected

        assertEquals(2, getInventory(res1, "arrow").size());
        assertEquals(1, getInventory(res1, "key").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that sceptre is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res2, "arrow").size());
        assertEquals(0, getInventory(res2, "key").size());
        assertEquals(0, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre: arrow, treasure, sun_stone")
    public void testBuildSceptreArrowTreasuresun_stone() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");

        // Collect the 2 arrow + 1 treasure + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.UP); 
        dmc.tick(Direction.UP); // 2 arrow collected
        dmc.tick(Direction.DOWN);
        dmc.tick(Direction.DOWN); 
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN); // treasure collected
        dmc.tick(Direction.UP);  
        DungeonResponse res1 = dmc.tick(Direction.RIGHT); // sun_stone collected

        assertEquals(2, getInventory(res1, "arrow").size());
        assertEquals(1, getInventory(res1, "treasure").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that sceptre is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res2, "arrow").size());
        assertEquals(0, getInventory(res2, "treasure").size());
        assertEquals(0, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre: wood, treasure, sun_stone")
    public void testBuildSceptreWoodTreasuresun_stone() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");
        
        // Collect the 1 wood + 1 treasure + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN); // wood collected
        dmc.tick(Direction.UP); 
        dmc.tick(Direction.RIGHT);
        dmc.tick(Direction.DOWN); // treasure collected
        dmc.tick(Direction.UP);  
        DungeonResponse res1 = dmc.tick(Direction.RIGHT); // sun_stone collected

        assertEquals(1, getInventory(res1, "wood").size());
        assertEquals(1, getInventory(res1, "treasure").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that sceptre is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(0, getInventory(res2, "treasure").size());
        assertEquals(0, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "sceptre").size());

    }

    @Test
    @DisplayName("Test build midnight armour, has zombie")
    public void testBuildArmourZombie() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildArmourZombie", "milestone3SimpleConfig");

        // Collect the 1 sword + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res1, "sword").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that buildable does not contain midnight armour
        assertFalse(res1.getBuildables().contains("midnight_armour"));

        // Test that invalid action exception is thrown
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));

    }

    @Test
    @DisplayName("Test build midnight armour, no zombie")
    public void testBuildArmourNoZombie() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildArmourNoZombie", "milestone3NoSpawn");

        // Collect the 1 sword + 1 sun_stone
        dmc.tick(Direction.RIGHT);
        DungeonResponse res1 = dmc.tick(Direction.RIGHT);

        assertEquals(1, getInventory(res1, "sword").size());
        assertEquals(1, getInventory(res1, "sun_stone").size());

        // Test that sceptre is built successfully
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(0, getInventory(res2, "sword").size());
        assertEquals(0, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "midnight_armour").size());

    }

    @Test
    @DisplayName("Test build sceptre: arrow, key (replaced with sun_stone and retained), sun_stone")
    public void testBuildSceptreKeyReplacedWithSunstone() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");

        // Collect the 1 wood + 2 sun_stone
        dmc.tick(Direction.UP); 
        dmc.tick(Direction.UP);
        DungeonResponse res1 = dmc.tick(Direction.UP);

        assertEquals(2, getInventory(res1, "sun_stone").size());
        assertEquals(1, getInventory(res1, "wood").size());

        // Test that sceptre is built successfully with sunstone replacing key
        // And that 1 sunstone is retained
        DungeonResponse res2 = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(0, getInventory(res2, "wood").size());
        assertEquals(1, getInventory(res2, "sun_stone").size());
        assertEquals(1, getInventory(res2, "sceptre").size());

    }

    @Test
    @DisplayName("Test build sceptre: sunstone only counted once")
    public void testBuildSceptreSunstoneCountedOnce() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("buildTest_buildSceptre", "milestone3SimpleConfig");

        // Collect the 1 wood + 1 sun_stone
        dmc.tick(Direction.UP); 
        DungeonResponse res1 = dmc.tick(Direction.UP);

        assertEquals(1, getInventory(res1, "sun_stone").size());
        assertEquals(1, getInventory(res1, "wood").size());

        // Test that sceptre is not built due to sun_stone cannot be counted twice
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));

    }

}
