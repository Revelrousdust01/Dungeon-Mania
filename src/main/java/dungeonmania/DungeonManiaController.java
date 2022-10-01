package dungeonmania;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.Entity.Player;
import dungeonmania.response.models.*;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import com.google.gson.*;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.*;
import dungeonmania.Entity.StaticEntity.*;
import dungeonmania.Entity.BuildableEntity.BuildableEntity;
import dungeonmania.Entity.BuildableEntity.Sceptre;
import dungeonmania.Battle.Battle;
import dungeonmania.Entity.CollectibleEntity.Potion;

public class DungeonManiaController {
    static private int dungeonCounter = 0;
    static private Dungeon currDungeon;
    static private Boolean rewinded = false;

    public String getSkin() {
        return "custom";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        String dungeonId = "d" + Integer.toString(dungeonCounter);

        // Get configuration settings from config file
        String configJsonString;
        try {
            configJsonString = FileLoader.loadResourceFile("configs/" + configName + ".json");
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        Config config = new Config(JsonParser.parseString(configJsonString).getAsJsonObject());

        currDungeon = new Dungeon(dungeonId, dungeonName, config);

        // Get map layout from dungeon file
        String dungeonJsonString;
        try {
            dungeonJsonString = FileLoader.loadResourceFile("dungeons/" + dungeonName + ".json");
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        JsonObject dungeonJsonObj = JsonParser.parseString(dungeonJsonString).getAsJsonObject();
        JsonArray entities = dungeonJsonObj.getAsJsonArray("entities");
        currDungeon.createDungeonMap(entities);

        // Get goals from dungeon file
        JsonObject goalCondition = dungeonJsonObj.getAsJsonObject("goal-condition");
        currDungeon.readGoals(goalCondition);

        // Other initialisations
        Spider.createInitialSpiderSpawn();
        
        return currDungeon.createDungeonResponseFirstTick();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return currDungeon.createDungeonResponse();
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        try {
            Entity.getPlayer().use(itemUsedId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot use item that is not a bomb nor potion");
        } catch (InvalidActionException e) {
            throw new InvalidActionException("Item does not exist in inventory");
        }

        // Other stuff ticking
        Entity.moveEntities();
        ZombieToastSpawner.spawnZombie();
        Spider.spawnSpider();
        currDungeon.addDungeon(currDungeon);
        currDungeon.tickOldPlayer();
        currDungeon.addTick(new Ticks("tick", itemUsedId));
        sceptreTick();
        battleCheck();
        potionTick();

        return currDungeon.createDungeonResponse();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        Entity.getPlayer().move(movementDirection);
        battleCheck();
        if (Entity.getPlayer() == null) {
            return currDungeon.createDungeonResponse();
        }

        // We ignore this when we manually rewinded
        if (rewinded) {
            // Player went through portal
            currDungeon.addDungeon(currDungeon);
            currDungeon.addTick(new Ticks("tick", movementDirection));
            rewinded = false;
            return currDungeon.createDungeonResponse();    
        }

        // Other stuff ticking
        Entity.moveEntities();
        ZombieToastSpawner.spawnZombie();
        Spider.spawnSpider();
        currDungeon.addDungeon(currDungeon);
        currDungeon.addTick(new Ticks("tick", movementDirection));
        currDungeon.tickOldPlayer();
        sceptreTick();
        battleCheck();
        DungeonManiaController.getCurrDungeon().setWireTicker();
        potionTick();
        
        return currDungeon.createDungeonResponse();

    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        try {
            BuildableEntity.build(buildable);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Item is not buildable");
        } catch (InvalidActionException e) {
            throw new InvalidActionException("Insufficient items");
        }
        currDungeon.addDungeon(currDungeon);
        currDungeon.addTick(new Ticks("build", buildable));
        currDungeon.tickOldPlayer();
        return currDungeon.createDungeonResponse();
    }
    
    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity.getPlayer().interact(entityId);
        currDungeon.addDungeon(currDungeon);
        currDungeon.addTick(new Ticks("interact", entityId));
        currDungeon.tickOldPlayer();
        return currDungeon.createDungeonResponse();
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        String dungeonId = "d" + Integer.toString(dungeonCounter);

        // Get configuration settings from config file
        String configJsonString;
        try {
            configJsonString = FileLoader.loadResourceFile("configs/" + configName + ".json");
        } catch (Exception e) {
            throw new IllegalArgumentException();
        } 
        Config config = new Config(JsonParser.parseString(configJsonString).getAsJsonObject());

        currDungeon = new DungeonGenerator(dungeonId, "maze", config);
        DungeonGenerator currGenerator = (DungeonGenerator) currDungeon;
        currGenerator.generateMaze(xStart, yStart, xEnd, yEnd);

        Spider.createInitialSpiderSpawn();

        return currDungeon.createDungeonResponseFirstTick();
    }

    static public Dungeon getCurrDungeon() {
        return currDungeon;
    }

    public static void setCurrDungeon(Dungeon currDungeon) {
        DungeonManiaController.currDungeon = currDungeon;
    }

    public static void setRewinded(Boolean rewinded) {
        DungeonManiaController.rewinded = rewinded;
    }

    static public void potionTick() {
        if (Entity.getPlayer() != null) {
            Entity.getPlayer().updateObserverList();
            Potion.decreaseDuration();
        }
    }
    
    static public void sceptreTick() {
        Sceptre.decreaseDuration();
    }

    static public void battleCheck() {
        Player player = Player.getPlayer();
        Position playerPos = player.getPosition();
        for(Entity entity : Entity.getAllEntities()) {
            if(entity.getPosition().equals(playerPos) && (entity instanceof Enemy)) {
                // TODO: Change condition for assassins

                // Check if the entity is bribed or not
                if (entity.getType().equals("mercenary")) {
                    // Check if mercenary is bribed
                    Mercenary merc = (Mercenary) entity;
                    if (!merc.isInteractable()) {
                        // Bribed mercenary
                        break;
                    }
                } else if (entity.getType().equals("assassin")) {
                    // Check if assassin is bribed
                    Assassin assa = (Assassin) entity;
                    if (!assa.isInteractable()) {
                        // Bribed assassin
                        break;
                    }
                } else if (entity.getType().equals("older_player")) {
                    // check inventory for player
                    if (player.getInventory().stream().filter(ent -> ent.getType().equals("sun_stone") || ent.getType().equals("midnight_armour")).count() > 0) {
                        // got items
                        break;
                    }
                }

                // player cant battle while invisible
                if(player.getCurrPotion() == null || !player.getCurrPotion().getType().equals("invisibility_potion")) {
                    Battle battle = new Battle();

                    List<Entity> weapons = player.getInventory().stream().filter(
                            e -> e.getType().equals("sword") || e.getType().equals("bow")
                                    || e.getType().equals("shield"))
                            .collect(Collectors.toList());

                    getCurrDungeon().addBattle(battle.battle(entity, weapons));
                }
                break;
            }
        }
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        currDungeon.saveDungeon(name);

        return currDungeon.createDungeonResponse();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        FileInputStream file = null;
        ObjectInputStream in = null;
        int tries = 0;
        while (file == null) {
            try {   
                // Reading the object from a file
                if (!name.contains(".ser")) {
                    name = name + ".ser";
                }
                file = new FileInputStream(FileLoader.getPathForNewFile("saved", name));
                in = new ObjectInputStream(file);

                // Method for deserialization of object
                currDungeon = (Dungeon) in.readObject();
                    
                in.close();
                file.close();
            } catch (Exception e) {
                // Retry loading in case the file isn't finished writing yet
                tries++;
                if (tries == 5) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        return currDungeon.createDungeonResponse();
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        try {
            String path2 = FileLoader.getPathForNewFile("saved", "");
            File dir = new File(path2);
            return Arrays.asList(dir.list());
        } catch (Exception e) {

        }
        return Collections.emptyList();
        
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) {
        if (ticks != 1 && ticks != 5 && ticks != 30) {
            return currDungeon.createDungeonResponse();
        }
        DungeonManiaController.currDungeon = currDungeon.loadDungeon(ticks);
        return currDungeon.createDungeonResponse();
    }
}
