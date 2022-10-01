package dungeonmania;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static dungeonmania.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.Wire;
import dungeonmania.response.models.*;
import dungeonmania.util.*;



public class TestWire {
    @Test
    @DisplayName("Test Wire Triggered when boulder is placed")
    public void testTriggeredWire() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_testWire", "c_testWire");
        DungeonResponse res = dmc.tick(Direction.RIGHT);
        String wireId = getEntities(res, "wire").get(0).getId();
        Wire wire = (Wire) Entity.findEntity(wireId);
        assertEquals(true, wire.getTriggered());

    }
    @Test
    @DisplayName("Test Wire Triggered when boulder is placed and not cardinally adjacent")
    public void testTriggeredWireNotCardinallyAdjacent() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_testWire", "c_testWire");
        DungeonResponse res = dmc.tick(Direction.UP);
        String wireId = getEntities(res, "wire").get(0).getId();
        Wire wire = (Wire) Entity.findEntity(wireId);
        assertEquals(false, wire.getTriggered());
    }

}
