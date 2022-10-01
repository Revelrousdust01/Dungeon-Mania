package dungeonmania;

import java.io.Serializable;

public class Ticks implements Serializable {
    private String tickType;
    private Object arguement;
    
    public Ticks(String tickType, Object arguement) {
        this.tickType = tickType;
        this.arguement = arguement;
    }

    public String getTickType() {
        return tickType;
    }

    public Object getArguement() {
        return arguement;
    }
}
