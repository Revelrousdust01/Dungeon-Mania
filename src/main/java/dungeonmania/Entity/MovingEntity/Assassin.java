package dungeonmania.Entity.MovingEntity;

import dungeonmania.util.Position;

import java.util.Random;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Subject;
import dungeonmania.Entity.CollectibleEntity.Potion;

public class Assassin extends MovingEntity {

    private Movement movementTowards = new MovementTowards();
    private Movement movementRandon = new MovementRandom();
    private Movement movementFollow = new MovementFollow();
    private Movement movementStationary = new MovementStationary();
    
    private boolean isBribed = false;
    
    public void setBribed(boolean isBribed) {
        this.isBribed = isBribed;
    }

    private int reconRange = DungeonManiaController.getCurrDungeon().getConfig("assassin_recon_radius");

    public Assassin(boolean isInteractable, Position position, String id, String type) {
        super(isInteractable, position, id, type);
        setMovementState(movementTowards);
    }

    public boolean getIsInteractable() {
        return this.isInteractable;
    }

    @Override
    public void update(Subject subject) {
        checkPotion(subject.getCurrPotion());
        super.update(subject);
    }

    // See which movementState assassin is 
    public void checkPotion(Potion currPotion) {
        if (currPotion == null) {
            if (isBribed) {
                setMovementState(movementFollow);
            } else {
                setMovementState(movementTowards);
            }
        } else if (currPotion.getType().equals("invisibility_potion")) {
            if (isBribed) {
                if (reconRange < Position.dist(Entity.getPlayer().getPosition(), getPosition())) {
                    // Outside recon range
                    setMovementState(movementRandon);
                } else {
                    // Within recon range
                    setMovementState(movementFollow);
                }
            } else {
                // Not bribed
                if (reconRange < Position.dist(Entity.getPlayer().getPosition(), getPosition())) {
                    // Outside recon range
                    setMovementState(movementStationary);
                } else {
                    // Within recon range
                    setMovementState(movementFollow);
                }
            }
        }
    }

    public void bribe() {
        Random rand = new Random();
        int bribeRand = rand.nextInt(100);
        if (bribeRand >= DungeonManiaController.getCurrDungeon().getConfig("assassin_bribe_fail_rate")) {
            // Bribe Success
            isBribed = true;
            setInteractable(false);    
        }
    }

    @Override
    public int getEnemyAttack() {
        return DungeonManiaController.getCurrDungeon().getConfig("assassin_attack");
    }

    @Override
    public int getEnemyHealth() {
        return DungeonManiaController.getCurrDungeon().getConfig("assassin_health");
    }
}
