package dungeonmania;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gson.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import dungeonmania.Entity.*;
import dungeonmania.Entity.BuildableEntity.BuildableEntity;
import dungeonmania.response.models.*;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;
import dungeonmania.Entity.StaticEntity.*;
import dungeonmania.Goals.*;


public class Dungeon implements Serializable, Cloneable {
    private int idCounter = 0;
    private String dungeonId;
    private String dungeonName;
    private Config config;
    private ArrayList<BattleResponse> battles;
    private ArrayList <Entity> allEntities;
    protected GoalComponent dungeonGoal;
    private ArrayList<Swamp> swamps;
    private Queue<Dungeon> prevDungeons;
    private Queue<Ticks> prevTicks;
    private int spiderTicker;
    private int zombieTicker;
    private int wireTicker;
    
    

    public Dungeon (String dungeonId, String dungeonName, Config config) {
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
        this.config = config;
        this.battles = new ArrayList<BattleResponse>();
        this.allEntities = new ArrayList<Entity>();
        this.swamps = new ArrayList<Swamp>();
        this.prevDungeons = new LinkedList<Dungeon>();
        this.prevTicks = new LinkedList<>();
        this.spiderTicker = 1;
        this.zombieTicker = 1;
        this.idCounter = 0;
        this.wireTicker = 1;
    }

    public int getConfig(String field) {
        return config.getConfig(field);
    }

    public int getSpiderTicker(){
        return spiderTicker;
    }

    public int getZombieTicker(){
        return zombieTicker;
    }

    public int getWireTicker() {
        return wireTicker;
    }


    public void setSpiderTicker(int ticker){
        this.spiderTicker = ticker;
    }

    public void setWireTicker(){
        this.wireTicker++;
    }

    public void setZombieTicker(int ticker){
        this.zombieTicker = ticker;
    }

    public List<BattleResponse> getBattles() {
        return this.battles;
    }

    public void addBattle(BattleResponse battle) {
        this.battles.add(battle);
    }

    public ArrayList<Entity> getAllEntities() {
        return allEntities;
    }

    public ArrayList<Swamp> getSwamps() {
        return swamps;
    }

    public void setAllEntities(ArrayList<Entity> allEntities) {
        this.allEntities = allEntities;
    }

    public String getDungeonId() {
        return dungeonId;
    }

    public Queue<Dungeon> getPrevDungeons() {
        return prevDungeons;
    }

    public void createDungeonMap(JsonArray entities) {
        for (JsonElement entity : entities) {
            JsonObject entityObj = entity.getAsJsonObject();
            EntityFactory.createEntity(entityObj);
        }
        
    }

    public void readGoals(JsonObject goalCondition) {
        if (goalCondition.has("subgoals")) {
            // Create the root 
            dungeonGoal = new GoalComposite("");
            GoalComposite composite = (GoalComposite) dungeonGoal;
            // Create rest of the tree
            createGoalComponent(goalCondition, composite);

        } else {
            dungeonGoal = new GoalLeaf(goalCondition.get("goal").getAsString());
        }
        
    }

    /**
     * Recursively create goal component tree like structure
     * @param goalCondition - json object of form {"goal":"condition"}
     * @param composite - parent goal
     */
    public void createGoalComponent(JsonObject goalCondition, GoalComposite composite) {
        String goal = goalCondition.get("goal").getAsString();

        if (goal.equals("AND") || goal.equals("OR")) {
            // Create the goal composite with the operator, and link it to the parent composite
            GoalComposite parent = new GoalComposite(goal);
            composite.addSubgoal(parent);

            JsonObject subgoal1 = goalCondition.get("subgoals").getAsJsonArray().get(0).getAsJsonObject();
            createGoalComponent(subgoal1, parent);
            JsonObject subgoal2 = goalCondition.get("subgoals").getAsJsonArray().get(1).getAsJsonObject();
            createGoalComponent(subgoal2, parent);

        } else {
            // Create the goal leaf with the goal strategy
            composite.addSubgoal(goal);

        }

    }

    public DungeonResponse createDungeonResponse() {
        List<EntityResponse> entities = Entity.createEntityResponseList();
        String goals = dungeonGoal.prettyPrint();
        
        List<ItemResponse> inventory = new ArrayList<ItemResponse>();
        List<String> buildables = new ArrayList<String>();

        if (Entity.getPlayer() != null) {
            inventory = Entity.getPlayer().createItemResponseList();
            buildables = BuildableEntity.getBuildables();
        } 
        return new DungeonResponse(dungeonId, dungeonName, entities, inventory, battles, buildables, goals);
    }

