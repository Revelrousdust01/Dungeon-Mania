package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.response.models.*;
import dungeonmania.util.*;

public class TestDungeonGenerator {
    // Helpers
    public boolean hasWall(Position expected, List<Position> walls) {
        return walls.stream().anyMatch(e -> e.equals(expected));
    }

    public boolean hasNoWall(Position expected, List<Position> noWalls) {
        return noWalls.stream().anyMatch(e -> e.equals(expected));
    }

    public void dfs(Position curr, ArrayList<Position> visited, List<Position> noWalls) {
        visited.add(curr);

        // Add adjacent no wall positions of curr to stack
        List<Position> adjList = curr.getAdjacentPositions();
        for (Position adj: adjList) {
            if (hasNoWall(adj, noWalls) && !(visited.contains(adj))) {
                dfs(adj, visited, noWalls);
            }
        }
    }

    @Test
    @DisplayName("Test generated dungeon has player, exit and exit goal")
    public void testGenerateExit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 5, 5, "goalConfig");

        // Test that player position is at xStart, yStart
        Position expected = new Position(0, 0);
        Position player = getEntities(res, "player").get(0).getPosition();
        assertEquals(expected, player);

        // Test that exit position is at xEnd, yEnd
        expected = new Position(5, 5);
        Position exit = getEntities(res, "exit").get(0).getPosition();
        assertEquals(expected, exit);

        // Test that goals is exit
        assertTrue(res.getGoals().contains(":exit"));

    }

    @Test
    @DisplayName("Test generated dungeon is enclosed by walls")
    public void testGenerateEnclosed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 5, 5, "goalConfig");

        // Test that the maze is enclosed by walls 
        // Where enclosed space is xEnd - xStart + 2 width, yEnd - yStart + 2 height
        List<Position> walls = getEntities(res, "wall").stream().map(e -> e.getPosition()).collect(Collectors.toList());
        // Horizontal walls
        for (int x = -1; x <= 6; x++) {
            assertTrue(hasWall(new Position(x, -1), walls));
            assertTrue(hasWall(new Position(x, 6), walls));
        }

        // Vertical walls
        for (int y = -1; y <= 6; y++) {
            assertTrue(hasWall(new Position(-1, y), walls));
            assertTrue(hasWall(new Position(6, y), walls));
        }
        
    }

    @Test
    @DisplayName("Test generated dungeon has solution path")
    public void testGenerateHasPath() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 5, 5, "goalConfig");

        Position exit = new Position(5, 5);

        // Get a list of all positions that has no walls in the enclosed space
        List<Position> noWalls = new ArrayList<Position>();
        for (int x = -1; x <= 6; x++) {
            for (int y = -1; y <= 6; y++) {
                noWalls.add(new Position(x, y));
            }
        }
        List<Position> walls = getEntities(res, "wall").stream().map(e -> e.getPosition()).collect(Collectors.toList());
        for (Position hasWall : walls) {
            noWalls.remove(hasWall);
        }

        // Do DFS to determine whether there is a solution to the maze
        Position start = new Position(0, 0);
        ArrayList<Position> visited = new ArrayList<Position>();
        dfs(start, visited, noWalls);
        assertTrue(visited.contains(exit));
    }

    @Test
    @DisplayName("Test generated dungeon can accept negative coordinates")
    public void testGenerateNegative() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertDoesNotThrow(() -> dmc.generateDungeon(-10, -10, -5, -5, "goalConfig"));
        
    }

    @Test
    @DisplayName("Test smallest possible dungeon generation")
    public void testGenerateSmallest() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertDoesNotThrow(() -> dmc.generateDungeon(0,0, 1,1, "goalConfig"));
        
    }

    @Test
    @DisplayName("Test mixed positive negative dungeon generation")
    public void testGenerateMixed() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertDoesNotThrow(() -> dmc.generateDungeon(-41, 4, -10,33, "goalConfig"));
        
    }

    @Test
    @DisplayName("Test dungeon is random")
    public void testGenerateIsRandom() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res1 = dmc.generateDungeon(0, 0, 10,10, "goalConfig");
        DungeonResponse res2 = dmc.generateDungeon(0, 0, 10, 10, "goalConfig");

        List<Integer> wall1 = getEntities(res1, "wall").stream().map(e -> e.getPosition().hashCode()).collect(Collectors.toList());
        List<Integer> wall2 = getEntities(res2, "wall").stream().map(e -> e.getPosition().hashCode()).collect(Collectors.toList());
        assertFalse(wall1.equals(wall2));
        
    }

    @Test
    @DisplayName("Test random dungeon generation")
    public void testGenerateRandom() {
        DungeonManiaController dmc = new DungeonManiaController();
        Random rand = new Random(10);

        for (int i = 0; i < 50; i++) {
            List<Integer> args = new ArrayList<Integer>();
            args.add(rand.nextInt(20));
            args.add(rand.nextInt(20));
            args.add(rand.nextInt(20));
            args.add(rand.nextInt(20));
            Collections.sort(args);
            assertDoesNotThrow(() -> dmc.generateDungeon(args.get(0), args.get(1), args.get(2), args.get(3), "goalConfig"));
        }
        
    }

}
