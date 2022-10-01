package dungeonmania;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
public class HydraTest {

    @Test
    @DisplayName("Test hydra random movement")
    public void testhydraRandomMove() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_hydraPotion", "c_usePotionTest_duration");
        for (int i = 0; i < 10; i++) {
            response = newDungeon.tick(Direction.RIGHT);
        }
        for (int i = 0; i < 10; i++) {
            // move 10 times
            Position initialPosition = getEntities(response, "hydra").get(0).getPosition();
            EntityResponse hydraEntity = null;
            response = newDungeon.tick(Direction.RIGHT);
            hydraEntity = getEntities(response, "hydra").get(0);
            assertTrue(Position.isAdjacent(hydraEntity.getPosition(), initialPosition));
        }
    }

    @Test
    @DisplayName("Test hydra moves away from invincible")
    public void testhydraMoveAway() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_hydraPotion", "c_usePotionTest_duration");

        response = newDungeon.tick(Direction.RIGHT);
        // Get the potion and use it
        String entityId = getInventory(response, "invincibility_potion").get(0).getId();
        response = assertDoesNotThrow(() -> newDungeon.tick(entityId));
        // Player is invincible
        for (int i = 0; i < 4; i++) {
            Position initialPosition = getEntities(response, "hydra").get(0).getPosition();
            response = newDungeon.tick(Direction.LEFT);
            EntityResponse hydraEntity = getEntities(response, "hydra").get(0);
            assertTrue(hydraEntity.getPosition().getX() > initialPosition.getX() || hydraEntity.getPosition().getY() > initialPosition.getY());
        }
    }
}
