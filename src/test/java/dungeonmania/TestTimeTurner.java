package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;

public class TestTimeTurner {
    
    @Test
    public void testTimeTurnerCollection() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTurnerCollection", "simpleConfig");

        assertEquals(1, getEntities(res, "time_turner").size());
        assertEquals(0, getInventory(res, "time_turner").size());

        res = dmc.tick(Direction.RIGHT);
        
        assertEquals(0, getEntities(res, "time_turner").size());
        assertEquals(1, getInventory(res, "time_turner").size());
    }
}
