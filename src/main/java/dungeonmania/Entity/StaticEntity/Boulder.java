package dungeonmania.Entity.StaticEntity;

import dungeonmania.util.Position;
import dungeonmania.util.Direction;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.Logic.FloorSwitchChecker;

public class Boulder extends StaticEntity implements RigidEntity {
    private FloorSwitch floorSwitch = null;

    public Boulder(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
    }

    /**
     * 
     * @param direction
     * @return true is boulder is pushed successfully
     */
    public boolean push(Direction direction) {
        Position destination = this.position.translateBy(direction);
        Entity destinationEntity = findEntityAtPosition(destination);

        if (destinationEntity == null) {
            this.position = this.position.translateBy(direction);
            checkSwitch(destinationEntity);
            return true;
        }

        // Boulder collision - locked doors, walls, boulders
        if (destinationEntity instanceof RigidEntity) {
            RigidEntity rEntity = (RigidEntity) destinationEntity;
            if (!rEntity.isPassable()) { return false; }
        }

        this.position = destination;
        checkSwitch(destinationEntity);
        return true;
        
    }

    public boolean isPassable() {
        return false;
    }

    /**
     * Used to check if a boulder is moving onto or off a switch
     * @param destinationEntity: entity at the position boulder is moving towards
     */
    public void checkSwitch(Entity destinationEntity) {
        if (floorSwitch != null) {
            floorSwitch.setTriggered(false, 1);
            floorSwitch = null;
        }

        if (destinationEntity instanceof FloorSwitch) {
            floorSwitch = (FloorSwitch) destinationEntity;
            floorSwitch.setTriggered(true, 1);
            FloorSwitchChecker.triggerFloorSwitchWires(floorSwitch);
            FloorSwitchChecker.checkLightBulbs();
        }
    }
}
