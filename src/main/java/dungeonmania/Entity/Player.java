package dungeonmania.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Iterator;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.BuildableEntity.Sceptre;
import dungeonmania.Entity.CollectibleEntity.*;
import dungeonmania.Entity.MovingEntity.Assassin;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.util.Position;
import dungeonmania.util.Direction;
import dungeonmania.Entity.StaticEntity.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.ItemResponse;

public class Player extends Entity implements Subject {
    protected ArrayList<Entity> inventory;
    private ArrayList<Potion> potionQueue;
    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private Position prevPosition;

    public Player (boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.inventory = new ArrayList<Entity>();
        this.potionQueue = new ArrayList<Potion>();
        this.prevPosition = position;
    }

    public Player (boolean isInteractable, Position position, String id, String type, 
                    ArrayList<Entity> inventory, ArrayList<Potion> potionQueue) {
        super(isInteractable, position, id, type);
        this.inventory = inventory;
        this.potionQueue = potionQueue;
        this.prevPosition = position;
    }

    public void move(Direction direction) {
        // Check the square you're moving into
        Position destination = this.position.translateBy(direction);
        Entity destinationEntity = findEntityAtPosition(destination);
        updateObserverList();
        
        if (destinationEntity == null) {
            prevPosition = position;
            this.position = destination;
            this.notifyObservers();
            return;
        }
        this.notifyObservers();
        
        // Player collisions - walls, boulders, locked doors
        if (destinationEntity instanceof RigidEntity) {
            RigidEntity rEntity = (RigidEntity) destinationEntity;
            if (!rEntity.isPassable() && !(rEntity instanceof Boulder || rEntity instanceof Door || rEntity instanceof SwitchDoor) ) { return; }
        }

        // Collecting collectible entities
        if (destinationEntity instanceof CollectibleEntity) {
            CollectibleEntity cEntity = (CollectibleEntity) destinationEntity;
            addToInventory(cEntity);
        }

        // Player interactions with other entities
        switch (destinationEntity.type) {
            case("boulder"):
                Boulder b = (Boulder) destinationEntity;
                if (!b.push(direction)) { return; } 
                break;
            case("door"):
                Door d = (Door) destinationEntity;
                if (!d.isPassable()) {
                    // Note the player will only ever have one key
                    SunStone s = (SunStone) inventorySearch("sun_stone");
                    Key k = (Key) inventorySearch("key");
                    if (s != null) {
                        d.unlock(s);
                    } else if (d.unlock(k)) {
                        inventory.remove(k);
                    } else {
                        return;
                    }
                }
                break;
            case("switch_door"):
                SwitchDoor sd = (SwitchDoor) destinationEntity;
                if (!sd.isPassable()) {
                    // Note the player will only ever have one key
                    SunStone s = (SunStone) inventorySearch("sun_stone");
                    Key k = (Key) inventorySearch("key");
                    if (s != null) {
                        sd.unlock(s);
                    } else if (sd.unlock(k)) {
                        inventory.remove(k);
                    } else {
                        return;
                    }
                }
                break;
            case("portal"):
                Portal portalEntry = (Portal) destinationEntity;
                Portal portalExit = portalEntry.getOtherPortal();
                if (portalExit == null) {break;}
                if (!portalExit.isBlocked()) {
                    destination = portalExit.getPosition();
                    this.position = destination;
                    move(direction);
                    return;
                }
                break;
            case("time_travelling_portal"):
                if (!position.equals(destination)) {
                    // Moved into time travel portal
                    DungeonManiaController.setCurrDungeon(DungeonManiaController.getCurrDungeon().loadDungeon(30));
                    DungeonManiaController.setRewinded(true);
                }

        }
        prevPosition = position;
        this.position = destination;
        this.notifyObservers();
    }

    private Entity inventorySearch(String typeToFind) {
        return inventory.stream().filter(item -> item.getType().equals(typeToFind)).findFirst().orElse(null);
    }

    public ArrayList<Entity> getInventory() {
        return inventory;
    }

    public void removeFromInventory(Entity entity) {
        Iterator<Entity> iter = inventory.iterator();

        while (iter.hasNext()) {
            Entity e = iter.next();
            if (e == entity) iter.remove();
        }
    }

    public void addToInventory(Entity entity) {
        if (entity instanceof Key && getInventory().stream().filter(item -> item.getType().equals("key")).count() != 0) {
            // do nothing
        } else {
            removeEntityFromMap(entity);
            this.getInventory().add(entity);
        }
    }

    public void removePotionFromQueue(Potion potion) {
        potionQueue.remove(potion);
        this.notifyObservers();
    }

    @Override
    public Potion getCurrPotion() {
        if (potionQueue.size() == 0) {
            return null;
        } else {
            return potionQueue.get(0);
        }
    }

