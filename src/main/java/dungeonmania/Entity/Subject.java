package dungeonmania.Entity;

import dungeonmania.Entity.CollectibleEntity.Potion;
import dungeonmania.util.Position;

public interface Subject {
    public void updateObserverList();
    public void notifyObservers();

    public Potion getCurrPotion();
    public Position getPosition();
    public Position getPrevPosition();
}
