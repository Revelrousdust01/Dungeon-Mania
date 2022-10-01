package dungeonmania.Battle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Random;

import dungeonmania.Dungeon;
import dungeonmania.DungeonManiaController;
import dungeonmania.Entity.BattleItem;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.BuildableEntity.MidnightArmour;
import dungeonmania.Entity.CollectibleEntity.Potion;
import dungeonmania.Entity.MovingEntity.Enemy;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.response.models.*;

public class Battle {
    private int PlayerAttackMultiplier = 1;
    private int PlayerAttackAdditive = 0;
    private int PlayerDefenseAdditive = 0;
    Dungeon currentDungeon = DungeonManiaController.getCurrDungeon();
    private int deadIdCounter = -1;
    static private Random hydraSeed = new Random();

    /**
     * calculates damage player/enemy will inflict per round for standard items
     * @param attacker modifies damage output of either player or enemy
     */
    private float calculateDmg(Entity attacker, List<Entity> playerWeapons) {
        float totalDamage;
        if(attacker instanceof Player) {
            // playerdmg = ((bow * (base + sword)) / 5)
            totalDamage = (float) Math.multiplyExact(this.PlayerAttackMultiplier, Math.addExact(currentDungeon.getConfig("player_attack"), this.PlayerAttackAdditive)) / 5;
        } else {
            Enemy enemy = (Enemy) attacker;
            int enemyBase = enemy.getEnemyAttack();
            // enemyDmg = ((base - shield) / 10)
            totalDamage = (float) Math.subtractExact(enemyBase, PlayerDefenseAdditive) / 10;
        }
        return totalDamage;
    }

    public BattleResponse battle(Entity enemy, List<Entity> playersWeapons) {
        List<ItemResponse> weaponsResponse = new ArrayList<ItemResponse>();

        Potion currentPotion = Player.getPlayer().getCurrPotion();
        if(currentPotion != null) weaponsResponse.add(new ItemResponse(currentPotion.getId(), currentPotion.getType()));

        // buffing player
        for(Entity weapon : playersWeapons) {
            String weaponType = weapon.getType();
            BattleItem battleWeapon = (BattleItem) weapon;
            if(weaponType == "bow") this.PlayerAttackMultiplier *= 2;
            if(weaponType == "sword") this.PlayerAttackAdditive += battleWeapon.getBattleEffect();
            if(weaponType == "shield") this.PlayerDefenseAdditive += battleWeapon.getBattleEffect();
            weaponsResponse.add(new ItemResponse(weapon.getId(), weapon.getType()));
        }

        float playerDmg = calculateDmg(Player.getPlayer(), playersWeapons);
        float enemyDmg = calculateDmg(enemy, playersWeapons);

        List<Entity> mercenaryEntities = Entity.getAllEntities()
        .stream()
        .filter(e -> e.getType().equals("mercenary"))
        .collect(Collectors.toList());

        List<Mercenary> mercenaries = mercenaryEntities
        .stream()
        .map(e -> (Mercenary) e)
        .collect(Collectors.toList());
        // apply allied buffs to player and debuffs to enemy
        for(Mercenary mercenary : mercenaries) { 
            if(!mercenary.getIsInteractable()) { // non-interactable merc == ally
                playerDmg += currentDungeon.getConfig("ally_attack");
                enemyDmg -= currentDungeon.getConfig("ally_defence");
            }
        }

        // apply midnight armor buffs 
        for(Entity item : Player.getPlayer().getInventory()) {
            if(item.getType().equals("midnight_armour")) {
                MidnightArmour armor = (MidnightArmour) item;
                playerDmg += armor.getMidnightAttack();
                enemyDmg -= armor.getMidnightDefence();
            }
        }

        float currentHealth = currentDungeon.getConfig("player_health");
        Enemy castedEnemy = (Enemy) enemy;
        float enemyHealth = castedEnemy.getEnemyHealth();
        float initialEnemyHealth = enemyHealth;

        List<RoundResponse> rounds = new ArrayList<RoundResponse>();

        Potion currPotion = Entity.getPlayer().getCurrPotion();
        if (currPotion != null && currPotion.getType().equals("invincibility_potion")) {
            playerDmg = enemyHealth;
            enemyHealth = 0;
            rounds.add(new RoundResponse(0, playerDmg * -1, weaponsResponse)); 
        } else {
            // run rounds until enemy or player dies, recording every round
            while(currentHealth > 0 && enemyHealth > 0) {
                currentHealth -= enemyDmg;
                if (enemy.getType().equals("hydra")) {
                    if (hydraSeed.nextInt(100) < currentDungeon.getConfig("hydra_health_increase_rate")) {
                        int healthIncrease = currentDungeon.getConfig("hydra_health_increase_amount");
                        enemyHealth += healthIncrease;
                        rounds.add(new RoundResponse(enemyDmg * -1, healthIncrease, weaponsResponse)); 
                    } else {
                        enemyHealth -= playerDmg;
                        rounds.add(new RoundResponse(enemyDmg * -1, playerDmg * -1, weaponsResponse)); 
                    }
                } else {
                    enemyHealth -= playerDmg;
                    rounds.add(new RoundResponse(enemyDmg * -1, playerDmg * -1, weaponsResponse)); 
                }
                
            }
        }
        
        if (currentHealth <= 0)
            Entity.removeEntityFromMap(Entity.getPlayer());
        if (enemyHealth <= 0) {
            Entity.removeEntityFromMap(enemy);
            enemy.setId(String.valueOf(deadIdCounter));
            deadIdCounter--;
        }

        // decrease durability and remove if 0
        for(Entity weapon : playersWeapons.stream().filter(e -> !e.getType().contains("potion"))
                .collect(Collectors.toList())) {
            BattleItem durableWeapon = (BattleItem) weapon;
            durableWeapon.decreaseDurability();
        }

        return new BattleResponse(enemy.getId(), rounds, (double) currentDungeon.getConfig("player_health"), (double) initialEnemyHealth);
    }

    /**
     * For hydra health increase testing purposes
     * @param seed
     */
    static public void setHydraSeed(int seed) {
        hydraSeed = new Random(seed);
    }
    
}
