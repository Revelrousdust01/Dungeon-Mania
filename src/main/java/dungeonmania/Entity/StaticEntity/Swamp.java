package dungeonmania.Entity.StaticEntity;

import java.util.ArrayList;
import java.util.Random;

import dungeonmania.DungeonManiaController;
import dungeonmania.util.Position;

public class Swamp extends StaticEntity {
    private int movementFactor;
    
    // Setting the maximum movementFactor to be 15
    private final int bound = 15;

    public Swamp(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        Random rand = new Random();
        this.movementFactor = rand.nextInt(bound);
        getSwampList().add(this);
    }

    // Run this before another test so the list doesn't remain!
    public void clearSwamps() {
        getSwampList().clear();
    }

    // Checks if the position has a swamp
    public static Swamp checkSwamp(Position position) {
        for (Swamp swamp: getSwampList()) {
            if (swamp.getPosition().equals(position)) {
                return swamp;
            }
        }
        return null;
    }

    public int getMovementFactor() {
        return movementFactor;
    }

    public void setMovementFactor(int movementFactor) {
        this.movementFactor = movementFactor;
    }

    public static ArrayList<Swamp> getSwampList() {
        return DungeonManiaController.getCurrDungeon().getSwamps();
    }
}
