package dungeonmania;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class SpiderTests {

    @Test
    @DisplayName("Test that spiders can spawn")
    public void testSpidersSpawn() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_spider_spawn", "simpleConfig");
        for (int i = 0; i < 10; i++) {
            response = newDungeon.tick(Direction.UP);
        }
        List<EntityResponse> entities = getEntities(response, "spider");
        assertEquals(1, entities.size());

    }
  
    @Test
    @DisplayName("Test spider circle spawn")
    public void testSpiderSurround() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_spiderMovementBasic", "simpleConfig");

        List<Position> positions = new ArrayList<Position>() {{
            add(new Position(5, 4));
            add(new Position(6, 4));
            add(new Position(6, 5));
            add(new Position(6, 6));
            add(new Position(5, 6));
            add(new Position(4, 6));
            add(new Position(4, 5));
            add(new Position(4, 4));
            add(new Position(5, 4));
        }};

        Position position = new Position(5, 5);
        EntityResponse spiderEntity = getEntities(response, "spider").get(0);
        assertEquals(position, spiderEntity.getPosition());

        // Have spider move 9 times
        for (int i = 0; i < 9; i++) {
            response = newDungeon.tick(Direction.DOWN);
            spiderEntity = getEntities(response, "spider").get(0);
            assertTrue(spiderEntity.getPosition().equals(positions.get(i)));
        }

        spiderEntity = getEntities(response, "spider").get(0);
        assertEquals(position.translateBy(Direction.UP), spiderEntity.getPosition());
    }

    @Test
    @DisplayName("Test Spider reverses on boulders")
    public void testSpiderBoulders() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_spiderMovementBoulder", "simpleConfig");

        List<Position> positions = new ArrayList<Position>() {{
            add(new Position(5, 4));
            add(new Position(6, 4));
            add(new Position(6, 5));
            add(new Position(6, 6));
            add(new Position(5, 6));
            add(new Position(6, 6));
            add(new Position(6, 5));
            add(new Position(6, 4));
            add(new Position(5, 4));
            add(new Position(4, 4));
            add(new Position(4, 5));
            add(new Position(4, 4));
        }};

        // Have spider move 12 times, reverse on boulder twice
        for (int i = 0; i < 12; i++) {
            response = newDungeon.tick(Direction.DOWN);
            EntityResponse spiderEntity = getEntities(response, "spider").get(0);
            assertEquals(positions.get(i), spiderEntity.getPosition());
        }
    }

    @Test
    @DisplayName("Test spider boulder on top")
    public void testSpiderBoulderAbove() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_spiderMovementBoulderAbove", "simpleConfig");

        List<Position> positions = new ArrayList<Position>() {{
            add(new Position(5, 6));
            add(new Position(6, 6));
            add(new Position(6, 5));
            add(new Position(6, 4));
            add(new Position(6, 5));
            add(new Position(6, 6));
            add(new Position(5, 6));
            add(new Position(4, 6));
            add(new Position(4, 5));
            add(new Position(4, 4));
            add(new Position(4, 5));
        }};

        // Have spider move 11 times, reverse on boulder thrice
        for (int i = 0; i < 11; i++) {
            response = newDungeon.tick(Direction.DOWN);
            EntityResponse spiderEntity = getEntities(response, "spider").get(0);
            assertEquals(positions.get(i), spiderEntity.getPosition());
        }
    }

    @Test
    @DisplayName("Test spider stuck between boulder")
    public void testSpiderBoulderStuck() {
        DungeonManiaController newDungeon = new DungeonManiaController();
        DungeonResponse response = newDungeon.newGame("d_spiderMovementStuck", "simpleConfig");

        Position initialPosition = new Position(5, 5);

        // Have spider move 11 times, reverse on boulder thrice
        for (int i = 0; i < 11; i++) {
            response = newDungeon.tick(Direction.DOWN);
            EntityResponse spiderEntity = getEntities(response, "spider").get(0);
            assertEquals(initialPosition, spiderEntity.getPosition());
        }
    }
}
