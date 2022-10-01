package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
public class TestCollecting {
    
    @Test
    @DisplayName("Tests whether a variety of collectible can be collected")
    public void testCollecting() {
        // Create dungeon with variety of objects
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame("d_allCollectiblesTest", "simpleConfig");
        
        // Check dungeon has all entities and player has no items in inventory
        assertEquals(initDungonRes.getEntities().size(), 11);
        assert(initDungonRes.getInventory().size() == 0);

        // Move right to collect key
        DungeonResponse firstDungonRes = dmc.tick(Direction.RIGHT);

        // Check inventory
        assertEquals(firstDungonRes.getInventory().size(), 1);
        assertTrue(firstDungonRes.getInventory().get(0).getType().equals("key"));

        // Check item has been removed from map
        assertEquals(firstDungonRes.getEntities().size(), 10);
        
        // ---
        
        // Move down to collect treasure
        DungeonResponse secondDungonRes = dmc.tick(Direction.DOWN);
        assertEquals(secondDungonRes.getInventory().size(), 2);
        assertEquals(secondDungonRes.getEntities().size(), 9);
        
        // Move Left to collect Invincibility potion
        DungeonResponse thirdDungonRes = dmc.tick(Direction.LEFT);
        assertEquals(thirdDungonRes.getInventory().size(), 3);
        assertEquals(thirdDungonRes.getEntities().size(), 8);
        
        // Move Down to collect Invisibility potion
        DungeonResponse fourthDungonRes = dmc.tick(Direction.DOWN);
        assertEquals(fourthDungonRes.getInventory().size(), 4);
        assertEquals(fourthDungonRes.getEntities().size(), 7);
        
        // Move right to collect wood
        DungeonResponse fifthDungonRes = dmc.tick(Direction.RIGHT);
        assertEquals(fifthDungonRes.getInventory().size(), 5);
        assertEquals(fifthDungonRes.getEntities().size(), 6);
        
        // Move right to collect arrow
        DungeonResponse sixthDungonRes = dmc.tick(Direction.RIGHT);
        assertEquals(sixthDungonRes.getInventory().size(), 6);
        assertEquals(sixthDungonRes.getEntities().size(), 5);
        
        // Move right to collect bomb
        DungeonResponse seventhDungonRes = dmc.tick(Direction.RIGHT);
        assertEquals(7, seventhDungonRes.getInventory().size());
        assertEquals(seventhDungonRes.getEntities().size(), 4);
        
        // Move up to collect sword
        DungeonResponse eigthDungonRes = dmc.tick(Direction.UP);
        assertEquals(eigthDungonRes.getInventory().size(), 8);
        assertEquals(eigthDungonRes.getEntities().size(), 3);

        // Finally check items??
    }
}
