package dungeonmania.Entity.MovingEntity;

import dungeonmania.util.Position;
import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.Subject;
import dungeonmania.Entity.CollectibleEntity.Potion;

public class Mercenary extends MovingEntity {

    private Movement movementTowards = new MovementTowards();
    private Movement movementRandon = new MovementRandom();
    private Movement movementAway = new MovementAway();
    private Movement movementFollow = new MovementFollow();
    private Movement movementStationary = new MovementStationary();

    private boolean isBribed = false;

    public void setBribed(boolean isBribed) {
        this.isBribed = isBribed;
    }

    public Mercenary(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        setMovementState(movementTowards);
    }

    public boolean getIsInteractable() {
        return this.isInteractable;
    }

    @Override
    public String getType() {
        return "mercenary";
    }
    
    @Override
    public int getEnemyAttack() {
        return DungeonManiaController.getCurrDungeon().getConfig("mercenary_attack");
    }

    @Override
    public int getEnemyHealth() {
        return DungeonManiaController.getCurrDungeon().getConfig("mercenary_health");
    }

    @Override
    public void update(Subject subject) {
        checkPotion(subject.getCurrPotion());
        super.update(subject);
    }

    // See which movementState merc is 
    public void checkPotion(Potion currPotion) {
        if (currPotion == null) {
            if (isBribed) {
                setMovementState(movementFollow);
            } else {
                setMovementState(movementTowards);
            }
        } else if (currPotion.getType().equals("invisibility_potion")) {
            setMovementState(movementRandon);
        } else if (currPotion.getType().equals("invincibility_potion")) {
            if (isBribed) {
                setMovementState(movementFollow);
            } else {
                setMovementState(movementAway);
            }
        }
    }

    public void bribe() {
        isBribed = true;
        setInteractable(false);
    }
}
