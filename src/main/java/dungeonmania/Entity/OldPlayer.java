package dungeonmania.Entity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import dungeonmania.DungeonManiaController;
import dungeonmania.Ticks;
import dungeonmania.Entity.CollectibleEntity.CollectibleEntity;
import dungeonmania.Entity.MovingEntity.Enemy;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class OldPlayer extends Player implements Enemy {
    private Queue<Ticks> toTick;
    
    public OldPlayer(boolean isInteractable, Position position, String id, String type, Queue<Ticks> ticksToAdd, ArrayList<Entity> inv) {
        super(isInteractable, new Position(position.getX(), position.getY()), id, type);
        this.inventory = inv;
        this.toTick = new LinkedList<>();
        this.toTick.addAll(ticksToAdd);
    }

    public int ticksLeft() {
        return toTick.size();
    }

    public void tick() {
        Ticks toDo = toTick.remove();
        switch (toDo.getTickType()) {
            case "tick":
                if (toDo.getArguement() instanceof String) {
                    String param = (String) toDo.getArguement();
                    // String itemId = typeToId(param);
                    try {
                        this.use(param);
                    } catch (Exception e) {
                        // nothing
                    }
                } else if (toDo.getArguement() instanceof Direction) {
                    Direction param = (Direction) toDo.getArguement();
                    this.move(param);
                }
                break;
            case "interact":
                String param = (String) toDo.getArguement();
                try {
                    this.interact(param);
                } catch (Exception e) {
                    // nothing
                }
                break;
            case "build":
                String params = (String) toDo.getArguement();
                break;
        }
    }

    @Override
    public void move(Direction direction) {
        Position destination = this.position.translateBy(direction);
        Entity destinationEntity = findEntityAtPosition(destination);
        if (destinationEntity != null && destinationEntity instanceof CollectibleEntity) {
            CollectibleEntity cEntity = (CollectibleEntity) destinationEntity;
            addToInventory(cEntity);
        }
        this.position = destination;
    }

    private String typeToId(String type) {
        for(Entity e: Entity.getPlayer().getInventory()) {
            if (e.type.equals(type)) {
                return e.getId();
            }
        }
        return null;
    }

    @Override
    public int getEnemyAttack() {
        return DungeonManiaController.getCurrDungeon().getConfig("player_attack");
    }

    @Override
    public int getEnemyHealth() {
        return DungeonManiaController.getCurrDungeon().getConfig("player_health");
    }
}
