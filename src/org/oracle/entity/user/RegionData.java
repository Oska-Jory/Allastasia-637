package org.oracle.entity.user;

import org.oracle.entity.Location;
import org.oracle.tickable.Tick;

public class RegionData {

	private boolean didTeleport;
	private boolean didMapRegionChange;
	private boolean NeedReload;
	private Location lastMapRegion;
	private Location lastLocation;

	private User user;

	public RegionData(User entity) {
		this.user = entity;
		this.setLastLocation(Location.locate(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ()));
	}

	public void teleport(int coordX, int coordY, int height) {
		getLastMapRegion();
		//user.getWalkingQueue().reset();
		//user.setAttribute("cantMove", Boolean.TRUE);
		final Location futurelocation = Location.locate(coordX, coordY, height);
		if (lastMapRegion.getRegionX() - futurelocation.getRegionX() >= 4 || lastMapRegion.getRegionX() - futurelocation.getRegionX() <= -4) {
			setDidMapRegionChange(true);
		} else if (lastMapRegion.getRegionY() - futurelocation.getRegionY() >= 4 || lastMapRegion.getRegionY() - futurelocation.getRegionY() <= -4) {
			setDidMapRegionChange(true);
		}
		this.lastLocation = user.getLocation();
		user.setLocation(futurelocation);
		setDidTeleport(true);
		if (isDidMapRegionChange()) {
			user.updateMap();
			setDidMapRegionChange(false);
		}
		//if (user.getSettings().isResting()) {
		//	ActionSender.sendBConfig(user, 119, 0);
		//	user.getSettings().setResting(false);
	//	}
		user.submitTick("resetMove", new Tick(1) {
			public void execute() {
				stop();
				user.removeAttribute("cantMove");
			}
		});
	}


	public void reset() {
		//if (user.getWalkingQueue().isDidTele())
		//	this.setDidTeleport(false);
		this.setDidMapRegionChange(false);
		this.setNeedReload(false);
	}

	public void setDidMapRegionChange(boolean b) {
		didMapRegionChange = b;
	}

	public boolean isDidMapRegionChange() {
		return didMapRegionChange;
	}

	public void setLastMapRegion(Location lastMapRegion) {
		this.lastMapRegion = lastMapRegion;
	}

	public Location getLastMapRegion() {
		if (lastMapRegion == null) {
			lastMapRegion = user.getLocation();
		}
		return lastMapRegion;
	}

	public void setDidTeleport(boolean didTeleport) {
		this.didTeleport = didTeleport;
	}

	public boolean isDidTeleport() {
		return didTeleport;
	}

	public void setNeedReload(boolean needReload) {
		NeedReload = needReload;
	}

	public boolean isNeedReload() {
		return NeedReload;
	}

	public void setLastLocation(Location lastLocation) {
		this.lastLocation = lastLocation;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public void teleport(Location pos) {
		teleport(pos.getX(), pos.getY(), pos.getZ());
	}
}
