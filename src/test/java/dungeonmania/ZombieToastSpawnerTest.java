package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.*;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;

public class ZombieToastSpawnerTest {

    @Test
    @DisplayName("Test that Zombies can spawn")
    public void testZombieSpawn() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_zombie_toast_spawn", "simpleConfig");
        for (int i = 0; i < 10; i++) {
            response = newDungeon.tick(Direction.UP);
        }
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(1, entities.size());

    }

    @Test
    @DisplayName("Test that Zombies can't spawn on top of another entity")
    public void testNoZombieSpawn() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_noZombieSpawn", "c_ZombieSpawn");
        for (int i = 0; i < 10; i++) {
            response = newDungeon.tick(Direction.UP);
        }
        List<EntityResponse> entities = getEntities(response, "zombie_toast");
        assertEquals(0, entities.size());

    }

    @Test
    @DisplayName("Test that ZombieToastSpawner can be destroyed")
    public void destroyZombieToastSpawner() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_ZombieToastSpawnerDestroy", "simpleConfig");
        List<EntityResponse> zombieToastSpawnerEntity = getEntities(response, "zombie_toast_spawner");
        assertEquals(1, zombieToastSpawnerEntity.size());

        for (int i = 0; i < 5; i++) {
            response = newDungeon.tick(Direction.RIGHT);
        }
        String spawnerId = getEntities(response, "zombie_toast_spawner").get(0).getId();
        DungeonResponse response2 = assertDoesNotThrow(() -> newDungeon.interact(spawnerId));
        assertEquals(0, countEntityOfType(response2, "zombie_toast_spawner"));

    }


    @Test
    @DisplayName("Test that ZombieToastSpawner can't be destroyed without weapon")
    public void destroyZombieToastSpawnerNoSword() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_cantdestroyzombietoastspawner", "simpleConfig");
        List<EntityResponse> zombieToastSpawnerEntity = getEntities(response, "zombie_toast_spawner");
        assertEquals(1, zombieToastSpawnerEntity.size());
        response = newDungeon.tick(Direction.RIGHT);
        String spawnerId = getEntities(response, "zombie_toast_spawner").get(0).getId();
        assertThrows(InvalidActionException.class,() -> newDungeon.interact(spawnerId));
    }

}
