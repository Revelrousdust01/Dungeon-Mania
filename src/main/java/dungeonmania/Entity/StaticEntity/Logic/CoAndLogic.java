package dungeonmania.Entity.StaticEntity.Logic;

import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.Wire;

public class CoAndLogic implements Logic {
    
    @Override
    public String getLogic() {
        return "co_and";
    }

    @Override
    public boolean enableLogic(Entity entity) {
        int checkTick = 0;
        List<Entity> adjacentEntities = FloorSwitchChecker.getAdjacentActivators(entity);
        if(entity instanceof FloorSwitch && adjacentEntities.contains((FloorSwitch) entity)) {
            adjacentEntities.remove((FloorSwitch) entity);
        }

        for (Entity entities: adjacentEntities) {
            if(entities.getType().equals("switch")){
                FloorSwitch switchCheck = (FloorSwitch) entities;
                if(!switchCheck.isTriggered()){
                    return false;
                }
                else if(checkTick != 0 && switchCheck.getTick() != checkTick){
                    return false;
                }
                else if(checkTick == 0){
                    checkTick = switchCheck.getTick();
                }
            }
            if(entities.getType().equals("wire")){
                Wire wireCheck = (Wire) entities;
                if(!wireCheck.getTriggered()){
                    return false;
                }
                else if(checkTick != 0 && wireCheck.getWireTicker() != checkTick){
                    return false;
                }
                else if(checkTick == 0){
                    checkTick = wireCheck.getWireTicker();
                }

            }

        }
        return true;
    }
}
