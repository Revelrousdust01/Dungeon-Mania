package dungeonmania.Entity.StaticEntity;

import dungeonmania.util.Position;

public class Exit extends StaticEntity {
    private boolean exitedMaze;

    public Exit(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        this.exitedMaze = false;
    }
    
    public boolean getExitedMaze() {
        return exitedMaze;
    }
    
}
