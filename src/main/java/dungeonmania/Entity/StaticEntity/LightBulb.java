package dungeonmania.Entity.StaticEntity;

import dungeonmania.Entity.StaticEntity.Logic.AndLogic;
import dungeonmania.Entity.StaticEntity.Logic.CoAndLogic;
import dungeonmania.Entity.StaticEntity.Logic.Logic;
import dungeonmania.Entity.StaticEntity.Logic.OrLogic;
import dungeonmania.Entity.StaticEntity.Logic.XorLogic;
import dungeonmania.util.Position;

public class LightBulb extends StaticEntity {
    private boolean triggered;
    private Logic logic;
    public LightBulb(Position position, String id, String type, String logic) {
        super(false, position, id, type);
        this.logic = logicCreator(logic);
        this.triggered = false;
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
    public boolean getTriggered() {
        return triggered;
    }
    public void checkTriggered() {
        this.triggered = logic.enableLogic(this);
    }
}
