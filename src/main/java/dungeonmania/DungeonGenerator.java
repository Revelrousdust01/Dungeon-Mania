package dungeonmania;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dungeonmania.Entity.*;
import dungeonmania.Entity.StaticEntity.*;
import dungeonmania.util.Position;
import dungeonmania.Goals.*;


public class DungeonGenerator extends Dungeon {
    public DungeonGenerator (String dungeonId, String dungeonName, Config config) {
        super(dungeonId, dungeonName, config);
        this.dungeonGoal = new GoalLeaf("exit");
    }

    public void generateMaze(int xStart, int yStart, int xEnd, int yEnd) {
        Position start = new Position(xStart, yStart);
        Position end = new Position(xEnd, yEnd);

        new Exit(false, end, getNewId(), "exit");
        new Player(false, start, getNewId(), "player");

        // Make the maze, false is wall, true is empty
        Map<Position, Boolean> maze = new HashMap<Position, Boolean>();

        // Initialise all positions in maze to false (wall)
        for (int x = xStart - 1; x <= xEnd + 1; x++) {
            for (int y = yStart - 1; y <= yEnd + 1; y++) {
                maze.put(new Position(x, y), false);
            }
        }

        generateMazeMap(maze, start, end);

        // Use the maze map to generate walls
        for (Map.Entry<Position, Boolean> entry : maze.entrySet()) {
            Position pos = entry.getKey();
            Boolean isEmpty = entry.getValue();
            
            if (!isEmpty) {
                new Wall(false, pos, getNewId(), "wall");
            }
        }
        

    }

    private void generateMazeMap(Map<Position, Boolean> maze, Position start, Position end) {
        maze.replace(start, true);

        List<Position> options = new ArrayList<Position>();
        for (Position p : findNeighbours(start, maze, 2, false)) {
            options.add(p);
        }

        Random rand = new Random();
        while(!options.isEmpty()) {
            Position next = options.get(rand.nextInt(options.size()));
            options.remove(next);

            List<Position> neighbours = findNeighbours(next, maze, 2, true);
            if (!neighbours.isEmpty()) {
                Position neighbour = neighbours.get(rand.nextInt(neighbours.size()));
                maze.replace(next, true);
                maze.replace(findInBetweenPosition(next, neighbour), true);
                maze.replace(neighbour, true);
            }

            for (Position p : findNeighbours(next, maze, 2, false)) {
                options.add(p);
            }

        }

        // Dealing with the case exit isn't connected to the pathway
        if (maze.get(end) == false) {
            maze.replace(end, true);

            if (findNeighbours(end, maze, 1, true).isEmpty()) {
                List<Position> neighbours = findNeighbours(end, maze, 1, false);
                maze.replace(neighbours.get(rand.nextInt(neighbours.size())), true);
            }
        }
    }

    private List<Position> findNeighbours(Position curr, Map<Position, Boolean> maze, int distance, Boolean isEmpty) {
        List<Position> distanceAway = new ArrayList<Position>();
        int x = curr.getX();
        int y = curr.getY();
        distanceAway.add(new Position(x, y + distance));
        distanceAway.add(new Position(x, y - distance));
        distanceAway.add(new Position(x + distance, y));
        distanceAway.add(new Position(x - distance, y));

        List<Position> options = new ArrayList<Position>();
        for (Position p : distanceAway) {
            if (maze.get(p) == null) continue;
            if (isBoundary(p, maze)) continue;

            if (!isEmpty && maze.get(p) == false) {
                options.add(p);
            }

            if (isEmpty && maze.get(p) == true) {
                options.add(p);
            }
        }
        return options;

    }

    private boolean isBoundary(Position curr, Map<Position, Boolean> maze) {
        int xLower = maze.keySet().stream().map(p -> p.getX()).min((i, j) -> i.compareTo(j)).get();
        int xUpper = maze.keySet().stream().map(p -> p.getX()).max((i, j) -> i.compareTo(j)).get();
        int yLower = maze.keySet().stream().map(p -> p.getY()).min((i, j) -> i.compareTo(j)).get();
        int yUpper = maze.keySet().stream().map(p -> p.getY()).max((i, j) -> i.compareTo(j)).get();

        return (curr.getX() == xLower) || (curr.getX() == xUpper) || (curr.getY() == yLower) || (curr.getY() == yUpper);
        
    }

    private Position findInBetweenPosition(Position a, Position b) {
        int x = (a.getX() - b.getX()) / 2;
        int y = (a.getY() - b.getY()) / 2;
        return new Position(a.getX() - x, a.getY() - y);
    }

}
