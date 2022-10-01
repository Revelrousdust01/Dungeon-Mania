package dungeonmania.Entity.StaticEntity;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.util.Position;

public class Wire extends StaticEntity {

    private boolean triggered = false;
    private int setTick = 0;
    private List<FloorSwitch> floorSwitches = new ArrayList<FloorSwitch>();

    public Wire(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }
    
    public void setTriggered(boolean triggered) {
        this.triggered = triggered;
    }

    public boolean getTriggered(){
        return triggered;
    }

    public void addFloorSwitch(FloorSwitch floorSwitch) {
        floorSwitches.add(floorSwitch);
    }

    public List<FloorSwitch> getFloorSwitches() {
        return floorSwitches;
    }

    public void setTick(int tick){
        this.setTick = tick;
    }

    public void removeTick(){
        this.setTick = 0;
    }

    public int getWireTicker(){
        return setTick;
    }

}
