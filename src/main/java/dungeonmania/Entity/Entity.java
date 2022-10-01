package dungeonmania.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;
import java.io.Serializable;

import dungeonmania.util.Position;
import dungeonmania.response.models.EntityResponse;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.CollectibleEntity.CollectibleEntity;
import dungeonmania.Entity.MovingEntity.MovingEntity;
import dungeonmania.Entity.StaticEntity.*;

public abstract class Entity implements Serializable, Cloneable {
    protected boolean isInteractable;
    protected Position position;
    protected String id;
    protected String type;

    public Entity(boolean isInteractable, Position position, String id, String type) {
        this.isInteractable = isInteractable;
        this.position = position;
        this.id = id;
        this.type = type;
        getAllEntities().add(this);
    }

    public Entity(String id, String type) {
        this.id = id;
        this.type = type;
    }
    
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getId() {
        return this.id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    static public ArrayList<Entity> getAllEntities() {
        return DungeonManiaController.getCurrDungeon().getAllEntities();
    }

    static public Player getPlayer() {
        List<Entity> allEntities = getAllEntities();

        for (Entity e : allEntities) {
            if (e.getType().equals("player")) {
                return (Player) e;
            }
        }
        return null;
    }

    static public int allEntitySize() {
        return getAllEntities().size();
    }

    static public Entity findEntity(String id) {
        List<Entity> match = getAllEntities().stream().filter(e -> e.id.equals(id)).collect(Collectors.toList());
        if (match.size() < 1) {
            return null;
        }
        return match.get(0);
    }

    static public List<Entity> entityType(String type) {
        return getAllEntities().stream().filter(e -> e.type.equals(type)).collect(Collectors.toList());

    }

    /**
     * Determine whether there is an entity at the given position and retrieves it
     * @param position
     * @return Entity found at the given position or null if nothing is there
     */
    static public Entity findEntityAtPosition(Position position) {
        List<Entity> allEntities = getAllEntities();

        Entity foundEntity = null;
        for (Entity e : allEntities) {
            if (e.position.equals(position) && !(foundEntity instanceof RigidEntity) && !(foundEntity instanceof CollectibleEntity)) {
                foundEntity = e;
            }
            
        }
        return foundEntity;
    }

    /**
     * Determine whether there is an entity at the given position and retrieves it
     * Method used exclusively for logic checking
     * @param position
     * @return Entity found at the given position or null if nothing is there
     */
    static public Entity findEntityAtPositionSwitch(Position position) {
        List<Entity> allEntities = getAllEntities();

        Entity foundEntity = null;
        for (Entity e : allEntities) {
            if (e.position.equals(position) && !(foundEntity instanceof FloorSwitch)) {
                foundEntity = e;
            }
            
        }
        return foundEntity;
    }

    /**
     * Find all the entities except player in 
     * the (2*range + 1) * (2*range + 1) square range from the center
     * @param center
     * @param range - radius from the center. 
     * @return list of entities in range
     */
    static public List<Entity> getEntitiesInRange(Position center, int range) {
        ArrayList<Entity> entitiesInRange = new ArrayList<Entity>();
        
        int cX = center.getX();
        int cY = center.getY();
        for (Entity e : getAllEntities()) {
            int eX = e.getPosition().getX();
            int eY = e.getPosition().getY();

            if (eX >= cX - range && eX <= cX + range && 
                eY >= cY - range && eY <= cY + range &&
                !(e instanceof Player)) {
                
                entitiesInRange.add(e);
            }
        }

        return entitiesInRange;

    }

    static public void removeEntityFromMap(Entity entity) {
        Iterator<Entity> iter = getAllEntities().iterator();

        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e == entity) iter.remove();
        }
    }

    static public List<EntityResponse> createEntityResponseList() {
        ArrayList<EntityResponse> entityResponseList = new ArrayList<EntityResponse>();
        List<Entity> allEntities = getAllEntities();

        EntityResponse eResponse;
        for (Entity e : allEntities) {
            // Condition statement deals with aesthetics on frontend
            if (e instanceof Portal) {
                Portal p = (Portal) e;
                eResponse = new EntityResponse(p.id, p.type + "_" + p.getColour(), p.position, p.isInteractable);
            } else if (e instanceof MovingEntity || e instanceof Player) {
                eResponse = new EntityResponse(e.id, e.type, e.position.asLayer(5), e.isInteractable);
            } else if (e instanceof Door && ((Door) e).isPassable()) {
                eResponse = new EntityResponse(e.id, e.type + "_open", e.position, e.isInteractable);
            } else if (e instanceof Boulder) {
                eResponse = new EntityResponse(e.id, e.type, e.position.asLayer(1), e.isInteractable);
            } else {
                eResponse = new EntityResponse(e.id, e.type, e.position, e.isInteractable);
            }

            entityResponseList.add(eResponse);
        }

        return entityResponseList;
    }

    public static void moveEntities() {
        List<String> movingEntities = new ArrayList<>();
        movingEntities.add("zombie_toast");
        movingEntities.add("spider");
        movingEntities.add("mercenary");
        movingEntities.add("assassin");
        movingEntities.add("hydra");
        for (Entity e: getAllEntities()) {
            if (movingEntities.contains(e.getType())) {
                MovingEntity movingEntity = (MovingEntity) e;
                movingEntity.performMove();
            }
        }
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public void setInteractable(boolean isInteractable) {
        this.isInteractable = isInteractable;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
