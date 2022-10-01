package dungeonmania.Entity.MovingEntity;

import dungeonmania.util.Position;
import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.Subject;
import dungeonmania.Entity.CollectibleEntity.Potion;

public class MobEntity extends MovingEntity {
    private Movement movementRandom = new MovementRandom();
    private Movement movementAway = new MovementAway();

    public MobEntity(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        setMovementState(movementRandom);
        
    }
    
    @Override
    public int getEnemyAttack() {
        if (type.equals("zombie_toast")) {
            return DungeonManiaController.getCurrDungeon().getConfig("zombie_attack");
        } else {
            return DungeonManiaController.getCurrDungeon().getConfig("hydra_attack");
        }
        
    }

    @Override
    public int getEnemyHealth() {
        if (type.equals("zombie_toast")) {
            return DungeonManiaController.getCurrDungeon().getConfig("zombie_health");
        } else {
            return DungeonManiaController.getCurrDungeon().getConfig("hydra_health");
        }
    }

    @Override
    public void update(Subject subject) {
        checkPotion(subject.getCurrPotion());
        super.update(subject);
    }

    public void checkPotion(Potion currPotion) {
        if (currPotion == null) {
            setMovementState(movementRandom);
        } else if (currPotion.getType().equals("invincibility_potion")) {
            setMovementState(movementAway);
        }
    }
}