    /**
     * Used exclusively for newGame() where goal cannot be evaluated on first tick
     */
    public DungeonResponse createDungeonResponseFirstTick() {
        List<EntityResponse> entities = Entity.createEntityResponseList();
        String goals = dungeonGoal.prettyPrintFirstTick();

        List<ItemResponse> inventory = Entity.getPlayer().createItemResponseList();
        List<String> buildables = BuildableEntity.getBuildables();
        addDungeon(this);
        return new DungeonResponse(dungeonId, dungeonName, entities, inventory, battles, buildables, goals);
    }

    public String getNewId() {
        String newId = String.valueOf(idCounter);
        idCounter++;
        return newId;
    }

    public void saveDungeon(String filename) {
        // Makes sure there is a saved directory with a file in it
        Path path = Paths.get("src/main/resources/saved/");
        if (Files.notExists(path)) {
            new File("src/main/resources/saved/").mkdirs();
            File junk = new File("src/main/resources/saved/junk");
            try {
                junk.createNewFile();
            } catch (Exception e) {

            }
            
        }
        
        try {
            FileOutputStream file = new FileOutputStream(FileLoader.getPathForNewFile("saved", filename + ".ser"));
            ObjectOutputStream out = new ObjectOutputStream(file);
                
            // Method for serialization of object
            out.writeObject(this);
            
            out.close();
            file.close();
        } catch (Exception e) {

        }
        
        
    }

    // Save the dungeon for time travel
    public void addDungeon(Dungeon dungeon) {
        // Clone the dungeon
        Dungeon clonedDungeon = null;
        try {
            clonedDungeon = (Dungeon) dungeon.clone();
        }  catch (Exception e) {

        }
        clonedDungeon.setAllEntities(copyEntities());
        // Add the cloned dungeon
        if (prevDungeons != null && prevDungeons.size() >= 30) {
            prevDungeons.remove();
            prevDungeons.add(clonedDungeon);
        } else if (prevDungeons != null) {
            prevDungeons.add(clonedDungeon);
        }
    }

    // Save the tick required
    public void addTick(Ticks tick) {
        prevTicks.add(tick);
    }

    // Load dungon
    public Dungeon loadDungeon(int ticks) {
        Dungeon toReturn = null;
        if (prevDungeons.size() < ticks) {
            toReturn = prevDungeons.remove();
        } else {
            int toRemove = prevDungeons.size() - (ticks + 1);
            for (int i = 0; i < toRemove; i++) {
                prevDungeons.remove();
            }
            toReturn = prevDungeons.remove();
        }
        
        int toRemove2 = prevTicks.size() - ticks;
        for (int i = 0; i < toRemove2; i++) {
            prevTicks.remove();
        }

        // Change the item for items in player inventory to avoid ID conflict
        for (Entity invItem: Entity.getPlayer().getInventory()) {
            invItem.setId(String.valueOf(idCounter));
            idCounter++;
        }

        toReturn.prepPlayer(Entity.getPlayer(), prevTicks);

        prevDungeons.clear();
        return toReturn;
    }

    private void prepPlayer(Player newPlayer, Queue<Ticks> toAdd) {
        Player Oldplayer = (Player)allEntities.stream().filter(ent -> ent.getType().equals("player")).findFirst().orElse(null);
        allEntities.add(new OldPlayer(false, Oldplayer.getPosition(), getNewId(), "older_player", toAdd, Oldplayer.getInventory()));
        allEntities.remove(Oldplayer);
        allEntities.add(newPlayer);
    }

    public void tickOldPlayer() {
        OldPlayer older = (OldPlayer) allEntities.stream().filter(ent -> ent.getType().equals("older_player")).findFirst().orElse(null);
        if (older == null) {
            return;
        } else if (older.ticksLeft() == 0 ) {
            allEntities.remove(older);
        } else {
            older.tick();
        }
    }

    // Clone dungeon
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private ArrayList<Entity> copyEntities() {
        ArrayList<Entity> copyList = new ArrayList<Entity>();
        for (Entity entity: allEntities) {
            Entity copyEntity = null;
            try {
                copyEntity = (Entity) entity.clone();
            } catch (Exception e) {

            }
            if (copyEntity instanceof Player) {
                Player copyPlayer = (Player) copyEntity;
                copyPlayer.copyInventory();
            }
            copyEntity.setPosition(new Position(copyEntity.getPosition().getX(), copyEntity.getPosition().getY()));
            copyEntity.setId(copyEntity.getId());
            copyList.add(copyEntity);
        }
        return copyList;
    }
}