    public void addPotionToQueue(Potion potion) {
        potionQueue.add(potion);
        this.notifyObservers();
    }

    public List<ItemResponse> createItemResponseList() {
        ArrayList<ItemResponse> itemResponseList = new ArrayList<ItemResponse>();

        for (Entity e : inventory) {
            ItemResponse iResponse = new ItemResponse(e.id, e.type);
            itemResponseList.add(iResponse);
        }

        return itemResponseList;
    }

    public void use(String id) throws IllegalArgumentException, InvalidActionException {
        Entity item = getFromInv(id);

        if (item == null) { throw new InvalidActionException(id + " does not exist in inventory"); }

        switch (item.type) {
            case("invisibility_potion"):
                InvisibilityPotion invisP = (InvisibilityPotion) item;
                invisP.use();
                break;

            case("invincibility_potion"):
                InvincibilityPotion invincP = (InvincibilityPotion) item;
                invincP.use();
                break;

            case("bomb"):
                Bomb b = (Bomb) item;
                b.use(position);
                break;
            case("sceptre"):
                Sceptre sceptre = (Sceptre) item;
                sceptre.use();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Entity getFromInv(String id) {
        for (Entity e : inventory) {
            if (e.id.equals(id)) {
                return e;
            }
        }
        return null;
    }

    public void interact(String id) throws IllegalArgumentException, InvalidActionException {
        Entity interactable = findEntity(id);
        
        if (interactable == null) { throw new IllegalArgumentException(id + " does not exist"); }

        switch (interactable.type) {
            case("mercenary"):
                Mercenary mercenary = (Mercenary) interactable;
                // Check if already bribed
                if (mercenary.isInteractable) {
                    // Check in range
                    if (!getEntitiesInRange(position, DungeonManiaController.getCurrDungeon().getConfig("bribe_radius")).contains(interactable)) {
                        throw new InvalidActionException("Out of range");
                    }
                    
                    // Check if the player has enough to bribe
                    int bribeAmount = DungeonManiaController.getCurrDungeon().getConfig("bribe_amount");
                    List<Entity> playerTreasure = inventory.stream().filter(item -> item.getType().equals("treasure")).limit(bribeAmount).collect(Collectors.toList());
                    if (playerTreasure.size() < bribeAmount) {
                        throw new InvalidActionException("Not enough gold");
                    }

                    // Modify merc
                    mercenary.bribe();

                    // Charge player
                    inventory.removeAll(playerTreasure);
                }

                break;
            case("assassin"):
                Assassin assassin = (Assassin) interactable;
                // Check if already bribed
                if (assassin.isInteractable) {
                    // Check in range
                    if (!getEntitiesInRange(position, DungeonManiaController.getCurrDungeon().getConfig("bribe_radius")).contains(interactable)) {
                        throw new InvalidActionException("Out of range");
                    }
                    
                    // Check if the player has enough to bribe
                    int bribeAmount = DungeonManiaController.getCurrDungeon().getConfig("assassin_bribe_amount");
                    List<Entity> playerTreasure = inventory.stream().filter(item -> item.getType().equals("treasure")).limit(bribeAmount).collect(Collectors.toList());
                    if (playerTreasure.size() < bribeAmount) {
                        throw new InvalidActionException("Not enough gold");
                    }

                    // Modify asssassin
                    assassin.bribe();

                    // Charge player
                    inventory.removeAll(playerTreasure);
                }

                break;
            case("zombie_toast_spawner"):
                ZombieToastSpawner spawner = (ZombieToastSpawner) interactable;
                ZombieToastSpawner.destroyZombieToastSpawner(spawner, this);
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    public void copyInventory() {
        ArrayList<Entity> copyInventory = new ArrayList<>();
        for (Entity entity: inventory) {
            Entity copyEntity = null;
            try {
                copyEntity = (Entity) entity.clone();
            } catch (Exception e) {

            }
            if (copyEntity.getPosition() != null) {
                copyEntity.setPosition(new Position(copyEntity.getPosition().getX(), copyEntity.getPosition().getY()));
            }
            copyEntity.setId(copyEntity.getId());
            copyInventory.add(copyEntity);
        }
        this.inventory = copyInventory;
    }

    /**
     * Needs to be called at every tick to ensure potion observers is up to date
     * with new spawns or death
     */
    @Override
    public void updateObserverList() {
        ArrayList<Observer> updatedObservers = new ArrayList<Observer>();

        for (Entity e : getAllEntities()) {
            if (e instanceof Observer) {
                updatedObservers.add((Observer) e);
            }
        }
        this.observers = updatedObservers;
    }

    /**
     * Notify observers
     */
    @Override
    public void notifyObservers() {
        for (Observer e : observers) {
            e.update(this);
        }
    }
    
    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Position getPrevPosition() {
        return prevPosition;
    }
}
