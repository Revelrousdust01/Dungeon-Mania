package dungeonmania.Entity.BuildableEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.Assassin;
import dungeonmania.Entity.MovingEntity.Mercenary;

public class Sceptre extends BuildableEntity {
    private int remainingDuration;
    private boolean isInUse;

    public void setInUse(boolean isInUse) {
        this.isInUse = isInUse;
    }

    public boolean isInUse() {
        return isInUse;
    }

    public Sceptre() {
        super(DungeonManiaController.getCurrDungeon().getNewId(), "sceptre");
        this.remainingDuration = DungeonManiaController.getCurrDungeon().getConfig("mind_control_duration");
        this.isInUse = false;
    }

    @Override
    public int getBattleEffect() {
        // TODO Auto-generated method stub
        return 0;
    }

    static public boolean make() {
        List<String> method = checkAvailable();
        if (method != null) {
            new Sceptre();
            removeUsedItems(method);
            return true;
        }
        return false;
    }

    /**
     * Turns on bribed state for all bribed enemies and starts countdown for duration with IsInUse(true)
     */
    public void use() {
        if(isInUse) return;
        this.setInUse(true);
        for (Entity entity : Entity.getAllEntities()) {
            if (entity instanceof Mercenary) {
                Mercenary merc = (Mercenary) entity;
                merc.setBribed(true);
                merc.setInteractable(false);
            }
            if (entity instanceof Assassin) {
                Assassin assassin = (Assassin) entity;
                assassin.setBribed(true);
                assassin.setInteractable(false);
            }
        }
    }

    /**
     * Decreases duration and runs deactivating mind control when duration <= 0
     */
    public static void decreaseDuration() {
        if (getPlayer() == null) {
            return;
        }
        List<Sceptre> sceptreList = Entity.getPlayer().getInventory()
            .stream()
            .filter(e -> e.getType().equals("sceptre"))
            .map(e -> (Sceptre) e)
            .collect(Collectors.toList());
        
        if(sceptreList.size() == 0) return;

        Sceptre currSceptre = sceptreList.get(0);
        if (!currSceptre.isInUse()) {
            return;
        }
        currSceptre.remainingDuration--;
        if (currSceptre.remainingDuration <= 0) {
            deactivateMindControl();
            currSceptre.setInUse(false);
            currSceptre.resetDuration();
        }
    }

    private void resetDuration() {
        this.remainingDuration = DungeonManiaController.getCurrDungeon().getConfig("mind_control_duration");
    }

    /**
     * Resets bribed state of all mercenaries/assassins
     */
     private static void deactivateMindControl() {
        for(Entity entity : Entity.getAllEntities()) {
            if(entity instanceof Mercenary) {
                Mercenary merc = (Mercenary) entity;
                merc.setBribed(false);
                merc.setInteractable(true);
            }
            if(entity instanceof Assassin) {
                Assassin assassin = (Assassin) entity;
                assassin.setBribed(false);
                assassin.setInteractable(true);
            }
        }
    }

    static public List<String> checkAvailable() {
        // Create lists for each recipe
        List<List<String>> requiredToCraft = new ArrayList<>();
        List<String> method1 = Arrays.asList("wood", "treasure", "sun_stone");
        requiredToCraft.add(method1);
        List<String> method2 = Arrays.asList("arrow", "arrow", "treasure", "sun_stone");
        requiredToCraft.add(method2);
        List<String> method3 = Arrays.asList("wood", "key", "sun_stone");
        requiredToCraft.add(method3);
        List<String> method4 = Arrays.asList("arrow", "arrow", "key", "sun_stone");
        requiredToCraft.add(method4);

        // Craft the item if inventory has sufficient items
        for (List<String> method : requiredToCraft) {
            if (hasSufficientItems(method)) {
                return method;
            }
        }

        List<String> alternativeRecipe = sunStoneReplacement(requiredToCraft);
        if (alternativeRecipe != null) return alternativeRecipe;

        return null;

    }
}