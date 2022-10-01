package dungeonmania.Entity.StaticEntity.Logic;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntity.FloorSwitch;
import dungeonmania.Entity.StaticEntity.LightBulb;
import dungeonmania.Entity.StaticEntity.Wire;
import dungeonmania.util.Position;

public class FloorSwitchChecker {

    public static List<Entity> getCardinallyAdjacentEntities(Entity entity) {
        
        List<Position> adjacentPositions = entity.getPosition().getAdjacentPositions();
        adjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()-1 && x.getY() == entity.getPosition().getY()+1);
        adjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()+1 && x.getY() == entity.getPosition().getY()+1);
        adjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()-1 && x.getY() == entity.getPosition().getY()-1);
        adjacentPositions.removeIf(x-> x.getX() == entity.getPosition().getX()+1 && x.getY() == entity.getPosition().getY()-1);

        List<Entity> result = new ArrayList<Entity>();
        for(Position position : adjacentPositions) {
            Entity adjacentEntity = Entity.findEntityAtPositionSwitch(position);
            if(adjacentEntity != null) result.add(adjacentEntity);
        }
        return result;
    }
    
    /**
     * Recursively triggers all wires connected to a floor switch
     */
    public static void triggerFloorSwitchWires(FloorSwitch floorSwitch) {
        if(floorSwitch.isTriggered()) {
            List<Wire> adjacentWires = FloorSwitchChecker.getAdjacentWires(floorSwitch);
            for(Wire wire : adjacentWires) {
                triggerWireLine(wire);
            }
        }
    }
    // helper for triggerFloorSwitchWires, triggers all untriggered wires connected to a wire
    private static void triggerWireLine(Wire wire) {
        wire.setTriggered(true);
        wire.setTick(DungeonManiaController.getCurrDungeon().getWireTicker());
        List<Wire> adjacentUntriggeredWires = getCardinallyAdjacentEntities(wire)
            .stream()
            .filter(e -> e.getType().equals("wire"))
            .map(e -> (Wire) e)
            .filter(e -> !e.getTriggered())
            .collect(Collectors.toList());
        for(Wire untriggeredWire : adjacentUntriggeredWires) {
            triggerWireLine(untriggeredWire);
        }
    }
    /**
     * Returns a list of switches adjacent to an entity
     * @param entity Entity where we check for adjacent switches
     */
    public static List<FloorSwitch> getAdjacentSwitches(Entity entity) {
        return getCardinallyAdjacentEntities(entity)
                .stream()
                .filter(e -> e.getType().equals("switch"))
                .map(e -> (FloorSwitch) e)
                .collect(Collectors.toList());
    }
    /**
     * Returns A list of logic entities adjacent to wires
    */
     public static List<Entity> getAdjacentLogicEntities() {
        // get logical entities
        List<Entity> logicEntities = Entity.getAllEntities()
                .stream()
                .filter(e -> e.getType() == "switch" || e.getType() == "light_bulb" || e.getType() == "switch_door")
                .collect(Collectors.toList());

        List<Entity> result = new ArrayList<Entity>();
        // get floor switches that are adjacent to wires
        for(Entity logicEntity : logicEntities) {
            // find entities next to current switch
            List<String> adjacentEntityTypes = getCardinallyAdjacentEntities(logicEntity)
                .stream()
                .map(e -> e.getType())
                .collect(Collectors.toList());
            // if list of adjacent entities contains a wire, add to result
            if(adjacentEntityTypes.contains("wire")) result.add(logicEntity);
        }
        
        return result;
    }
    /**
     * Returns a list of wires that are activated or 'on'
     */
    public static List<Wire> getActivatedWires() {
        return Entity.getAllEntities()
            .stream()
            .filter(e -> e.getType().equals("wire"))
            .map(e -> (Wire) e)
            .filter(e -> e.getTriggered())
            .collect(Collectors.toList());
    }

    /**
     * Returns a list of wires adjacent to a specified floor switch
     * @param floorSwitch The switch that we check for adjacent wires
     */
    public static List<Wire> getAdjacentWires(FloorSwitch floorSwitch) {
        return getCardinallyAdjacentEntities(floorSwitch)
            .stream()
            .filter(e -> e.getType().equals("wire"))
            .map(e -> (Wire) e)
            .collect(Collectors.toList());
    }
    /**
     * Returns list of cardinally adjacent switches or wires to an entity
     * @param entity Entity to check for cardinally adjacent stuff
     */
    public static List<Entity> getAdjacentActivators(Entity entity) {
        return getCardinallyAdjacentEntities(entity)
            .stream()
            .filter(e -> e.getType().equals("wire") || e.getType().equals("switch"))
            .collect(Collectors.toList());
    }
    /**
     * Returns list of all triggered floor switches
     */
    public static List<FloorSwitch> getTriggeredSwitches() {
        return Entity.getAllEntities()
            .stream()
            .filter(e -> e.getType().equals("switch"))
            .map(e -> (FloorSwitch) e)
            .filter(e -> e.isTriggered())
            .collect(Collectors.toList()); 
    }
    /**
     * Returns a list of all activated wires
     */
    public static List<Wire> getTriggeredWires() {
        return Entity.getAllEntities()
            .stream()
            .filter(e -> e.getType().equals("wire"))
            .map(e -> (Wire) e)
            .filter(e -> e.getTriggered())
            .collect(Collectors.toList());
    }

    public static void checkLightBulbs() {
        List<LightBulb> lightbulbs = Entity.getAllEntities()
            .stream()
            .filter(e -> e.getType().equals("light_bulb"))
            .map(e -> (LightBulb) e)
            .collect(Collectors.toList());
        
        for(LightBulb lightbulb : lightbulbs) {
            lightbulb.checkTriggered();
        }
    }
}
