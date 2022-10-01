package dungeonmania.Entity.StaticEntity;

import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Portal extends StaticEntity {
    private String colour;

    public Portal(boolean isInteractable, Position position, String id, String type, String colour) {
        super(isInteractable, position, id, type);
        this.colour = colour;
    }

    public String getColour() {
        return colour;
    }
    
    public Portal getOtherPortal() {
        return getAllEntities().stream().filter(entity -> entity instanceof Portal && entity != this).map(entity -> (Portal) entity).filter(portal -> portal.getColour().equals(colour)).findFirst().orElse(null);
    }

    public boolean isBlocked() {
        // Check the four edges
        if (isPositionBlocked(position.translateBy(Direction.UP))) {
            return true;
        }
        if (isPositionBlocked(position.translateBy(Direction.DOWN))) {
            return true;
        }
        if (isPositionBlocked(position.translateBy(Direction.LEFT))) {
            return true;
        }
        if (isPositionBlocked(position.translateBy(Direction.RIGHT))) {
            return true;
        }
        return false;
    }

    private boolean isPositionBlocked(Position toLook) {
        return getAllEntities().stream().filter(ent -> ent instanceof RigidEntity).filter(ent -> ent.getPosition().getX() == toLook.getX() && ent.getPosition().getY() == toLook.getY()).count() > 0;
    }

    public Position getExitPosition(Direction toGo) {
        return this.position.translateBy(toGo);
    }
}
