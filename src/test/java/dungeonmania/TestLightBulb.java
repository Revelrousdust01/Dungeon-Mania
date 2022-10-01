package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.LightBulb;
import dungeonmania.Entity.StaticEntity.Wire;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.*;
import static dungeonmania.TestUtils.*;

public class TestLightBulb {
    @Test
    @DisplayName("Test lightbulb OR")
    public void testLightBulbOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_lightBulbOR", "c_testLightBulb");

        DungeonResponse res = dmc.tick(Direction.LEFT);

        String lightbulbId = getEntities(res, "light_bulb").get(0).getId();
        LightBulb lightbulb = (LightBulb) Entity.findEntity(lightbulbId);

        assertEquals(false, lightbulb.getTriggered());

        res = dmc.tick(Direction.LEFT);        
        assertEquals(true, lightbulb.getTriggered());
    }

    @Test
    @DisplayName("TestLightbulb AND")
    public void testLightBulbAND() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_lightBulbAND", "c_testLightBulb");
        DungeonResponse res = dmc.tick(Direction.RIGHT);

        String lightbulbId = getEntities(res, "light_bulb").get(0).getId();
        LightBulb lightbulb = (LightBulb) Entity.findEntity(lightbulbId); 
        
        assertEquals(false, lightbulb.getTriggered());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(true, lightbulb.getTriggered());
    }

    @Test
    @DisplayName("Test lightbulb XOR")
    public void testLightBulbXOR() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_lightBulbXOR", "c_testLightBulb");

        DungeonResponse res = dmc.tick(Direction.LEFT);

        String lightbulbId = getEntities(res, "light_bulb").get(0).getId();
        LightBulb lightbulb = (LightBulb) Entity.findEntity(lightbulbId);

        String switchID = getEntities(res, "switch").get(0).getId();
        FloorSwitch floorSwitch = (FloorSwitch) Entity.findEntity(switchID);

        assertEquals(false, lightbulb.getTriggered());

        res = dmc.tick(Direction.LEFT);
        assertEquals(true, floorSwitch.isTriggered());
        assertEquals(true, lightbulb.getTriggered());
    }

    @Test
    @DisplayName("Test lightbulb COAND")
    public void testLightBulbCOAND() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_lightBulbCOAND", "c_testLightBulb");
        DungeonResponse res = dmc.tick(Direction.LEFT);

        String lightbulbId = getEntities(res, "light_bulb").get(0).getId();
        LightBulb lightbulb = (LightBulb) Entity.findEntity(lightbulbId); 

        String switchId = getEntities(res, "switch").get(0).getId();
        FloorSwitch floorSwitch = (FloorSwitch) Entity.findEntity(switchId);
        
        assertEquals(false, lightbulb.getTriggered());
        assertEquals(false, floorSwitch.isTriggered());
        res = dmc.tick(Direction.LEFT);
        assertEquals(true, floorSwitch.isTriggered());
        assertEquals(true, lightbulb.getTriggered());
    }
}
