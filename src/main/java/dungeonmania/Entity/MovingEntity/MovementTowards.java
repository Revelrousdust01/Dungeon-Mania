package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.Portal;
import dungeonmania.Entity.StaticEntity.RigidEntity;
import dungeonmania.Entity.StaticEntity.Swamp;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class MovementTowards implements Movement {
    // Checking 30 + 30 + 1 by 30 + 30 + 1 position around player
    private static int bound = 30;

    @Override
    public void move(MovingEntity movingEntity, Position playerPosition) {
        Position newPosition = pathDijkstra(movingEntity, playerPosition);
        movingEntity.setPosition(newPosition);
    }

    // Runs dijkstra algorithm and returns the position the entity needs to move to
    private Position pathDijkstra(MovingEntity movingEntity, Position playerPosition) {
        HashMap<Position, Integer> dist = new HashMap<Position, Integer>();
        HashMap<Position, Boolean> visited = new HashMap<Position, Boolean>();
        HashMap<Position, Position> parent = new HashMap<Position, Position>();
        Queue<Position> queue = new LinkedList<Position>();

        Position gridStartingPosition = movingEntity.getPosition();

        // Create dist, visited and parent 
        for (int i = 0; i < 2 * bound + 1; i++) {
            for (int j = 0; j < 2 * bound + 1; j++) {
                Position position = new Position(gridStartingPosition.getX() - bound + i, gridStartingPosition.getY() - bound + j);
                // If it is a rigidEntity, don't add it into the list since we cannot traverse it
                if (!(Entity.findEntityAtPosition(position) instanceof RigidEntity)){
                    dist.put(position, Integer.MAX_VALUE);
                    visited.put(position, false);
                    parent.put(position, null);
                }
            }
        }
        
        // Set the initial position
        dist.replace(movingEntity.getPosition(), 0);
        queue.add(movingEntity.getPosition());

        // Going through all the possible positions to find the shortest path
        while (!queue.isEmpty()) {
            Position pos = queue.remove();
            List<Position> adjacent = reachable(queue, visited, pos, movingEntity);
            visited.replace(pos, true);

            // Go through all the adjacent positions and add 1 to them if we haven't visited them yet
            for (Position position: adjacent) {
                int distValue = 1;
                // Check if the position is a swamp and whether we need to add the movementFactor
                if (Swamp.checkSwamp(position) != null) {
                    distValue = Swamp.checkSwamp(position).getMovementFactor() + 1;
                }

                if (dist.get(pos) + distValue < dist.get(position)) {
                    dist.replace(position, dist.get(pos) + distValue);
                    parent.replace(position, pos);
                }
            }
        }
        return getMoving(parent, playerPosition, movingEntity.getPosition());
    }

    // Add in the surrounding positions that have not been checked and within bound
    private List<Position> reachable(Queue<Position> queue, HashMap<Position, Boolean> visited, Position pos, MovingEntity movingEntity) {
        List<Direction> directions = MovingEntity.checkForREntity(pos);
        List<Position> adjacent = new ArrayList<Position>();

        for (Direction direction: directions) {
            Position tmpPosition = pos.translateBy(direction);
            // Add next positions that we can go that we have not visited

            if (visited.containsKey(tmpPosition) && !visited.get(tmpPosition)) {
                Entity destinationEntity = checkPortal(tmpPosition);

                if (destinationEntity != null) {
                    // When tmpPosition is a portal that can be teleported
                    Portal portalEntry = (Portal) destinationEntity;
                    Position portalExPosition = portalEntry.getOtherPortal().getPosition().translateBy(direction);
                    if (visited.containsKey(portalExPosition) && !visited.get(portalExPosition)) {
                        // If the exit direction has not been visited and is in range since we didn't check it for this
                        // new position
                        tmpPosition = portalExPosition;
                    }
                }

                // Making sure we don't queue the same position more than once
                if (!queue.contains(tmpPosition)) {
                    queue.add(tmpPosition);
                }
            }
            // Add in the adjacent position so we can update the new distance
            if (visited.containsKey(tmpPosition)) {
                adjacent.add(tmpPosition);
            }
            
        }

        return adjacent;
    }

    // Get the position the player needs to move
    private Position getMoving(HashMap<Position, Position> parent, Position playerPosition, Position currPosition) {
        List<Position> path = new ArrayList<Position>();
        Position retPosition = playerPosition;
        Position tmpPosition = playerPosition;
        
        if (parent.get(playerPosition) == null) {
            // There is no path to player
            return currPosition;
        }

        while (!tmpPosition.equals(currPosition)) {
            // Going through the children and the parents
            retPosition = tmpPosition;
            path.add(retPosition);
            tmpPosition = parent.get(tmpPosition);
        }
        
        path.add(currPosition);

        if (path.size() == 1) {
            // Next to Player
            return path.get(0);
        } else {
            // Not next to player
            return path.get(path.size() - 2);
        }
    }

    // Checks if there is a portal it can teleport through at <pos>
    private Entity checkPortal(Position pos) {
        Entity destinationEntity = Entity.findEntityAtPosition(pos);

        // When it steps onto portal, teleport to next portal
        if (destinationEntity != null && destinationEntity.getType().equals("portal")) {
            Portal portalEntry = (Portal) destinationEntity;
            Portal portalExit = portalEntry.getOtherPortal();
            if (portalExit == null) {
                return null;
            }
            if (!portalExit.isBlocked()) {
                // Can teleport
                return destinationEntity;
            }
        }
        return null;
    }
}
