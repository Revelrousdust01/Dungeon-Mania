package dungeonmania.Entity;

import com.google.gson.*;

import dungeonmania.util.Position;
import dungeonmania.Entity.StaticEntity.*;
import dungeonmania.Entity.CollectibleEntity.*;
import dungeonmania.Entity.MovingEntity.*;
import dungeonmania.DungeonManiaController;

public class EntityFactory {
    /**
     * Create entities and automatically assigning isInteractable and id
     * @param entityType
     * @param position
     * @return
     */
    static public Entity createEntity(String entityType, Position position) {
        String id = DungeonManiaController.getCurrDungeon().getNewId();
        switch (entityType) {
            case "wall":
                return new Wall(false, position, id, entityType);
            case "exit":
                return new Exit(false, position, id, entityType);
            case "boulder":
                return new Boulder(false, position, id, entityType);
            case "switch":
                return new FloorSwitch(false, position, id, entityType);
            case "zombie_toast_spawner":
                return new ZombieToastSpawner(true, position, id, entityType);
            case "treasure":
                return new Treasure(false, position, id, entityType);
            case "invisibility_potion":
                return new InvisibilityPotion(false, position, id, entityType);
            case "invincibility_potion":
                return new InvincibilityPotion(false, position, id, entityType);
            case "wood":
                return new Wood(false, position, id, entityType);
            case "arrow":
                return new Arrows(false, position, id, entityType);
            case "bomb":
                return new Bomb(false, position, id, entityType);
            case "sword":
                return new Sword(false, position, id, entityType);
            case "player":
                return new Player(false, position, id, entityType);
            case "mercenary":
                return new Mercenary(true, position, id, entityType);
            case "spider":
                return new Spider(false, position, id, entityType);
            case "zombie_toast":
                return new MobEntity(false, position, id, entityType);
            case "hydra":
                return new MobEntity(false, position, id, entityType);
            case "sun_stone":
                return new SunStone(false, position, id, entityType);
            case "assassin":
                return new Assassin(true, position, id, entityType);
            case "swamp_tile":
                return new Swamp(false, position, id, entityType);
            case "time_turner":
                return new TimeTurner(false, position, id, entityType);
            case "wire":
                return new Wire(false, position, id, entityType);
            case "time_travelling_portal":
                return new TimeTravelPortal(false, position, id, entityType);

        }
        return null;
    }

    /**
     * Creating entities that has an extra field
     * @param entityType
     * @param position
     * @param additional - the extra field
     * @return
     */
    static public Entity createEntity(String entityType, Position position, String additional) {
        String id = DungeonManiaController.getCurrDungeon().getNewId();
        switch (entityType) {
            case "door":
                return new Door(false, position, id, entityType, Integer.parseInt(additional));
            case "portal":
                return new Portal(false, position, id, entityType, additional);
            case "key":
                return new Key(false, position, id, entityType, Integer.parseInt(additional)); 
            case "light_bulb":
                return new LightBulb(position, id, entityType, additional);
        }
        return null;
    }

    static public Entity createEntity(String entityType, Position position, String additional1, String additional2) {
        String id = DungeonManiaController.getCurrDungeon().getNewId();
        switch (entityType) {
            case "switch_door":
                return new SwitchDoor(false, position, id, entityType, Integer.parseInt(additional1), additional2);
               
        }
        return null;
    }

    /**
     * Create entities based on json file
     * @param entityJson
     * @param isSave
     * @return
     */
    static public Entity createEntity(JsonObject entityJson) {
        String entityType = entityJson.get("type").getAsString();
        int x = entityJson.get("x").getAsInt();
        int y = entityJson.get("y").getAsInt();
        Position p = new Position(x, y);
        
        // Dealing with entities with additional fields
        switch(entityType) {
            case "door":
                return createEntity(entityType, p, entityJson.get("key").getAsString());
            case "portal":
                return createEntity(entityType, p, entityJson.get("colour").getAsString());
            case "key":
                return createEntity(entityType, p, entityJson.get("key").getAsString());
            case "switch_door":
                return createEntity(entityType, p, entityJson.get("key").getAsString(), entityJson.get("logic").getAsString());
            case "light_bulb":
                return createEntity(entityType, p, entityJson.get("logic").getAsString());
        }
        return createEntity(entityType, p);

    }
}
