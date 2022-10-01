package dungeonmania.Entity.StaticEntity;

import dungeonmania.Entity.CollectibleEntity.Key;
import dungeonmania.Entity.CollectibleEntity.SunStone;
import dungeonmania.Entity.StaticEntity.Logic.AndLogic;
import dungeonmania.Entity.StaticEntity.Logic.CoAndLogic;
import dungeonmania.Entity.StaticEntity.Logic.Logic;
import dungeonmania.Entity.StaticEntity.Logic.OrLogic;
import dungeonmania.Entity.StaticEntity.Logic.XorLogic;
import dungeonmania.util.Position;

public class SwitchDoor extends StaticEntity implements RigidEntity {
    private boolean open;
    private int keyNum;
    private Logic logic;

    public SwitchDoor(boolean isInteractable, Position position, String id, String type, int keyNum, String logic) {
        super(isInteractable, position, id, type);
        this.open = false;
        this.keyNum = keyNum;
        this.logic = logicCreator(logic);
    }

    private Logic logicCreator(String logic) {
        switch (logic) {
            case "or":
                return new OrLogic();
            case "and":
                return new AndLogic();
            case "xor":
                return new XorLogic();
            case "co_and":
                return new CoAndLogic();
        }
        return null;
    }

    public boolean isPassable() {
        if (open) {
            return open;
        }
        return logic.enableLogic(this);
    }

    public boolean unlock(Key key) {
        if (key != null && key.getKeyNum() == this.keyNum) {
            open = true;
            return true;
        } else {
            return false;
        }
    }

    public void unlock(SunStone stone) {
        open = true;
    }
}
