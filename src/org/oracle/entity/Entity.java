package org.oracle.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.oracle.entity.npc.Npc;
import org.oracle.entity.object.GameObject;
import org.oracle.entity.user.User;
import org.oracle.tickable.Tick;

/**
 * 
 * @author Oska - <format@allastasia.com>
 *
 * Represents any given Entity (User, NPC, Object)
 */
public class Entity {
	
	
	private int index = 0;
	
	public short getIndex() {
		return (short) index;
	}
	
	public void setIndex(short index) {
		this.index = index;
	}
	
	private Location location = Location.locate(3222, 3218, 0);
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public boolean isPlayer() {
		return false;
		
	}
	
	public boolean isNpc() {
		return false;
			
	}
	
	
	public boolean isObject() {
		return false;
	}
	
	
    public User getPlayer() {
        return null;
    }

    public Npc getNPC() {
        return null;
    }

    public GameObject getGameObject() {
        return null;
    }
    
    public void resetTurnTo() {
       // mask.setInteractingEntity(null);
    }
    
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    
    public void setAttribute(String string, Object object) {
        attributes.put(string, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String string) {
        return (T) attributes.get(string);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String string, T fail) {
        T object = (T) attributes.get(string);
        if (object != null) {
            return object;
        }
        return fail;
    }

    public void removeAttribute(String string) {
        attributes.remove(string);
    }
    
    private Map<String, Tick> ticks = new HashMap<String, Tick>();
    
    public void submitTick(String identifier, Tick tick, boolean replace) {
        if (ticks.containsKey(identifier) && !replace) {
            return;
        }
        ticks.put(identifier, tick);
    }

    public void submitTick(String identifier, Tick tick) {
        submitTick(identifier, tick, false);
    }

    public void removeTick(String identifier) {
        if (hasTick("following_mob") && identifier.equals("following_mob")) {
            resetTurnTo();
        }
        Tick tick = ticks.get(identifier);
        if (tick != null) {
            tick.stop();
            ticks.remove(identifier);
        }
    }
    
    public Tick getTick(String identifier) {
    	return ticks.get(identifier);
    }

    public boolean hasTick(String string) {
        return ticks.containsKey(string);
    }

    public void processTicks() {
        if (ticks.isEmpty()) {
            return;
        }
        Map<String, Tick> ticks = new HashMap<String, Tick>(this.ticks);
        Iterator<Map.Entry<String, Tick>> it = ticks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Tick> entry = it.next();
            if (entry.getKey().equals("following_mob")) {
            	continue;
            }
            if (!entry.getValue().run()) {
                this.ticks.remove(entry.getKey());
            }
        }
    }
    
    public int getViewportDepth() {
        return 0;
    }
}
