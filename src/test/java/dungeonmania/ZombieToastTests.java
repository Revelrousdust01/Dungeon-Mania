package dungeonmania;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;


import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
public class ZombieToastTests {

    @Test
    @DisplayName("Test Zombie random movement")
    public void testZombieRandomMove() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_zombie_toast_spawn", "c_zombieToastMove");
        for (int i = 0; i < 10; i++) {
            response = newDungeon.tick(Direction.UP);
        }
        for (int i = 0; i < 10; i++) {
            // move 10 times
            Position initialPosition = getEntities(response, "zombie_toast").get(0).getPosition();
            EntityResponse zombieEntity = null;
            response = newDungeon.tick(Direction.UP);
            zombieEntity = getEntities(response, "zombie_toast").get(0);
            assertTrue(Position.isAdjacent(zombieEntity.getPosition(), initialPosition));
        }
    }

    @Test
    @DisplayName("Test zombie moves away from invincible")
    public void testZombieMoveAway() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_zombieToast_potion", "c_usePotionTest_duration");

        response = newDungeon.tick(Direction.LEFT);
        String entityId = getInventory(response, "invincibility_potion").get(0).getId();
        response = assertDoesNotThrow(() -> newDungeon.tick(entityId));
        // Player is invincible
        for (int i = 0; i < 4; i++) {
            Position initialPosition = getEntities(response, "zombie_toast").get(0).getPosition();
            response = newDungeon.tick(Direction.LEFT);
            EntityResponse zombieEntity = getEntities(response, "zombie_toast").get(0);
            assertTrue(zombieEntity.getPosition().getX() < initialPosition.getX() || zombieEntity.getPosition().getY() < initialPosition.getY());
        }
    }
}
