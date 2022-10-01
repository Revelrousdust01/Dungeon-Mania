package dungeonmania.Entity;

public interface BattleItem {
    public abstract int getBattleEffect();
    
    public abstract void decreaseDurability();

    public abstract int remainingDurability();
}