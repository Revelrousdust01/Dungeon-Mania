package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dungeonmania.DungeonManiaController;
import dungeonmania.util.Position;
import dungeonmania.Entity.EntityFactory;

public class Spider extends MovingEntity {
    private Position surroundPosition;
    private static List<Position> spawnPositions = new ArrayList<Position>();
    private Movement movementSpider = new MovementSpider();
    private int movementIndex = -1;
    private boolean initialMove = true;


    public Spider(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        // Assign the position it is surround as the spawn position
        surroundPosition = position;
        setMovementState(movementSpider);
        ((MovementSpider) movementSpider).createSurroundPositions(this);
    }

    @Override
    public int getEnemyAttack() {
        return DungeonManiaController.getCurrDungeon().getConfig("spider_attack");
    }
    @Override
    public int getEnemyHealth() {
        return DungeonManiaController.getCurrDungeon().getConfig("spider_health");
    }

    public Position getSurroundPosition() {
        return surroundPosition;
    }

    public static void createInitialSpiderSpawn() {
        // Create the initial spawn positions
        
        List<Integer> previousValuesX = new ArrayList<Integer>();
        for(int i =0; i<21; i++){
            previousValuesX.add(i);
        }
        Collections.shuffle(previousValuesX);
        List<Integer> previousValuesY = new ArrayList<Integer>();
        for(int i =0; i<11; i++){
            previousValuesY.add(i);
        }
        for (int i = 0; i < 4; i++) {
            int x = previousValuesX.get(0);
            previousValuesX.remove(0);
            int y = previousValuesY.get(0);
            previousValuesY.remove(0);
            spawnPositions.add(new Position(x, y));
        }
    }

    public static void spawnSpider() {
        int spawnRate = DungeonManiaController.getCurrDungeon().getConfig("spider_spawn_rate");
        int ticker = DungeonManiaController.getCurrDungeon().getSpiderTicker();
        if (spawnRate == ticker) {
            int spawnerLocation = new Random().nextInt(0 + 4);
            EntityFactory.createEntity("spider", spawnPositions.get(spawnerLocation).asLayer(3));
            DungeonManiaController.getCurrDungeon().setSpiderTicker(1);
        } else {
            DungeonManiaController.getCurrDungeon().setSpiderTicker(ticker + 1);
        }
    }

    public int getMovementIndex() {
        return movementIndex;
    }
    public void setMovementIndex(int movementIndex) {
        this.movementIndex = movementIndex;
    }
    public boolean isInitialMove() {
        return initialMove;
    }
    public void setInitialMove(boolean initialMove) {
        this.initialMove = initialMove;
    }

}
