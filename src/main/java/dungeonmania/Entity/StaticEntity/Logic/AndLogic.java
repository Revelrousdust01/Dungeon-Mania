package dungeonmania.Entity.StaticEntity.Logic;

import java.util.List;

import dungeonmania.Entity.Entity;

import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.Wire;

public class AndLogic implements Logic{
    @Override
    public String getLogic() {
        return "and";
    }

    @Override
    public boolean enableLogic(Entity entity) {
        List<Entity> adjacentEntities = FloorSwitchChecker.getAdjacentActivators(entity);
        if(entity instanceof FloorSwitch && adjacentEntities.contains((FloorSwitch) entity)) {
            adjacentEntities.remove((FloorSwitch) entity);
        }

        if(adjacentEntities.size() < 2){
            return false;
        }
        for (Entity entities: adjacentEntities) {
            if(entities.getType().equals("switch")){
                FloorSwitch switchCheck = (FloorSwitch) entities;
                if(!switchCheck.isTriggered()){
                    return false;
                }
            }
            if(entities.getType().equals("wire")){
                Wire wireCheck = (Wire) entities;
                if(!wireCheck.getTriggered()){
                    return false;
                }
            }
        }
        return true;
    }
}
    
