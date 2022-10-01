package dungeonmania.Entity.BuildableEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dungeonmania.Entity.BattleItem;
import dungeonmania.Entity.Entity;
import dungeonmania.exceptions.InvalidActionException;

public abstract class BuildableEntity extends Entity implements BattleItem {
    protected int remainingDurability;

    public BuildableEntity(String id, String type) {
        super(id, type);
        getPlayer().addToInventory(this);
    }

    static public void build(String item) throws InvalidActionException {
        boolean successfulBuild = false;
        switch(item) {
            case("bow"):
                successfulBuild = Bow.make();
                break;
            case("shield"):
                successfulBuild = Shield.make();
                break;
            case("sceptre"):
                successfulBuild = Sceptre.make();
                break;
            case("midnight_armour"):
                successfulBuild = MidnightArmour.make();
                break;
            default:
                throw new IllegalArgumentException();
        }

        // Not successful build due to insufficient items or zombie on map for midnight armour
        if (!successfulBuild) {
            throw new InvalidActionException("Insufficient items");
        }
    }

    @Override
    public void decreaseDurability() {
        remainingDurability--;
        if (remainingDurability <= 0 && getPlayer() != null) {
            getPlayer().removeFromInventory(this);
        }
    }
    
    @Override
    public int remainingDurability() {
        return this.remainingDurability;
    }

    static public boolean hasSufficientItems(List<String> method) {
        List<String> currInventoryLs = getPlayer().getInventory().stream().map(e -> e.getType()).collect(Collectors.toList());
        ArrayList<String> currInventory = new ArrayList<String>(currInventoryLs);

        for (String mat : method) {
            if (currInventory.contains(mat)) {
                // Remove the 'used item' so that it doesn't get counted twice
                currInventory.remove(mat);
            } else {
                return false;
            }
        }
        return true;
    }

    static public void removeUsedItems(List<String> method) {
        // Copy the inventory so it doesn't have concurrent modification error
        List<Entity> inventory = new ArrayList<Entity>(getPlayer().getInventory());

        for (String mat : method) {
            // Update the inventory list in case item is removed
            inventory = new ArrayList<Entity>(getPlayer().getInventory());
            
            for (Entity e : inventory) {
                if (e.getType().equals(mat)) {
                    getPlayer().removeFromInventory(e);
                    break;
                }
            }
        }
    }

    static public List<String> getBuildables() {
        ArrayList<String> availableBuildables = new ArrayList<String>();
        if (Bow.checkAvailable() != null) availableBuildables.add("bow");
        if (Shield.checkAvailable() != null) availableBuildables.add("shield");
        if (Sceptre.checkAvailable() != null) availableBuildables.add("sceptre");
        if (MidnightArmour.checkAvailable() != null) availableBuildables.add("midnight_armour");
        
        return availableBuildables;
    }

    static public List<String> sunStoneReplacement(List<List<String>> recipe) {
        for (List<String> method : recipe) {
            // Replace 'key' in recipes with 'sun_stone'
            // This implicitly replaces treasure due to all existing recipes both have (key OR treasure)
            if (method.contains("key")) {
                List<String> modifiedRecipe = new ArrayList<>(method);
                modifiedRecipe.remove("key");
                modifiedRecipe.add("sun_stone");

                // Remove key replacing sun_stone from recipe to prevent it from being consumed
                if (hasSufficientItems(modifiedRecipe)) {
                    modifiedRecipe.remove("sun_stone");
                    return modifiedRecipe;
                }
            }
        }

        return null;
    }
}
