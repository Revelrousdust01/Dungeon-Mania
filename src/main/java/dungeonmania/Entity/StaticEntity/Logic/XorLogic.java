package dungeonmania.Entity.StaticEntity.Logic;

import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.Wire;

public class XorLogic implements Logic {

    @Override
    public String getLogic() {
        return "xor";
    }

    @Override
    public boolean enableLogic(Entity entity) {
        int active = 0;
        List<Entity> adjacentEntities = FloorSwitchChecker.getAdjacentActivators(entity);
        if(entity instanceof FloorSwitch && adjacentEntities.contains((FloorSwitch) entity)) {
            adjacentEntities.remove((FloorSwitch) entity);
        }
        for (Entity entities: adjacentEntities) {
            if(entities.getType().equals("switch")){
                FloorSwitch switchCheck = (FloorSwitch) entities;
                if(switchCheck.isTriggered()){
                    active += 1;
                }
            }
            if(entities.getType().equals("wire")){
                Wire wireCheck = (Wire) entities;
                if(wireCheck.getTriggered()){
                    active += 1;
                }
            }
        }
        if(active == 1){
            return true;
        }
        return false;
    }
}
