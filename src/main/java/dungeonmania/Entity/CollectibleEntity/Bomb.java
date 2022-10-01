package dungeonmania.Entity.CollectibleEntity;

import java.util.List;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.RigidEntity;
import dungeonmania.util.Position;
import dungeonmania.Entity.Entity;

public class Bomb extends CollectibleEntity implements RigidEntity {
    private boolean placed;

    public Bomb (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.placed = false;
    }

    public void explode() {
        for (Entity e : getEntitiesInExplosionRange()) {
            getAllEntities().remove(e);
        }
        
    }

    public void use(Position position) {
        placed = true;
        this.position = position;
        getAllEntities().add(this);

        // Check if there is an switch in adjacent position
        boolean triggeredSwitch = false;
        for (Entity e : getAllEntities()) {
            if (Position.isAdjacent(e.getPosition(), this.position) && e.getType().equals("switch")) {
                FloorSwitch s = (FloorSwitch) e;
                s.getBombsInRange().add(this);

                if (s.isTriggered()) { triggeredSwitch = true; }
            }
        }
        
        if (triggeredSwitch) {
            explode();
        }

        Entity.getPlayer().removeFromInventory(this);
    }

    public boolean isPassable() {
        return !placed;
    }
    
    public List<Entity> getEntitiesInExplosionRange() {
        int bomb_radius = DungeonManiaController.getCurrDungeon().getConfig("bomb_radius");
        return Entity.getEntitiesInRange(this.position, bomb_radius);

    }

}

