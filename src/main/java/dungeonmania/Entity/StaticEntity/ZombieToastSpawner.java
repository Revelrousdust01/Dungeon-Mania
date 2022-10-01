package dungeonmania.Entity.StaticEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.EntityFactory;
import dungeonmania.Entity.Player;
import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends StaticEntity {
    public ZombieToastSpawner(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    public static void destroyZombieToastSpawner(ZombieToastSpawner entity, Player playerKind) throws InvalidActionException {
        List<Position> zombieToastSpawnerAdjacentPositions = new ArrayList<Position>();
        zombieToastSpawnerAdjacentPositions = entity.getPosition().getAdjacentPositions();
        zombieToastSpawnerAdjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()-1 && x.getY() == entity.getPosition().getY()+1);
        zombieToastSpawnerAdjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()+1 && x.getY() == entity.getPosition().getY()+1);
        zombieToastSpawnerAdjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()-1 && x.getY() == entity.getPosition().getY()-1);
        zombieToastSpawnerAdjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()+1 && x.getY() == entity.getPosition().getY()-1);
        Boolean swordFind = playerKind.getInventory().stream()
                .anyMatch(item -> item.getType().equals("sword"));
        Boolean bowFind = playerKind.getInventory().stream()
                .anyMatch(item -> item.getType().equals("bow"));
        if (!swordFind && !bowFind) {
            throw new InvalidActionException("Spawner can't be destroyed without a weapon");
        }
        for (Position position : zombieToastSpawnerAdjacentPositions) {
            if (playerKind.getPosition().equals(position)) {
                removeEntityFromMap(entity);
                return;
            }
        }
        throw new InvalidActionException("Player not near spawner");

    }

    public static void spawnZombie() {
        List<Entity> allEntities = getAllEntities();
        List<Entity> zombieToastSpawners = entityType("zombie_toast_spawner");
        int spawnRate = DungeonManiaController.getCurrDungeon().getConfig("zombie_spawn_rate");
        int ticker = DungeonManiaController.getCurrDungeon().getZombieTicker();
        if (spawnRate == ticker) {
            for (Entity zts : zombieToastSpawners) {
                List<Position> adjacentPositions = zts.getPosition().getAdjacentPositions();
                adjacentPositions.removeIf(x-> x.getX() == zts.getPosition().getX()-1 && x.getY() == zts.getPosition().getY()+1);
                adjacentPositions.removeIf(x-> x.getX() == zts.getPosition().getX()+1 && x.getY() == zts.getPosition().getY()+1);
                adjacentPositions.removeIf(x-> x.getX() == zts.getPosition().getX()-1 && x.getY() == zts.getPosition().getY()-1);
                adjacentPositions.removeIf(x-> x.getX() == zts.getPosition().getX()+1 && x.getY() == zts.getPosition().getY()-1);

                for (Position directPosition : adjacentPositions) {
                    List<Entity> mobsOnPosition = allEntities.stream()
                            .filter(e -> e.getPosition().equals(directPosition))
                            .collect(Collectors.toList());
                    if (mobsOnPosition.isEmpty()) {
                        EntityFactory.createEntity("zombie_toast", directPosition.asLayer(3));
                        break;
                    }
                }
            }
            DungeonManiaController.getCurrDungeon().setZombieTicker(1);
        } else {
            DungeonManiaController.getCurrDungeon().setZombieTicker(ticker + 1);
        }
    }
}
