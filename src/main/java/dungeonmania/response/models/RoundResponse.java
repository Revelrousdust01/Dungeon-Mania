package dungeonmania.response.models;

import java.util.List;

public class RoundResponse {
    private float deltaPlayerHealth;
    private float deltaEnemyHealth;
    private List<ItemResponse> weaponryUsed;

    public RoundResponse(float deltaPlayerHealth, float deltaEnemyHealth, List<ItemResponse> weaponryUsed)
    {
        this.deltaPlayerHealth = deltaPlayerHealth;
        this.deltaEnemyHealth = deltaEnemyHealth;
        this.weaponryUsed = weaponryUsed;
    }

    public float getDeltaCharacterHealth(){
        return deltaPlayerHealth;
    }
    
    public float getDeltaEnemyHealth(){
        return deltaEnemyHealth;
    }

    public List<ItemResponse> getWeaponryUsed() { return weaponryUsed; }
}
