package dungeonmania;

import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Config implements Serializable {
    private Map<String, String> configValues = new HashMap<String, String>();

    public Config(JsonObject configJson) {
        for (String key : configJson.keySet()) {
            if (key.equals("assassin_bribe_fail_rate")) {
                Double failRate = configJson.get(key).getAsDouble() * 100;
                configValues.put(key, String.valueOf(failRate.intValue()));
            } else if (key.equals("hydra_health_increase_rate")) {
                Double increaseRate = configJson.get(key).getAsDouble() * 100;
                configValues.put(key, String.valueOf(increaseRate.intValue()));
            } else {
                configValues.put(key, configJson.get(key).getAsString());
            }
            
        }
        
    }

    public int getConfig(String field) {
        return Integer.parseInt(configValues.get(field));
    }

}