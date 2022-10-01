package dungeonmania.Entity.StaticEntity;

import java.util.ArrayList;

import dungeonmania.util.Position;
import dungeonmania.Entity.CollectibleEntity.Bomb;

public class FloorSwitch extends StaticEntity {
    protected boolean isTriggered;
    protected int tick;
    private ArrayList<Bomb> bombsInRange = new ArrayList<Bomb>();

    public FloorSwitch(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    public boolean isTriggered() {
        return isTriggered;
    }

   
    public int getTick(){
        return tick;
    }
    
    public void setTriggered(boolean isTriggered, int newTick) {
        this.isTriggered = isTriggered;
        this.tick = newTick + this.tick;
        if (isTriggered) {
            for (Bomb b : bombsInRange) {
                b.explode();
            }
        }
    }

    public ArrayList<Bomb> getBombsInRange() {
        return bombsInRange;
    }

    
}
