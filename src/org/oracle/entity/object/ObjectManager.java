package org.oracle.entity.object;

import java.util.ArrayList;
import java.util.List;

import org.oracle.Launcher;
import org.oracle.entity.Location;
import org.oracle.entity.user.User;
import org.oracle.network.ActionSender;

/**
 * @author 'Mystic Flow
 */
public class ObjectManager {


	private static List<GameObject> customObjects = new ArrayList<GameObject>();

	private static List<GameObject> removedObjects = new ArrayList<GameObject>();

	public static GameObject addCustomObject(int objectId, int x, int y, int height, int type, int direction) {
		return addCustomObject(objectId, x, y, height, type, direction, true);
	}

	public static GameObject removeCustomObject(Location l, int type) {
		return removeCustomObject(l.getX(), l.getY(), l.getZ(), type, true);
	}

	public static GameObject removeCustomObject(int x, int y, int height, int type) {
		return removeCustomObject(x, y, height, type, true);
	}

	public static GameObject addCustomObject(User owner, int objectId, int x, int y, int height, int type, int direction) {
		return addCustomObject(objectId, x, y, height, type, direction, true);
	}

	public static GameObject addCustomObject(GameObject object) {
		return addCustomObject(object.getId(), object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType(), object.getRotation());
	}

	public static GameObject addCustomObject(int objectId, int x, int y, int height, int type, int direction, boolean refresh) {
		GameObject objectAdded = Region.addObject(objectId, x, y, height, type, direction, false);
		if (objectAdded != null && removedObjects.contains(objectAdded)) {
			removedObjects.remove(objectAdded);
		}
		if (objectAdded != null) {
			customObjects.add(objectAdded);
			if (refresh) {
				refresh(objectAdded);
			}
		}
		return objectAdded;
	}

	public static GameObject removeCustomObject(int x, int y, int height, int type, boolean refresh) {
		GameObject objectRemoved = null;
		for (int i = type - 1; i <= type + 2; i++) {
			objectRemoved = Region.removeObject(x, y, height, i);
			if (objectRemoved != null) {
				customObjects.remove(objectRemoved);
			}
			if (refresh) {
				if (objectRemoved != null) {
					if (objectRemoved.getLocation() == null)
						objectRemoved.setLocation(Location.locate(x, y, height));
					for (User player : Launcher.getWorld().getUsers()) {//TODO: Check if this works >.< Region.getLocalPlayers(objectRemoved.getLocation())) {
						if (player != null && player.getLocation().withinDistance(objectRemoved.getLocation())) {
							ActionSender.deleteObject(player, objectRemoved.getId(), objectRemoved.getLocation().getX(), objectRemoved.getLocation().getY(), objectRemoved.getLocation().getZ(), objectRemoved.getType(), objectRemoved.getRotation());
						}
					}
				}
			}
			if (objectRemoved != null) {
				break;
			}
		}
		return objectRemoved;
	}

	public static void addCustomObject(User owner, int objectId, int x, int y, int height, int type, int direction, boolean refresh) {
		GameObject objectAdded = Region.addObject(objectId, x, y, height, type, direction, false);
		if (objectAdded != null) {
			customObjects.add(objectAdded);
			if (refresh) {
				refresh(objectAdded);
			}
		}
	}

	public static void replaceObjectTemporarily(Location location, int newId, int delay) {
		replaceObjectTemporarily(location.getX(), location.getY(), location.getZ(), newId, delay);
	}

	public static void replaceObjectTemporarily(final int x, final int y, final int height, final int newId, final int delay) {
		final GameObject objectRemoved = removeCustomObject(x, y, height, 10, false);
		if (objectRemoved != null) { //nothing to replace if it's null
			final int oldId = objectRemoved.getId();
			addCustomObject(newId, x, y, height, 10, objectRemoved.getRotation());
			Launcher.getWorld().submit(new Tick(delay) {
				public void execute() {
					removeCustomObject(x, y, height, 10);
					refresh(addCustomObject(oldId, x, y, height, 10, objectRemoved.getRotation()));
					stop();
				}
			});
		}
	}

	public static void replaceObject(Location location, int newId) {
		replaceObject(location.getX(), location.getY(), location.getZ(), newId);
	}

	public static void replaceObject(final int x, final int y, final int height, final int newId) {
		GameObject objectRemoved = removeCustomObject(x, y, height, 10, false);
		if (objectRemoved != null) {
			addCustomObject(newId, x, y, height, 10, 0);
		}
	}

	public static void clearArea(Location loc, int depth) {
		List<GameObject> toRemove = new ArrayList<GameObject>();
		for (GameObject object : customObjects) {
			if (object.getLocation().distance(loc) <= depth) {
				toRemove.add(object);
			}
		}
		for (GameObject object : toRemove) {
			ObjectManager.removeCustomObject(object.getLocation().getX(), object.getLocation().getY(), object.getLocation().getZ(), object.getType(), true);
			customObjects.remove(object);
		}
	}

	public static void refresh() {
		for (GameObject object : customObjects) {
			if (object.getOwner() == null) {
				for (User player : Region.getLocalPlayers(object.getLocation())) {
					ActionSender.sendObject(player, object);
				}
			} else {
				ActionSender.sendObject(object.getOwner(), object);
			}
		}
	}

	public static void refresh(GameObject object) {
		if (object.getOwner() == null) {
			for (User player : Region.getLocalPlayers(object.getLocation())) {
				ActionSender.sendObject(player, object);
			}
		} else {
			ActionSender.sendObject(object.getOwner(), object);
		}
	}

	public static void refresh(User player) {
		//		for (GameObject object : removedObjects) {
			//		//	ActionSender.deleteObject(player, object.getId(), object.getLocation().getX(), object.getLocation().getY(),
					//			//		object.getLocation().getZ(), object.getType(), object.getRotation());
			//		}
		for (GameObject object : customObjects) {
			if (object.getOwner() == null) {
				ActionSender.sendObject(player, object);
			} else {
				ActionSender.sendObject(object.getOwner(), object);
			}
		}
	}

	public static void init() {
		System.out.println("Loading objects...");
		//theiving stalls
		ObjectManager.addCustomObject(4874, 3046, 3502, 0, 10, 0, false);
		ObjectManager.addCustomObject(4875, 3046, 3503, 0, 10, 0, false);
		ObjectManager.addCustomObject(4876, 3046, 3504, 0, 10, 0, false);
		ObjectManager.addCustomObject(4877, 3046, 3505, 0, 10, 0, false);
		ObjectManager.addCustomObject(4878, 3046, 3506, 0, 10, 0, false);
	//end of theiving stalls
		ObjectManager.addCustomObject(2352, 3568, 9677, 0, 10, 0, false); //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3568, 9711, 0, 10, 0, false); //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3568, 9694, 0, 10, 0, false);  //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3551, 9711, 0, 10, 0, false);  //climbing rope @ barrows
		ObjectManager.addCustomObject(1293, 2540, 2849, 0, 10, 0, false); //spirit tree
		ObjectManager.addCustomObject(2273, 2648, 9562, 0, 10, 0, false);
		ObjectManager.addCustomObject(2274, 2648, 9557, 0, 10, 0, false);
		ObjectManager.addCustomObject(2465, 2683, 9504, 0, 10, 0, false);
		ObjectManager.addCustomObject(2466, 2687, 9508, 0, 10, 0, false);
		ObjectManager.addCustomObject(12128, 3198, 3425, 0, 22, 0, false);
		ObjectManager.addCustomObject(12129, 3199, 3425, 0, 22, 0, false);
		ObjectManager.addCustomObject(12130, 3199, 3424, 0, 22, 0, false);
		ObjectManager.addCustomObject(2782, 2401, 4470, 0 , 10, 0, false);
		ObjectManager.addCustomObject(409, 2388, 4451, 0 , 10, 1, false);
		//new home
		ObjectManager.addCustomObject(26972, 3053, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3052, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3051, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3050, 3484, 0 , 10, 0, false);//bank booth
		
		ObjectManager.addCustomObject(26972, 3053, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3052, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3051, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3050, 3482, 0 , 10, 0, false);//bank booth
		//garden bank booths
		/*ObjectManager.addCustomObject(26972, 3057, 3508, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3507, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3506, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3505, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3504, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3503, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3502, 0 , 10, 1, false);*/
		
		//other side
		/*ObjectManager.addCustomObject(26972, 3046, 3508, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3507, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3506, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3505, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3504, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3503, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3502, 0 , 10, 1, false);*/
		//end of garden banks
		//Triaining area fence
		ObjectManager.addCustomObject(9262, 3058, 3509, 0 , 10, 1, false);//wooden fences
		ObjectManager.addCustomObject(9262, 3059, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3060, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3061, 3509, 0 , 10, 1, false);
		//ObjectManager.addCustomObject(37, 3062, 3509, 0 , 10, 1, false);//gate
		ObjectManager.addCustomObject(9262, 3063, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3064, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3065, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3065, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3064, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3063, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3062, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3045, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3044, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3042, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3042, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3043, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3044, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3045, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3046, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3047, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3048, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3049, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3050, 3473, 0 , 10, 1, false);
		//ObjectManager.addCustomObject(52474, 3059, 3491, 0 , 10, 3, false);//furnace
		ObjectManager.addCustomObject(26814, 3059, 3491, 0 , 10, 2, false);//furnace
		ObjectManager.addCustomObject(2782, 3054, 3492, 0 , 10, 0, false);//anvil
		ObjectManager.addCustomObject(2782, 3054, 3489, 0 , 10, 0, false);//anvil
		
		//Skilling area
		//1306 = magic tree
		//1309 = yew tree
		//1307 = maple tree
		//1308 = willow tree
		//1281 = oak tree
		//1276 = normal tree
		//14859 = runite ore
		//5782 = admanite ore
		//5784 = mithrilite ore
		//5770 = Coal
		//5772 = iron ore
		//5776 = tin ore
		//5780 = copper ore
		//3044 = Furnace
		
		ObjectManager.addCustomObject(14859, 3035, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 3033, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 3042, 3476, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 3041, 3494, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 3041, 3491, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(5780, 3034, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3040, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3040, 3486, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3038, 3483, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3038, 3479, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3034, 3490, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1306, 3038, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3034, 3493, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3040, 3495, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3031, 3484, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1309, 3039, 3482, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 3046, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 3043, 3478, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1307, 3031, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3040, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3031, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3044, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3040, 3481, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1308, 3032, 3491, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3483, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3487, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3497, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3036, 3496, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3032, 3491, 0 , 10, 0, false);
		
		
		//ObjectManager.addCustomObject(1281, 3035, 3486, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3041, 3480, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3036, 3494, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3046, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3039, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3035, 3479, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3035, 3491, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3040, 3498, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1276, 3036, 3494, 0 , 10, 0, false);
		//ObjectManager.addCustomObject(1276, 3037, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3032, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3033, 3482, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3038, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3044, 3478, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3048, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3040, 3504, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3044, 3502, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3041, 3502, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3040, 3492, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(3044, 3030, 3486, 0 , 10, 4, false);
		
		
		//Start of custom skilling area by Eclipse (:
		//Trees
		ObjectManager.addCustomObject(1276, 2410, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 2412, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 2414, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 2416, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 2419, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 2421, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 2423, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 2426, 3531, 0 , 10, 0, false);
		//Mining Rocks
		ObjectManager.addCustomObject(5780, 2427, 3529, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 2427, 3528, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5773, 2427, 3527, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5770, 2427, 3526, 0 , 10, 0, false);
		ObjectManager.addCustomObject(3044, 2426, 3523, 0 , 10, 2, false);
		ObjectManager.addCustomObject(5784, 2425, 3522, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5782, 2424, 3521, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 2423, 3521, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 2422, 3521, 0 , 10, 0, false);
		
		//outside garden
		ObjectManager.addCustomObject(590, 3044, 3490, 0 , 10, 1, false);
		
	
		ObjectManager.addCustomObject(3192, 3042, 3515, 0 , 10, 1, false);
		ObjectManager.addCustomObject(2640, 3051, 3502, 0 , 10, 2, false);
		ObjectManager.addCustomObject(26972, 3053, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3054, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3055, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3056, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3057, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3050, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3049, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3048, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3047, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3046, 3508, 0 , 10, 0, false);
		/*	//theiving stalls
		ObjectManager.addCustomObject(4874, 3046, 3502, 0, 10, 0, false);
		ObjectManager.addCustomObject(4875, 3046, 3503, 0, 10, 0, false);
		ObjectManager.addCustomObject(4876, 3046, 3504, 0, 10, 0, false);
		ObjectManager.addCustomObject(4877, 3046, 3505, 0, 10, 0, false);
		ObjectManager.addCustomObject(4878, 3046, 3506, 0, 10, 0, false);
	//end of theiving stalls
	
		ObjectManager.addCustomObject(2352, 3568, 9677, 0, 10, 0, false); //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3568, 9711, 0, 10, 0, false); //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3568, 9694, 0, 10, 0, false);  //climbing rope @ barrows
		ObjectManager.addCustomObject(2352, 3551, 9711, 0, 10, 0, false);  //climbing rope @ barrows
		ObjectManager.addCustomObject(1293, 2540, 2849, 0, 10, 0, false); //spirit tree
		ObjectManager.addCustomObject(2273, 2648, 9562, 0, 10, 0, false);
		ObjectManager.addCustomObject(2274, 2648, 9557, 0, 10, 0, false);
		ObjectManager.addCustomObject(2465, 2683, 9504, 0, 10, 0, false);
		ObjectManager.addCustomObject(2466, 2687, 9508, 0, 10, 0, false);
		ObjectManager.addCustomObject(12128, 3198, 3425, 0, 22, 0, false);
		ObjectManager.addCustomObject(12129, 3199, 3425, 0, 22, 0, false);
		ObjectManager.addCustomObject(12130, 3199, 3424, 0, 22, 0, false);
		ObjectManager.addCustomObject(2782, 2401, 4470, 0 , 10, 0, false);
		ObjectManager.addCustomObject(409, 2388, 4451, 0 , 10, 1, false);
		//new home
		ObjectManager.addCustomObject(26972, 3053, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3052, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3051, 3484, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3050, 3484, 0 , 10, 0, false);//bank booth
		
		ObjectManager.addCustomObject(26972, 3053, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3052, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3051, 3482, 0 , 10, 0, false);//bank booth
		ObjectManager.addCustomObject(26972, 3050, 3482, 0 , 10, 0, false);//bank booth
		//garden bank booths
		/*ObjectManager.addCustomObject(26972, 3057, 3508, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3507, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3506, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3505, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3504, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3503, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3057, 3502, 0 , 10, 1, false);*/
		
		//other side
		/*ObjectManager.addCustomObject(26972, 3046, 3508, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3507, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3506, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3505, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3504, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3503, 0 , 10, 1, false);
		ObjectManager.addCustomObject(26972, 3046, 3502, 0 , 10, 1, false);*/
		//end of garden banks
		//Triaining area fence
	/*	ObjectManager.addCustomObject(9262, 3058, 3509, 0 , 10, 1, false);//wooden fences
		ObjectManager.addCustomObject(9262, 3059, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3060, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3061, 3509, 0 , 10, 1, false);
		//ObjectManager.addCustomObject(37, 3062, 3509, 0 , 10, 1, false);//gate
		ObjectManager.addCustomObject(9262, 3063, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3064, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3065, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3065, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3064, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3063, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3062, 3498, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3045, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3044, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3042, 3509, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3042, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3043, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3044, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3045, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3046, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3047, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3048, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3049, 3473, 0 , 10, 1, false);
		ObjectManager.addCustomObject(9262, 3050, 3473, 0 , 10, 1, false);
		//ObjectManager.addCustomObject(52474, 3059, 3491, 0 , 10, 3, false);//furnace
		ObjectManager.addCustomObject(26814, 3059, 3491, 0 , 10, 2, false);//furnace
		ObjectManager.addCustomObject(2782, 3054, 3492, 0 , 10, 0, false);//anvil
		ObjectManager.addCustomObject(2782, 3054, 3489, 0 , 10, 0, false);//anvil
		
		//Skilling area
		//1306 = magic tree
		//1309 = yew tree
		//1307 = maple tree
		//1308 = willow tree
		//1281 = oak tree
		//1276 = normal tree
		//14859 = runite ore
		//5782 = admanite ore
		//5784 = mithrilite ore
		//5770 = Coal
		//5772 = iron ore
		//5776 = tin ore
		//5780 = copper ore
		//3044 = Furnace
		
		ObjectManager.addCustomObject(14859, 3035, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 3033, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 3042, 3476, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 3041, 3494, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 3041, 3491, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(5780, 3034, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3040, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3040, 3486, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3038, 3483, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3038, 3479, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5780, 3034, 3490, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1306, 3038, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3034, 3493, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3040, 3495, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 3031, 3484, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1309, 3039, 3482, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 3046, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 3043, 3478, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1307, 3031, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3040, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3031, 3484, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3044, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 3040, 3481, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1308, 3032, 3491, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3483, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3487, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3040, 3497, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3036, 3496, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 3032, 3491, 0 , 10, 0, false);
		
		
		//ObjectManager.addCustomObject(1281, 3035, 3486, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3041, 3480, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3036, 3494, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3046, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3039, 3474, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3035, 3479, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3035, 3491, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 3040, 3498, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(1276, 3036, 3494, 0 , 10, 0, false);
		//ObjectManager.addCustomObject(1276, 3037, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3032, 3489, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3033, 3482, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3038, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3044, 3478, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3048, 3477, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3040, 3504, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3044, 3502, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3041, 3502, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 3040, 3492, 0 , 10, 0, false);
		
		ObjectManager.addCustomObject(3044, 3030, 3486, 0 , 10, 4, false);
		
		
		//Start of custom skilling area by Eclipse (:
		//Trees
		ObjectManager.addCustomObject(1276, 2410, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1276, 2412, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 2414, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1281, 2416, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1308, 2419, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1307, 2421, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1309, 2423, 3531, 0 , 10, 0, false);
		ObjectManager.addCustomObject(1306, 2426, 3531, 0 , 10, 0, false);
		//Mining Rocks
		ObjectManager.addCustomObject(5780, 2427, 3529, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5776, 2427, 3528, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5773, 2427, 3527, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5770, 2427, 3526, 0 , 10, 0, false);
		ObjectManager.addCustomObject(3044, 2426, 3523, 0 , 10, 2, false);
		ObjectManager.addCustomObject(5784, 2425, 3522, 0 , 10, 0, false);
		ObjectManager.addCustomObject(5782, 2424, 3521, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 2423, 3521, 0 , 10, 0, false);
		ObjectManager.addCustomObject(14859, 2422, 3521, 0 , 10, 0, false);
		
		//outside garden
		ObjectManager.addCustomObject(47120, 3056, 3515, 0 , 10, 0, false);
		ObjectManager.addCustomObject(6552, 3063, 3516, 0 , 10, 3, false);
		ObjectManager.addCustomObject(17010, 3046, 3515, 0 , 10, 0, false);
		ObjectManager.addCustomObject(2640, 3048, 3509, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3053, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3054, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3055, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3056, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3057, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3050, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3049, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3048, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3047, 3508, 0 , 10, 0, false);
		ObjectManager.addCustomObject(26972, 3046, 3508, 0 , 10, 0, false);*/
		System.out.println("Loaded " + customObjects.size() + " objects.");

		//		Region.addObject(4411, 2418, 3123, 0, 22, 0, true);
		//		Region.addObject(4411, 2418, 3125, 0, 22, 0, true);
		//		Region.addObject(4411, 2419, 3125, 0, 22, 0, true);
		//		Region.addObject(4411, 2419, 3123, 0, 22, 0, true);
		//
		//		Region.addObject(36691, 2400, 3108, 0, 22, 0, true);
	}

	public static void removeObjectTemporarily(Location location, int delay, final int type, final int dir) {
		final int x = location.getX();
		final int y = location.getY();
		final int height = location.getZ();
		GameObject objectRemoved = removeCustomObject(x, y, height, type, true);
		if (objectRemoved != null) { //nothing to replace if it's null
			final int oldId = objectRemoved.getId();
			World.getWorld().submit(new Tick(delay) {
				public void execute() {
					addCustomObject(oldId, x, y, height, type, dir);
					stop();
				}
			});
		}

	}

	public static void addObjectTemporarily(final int x, final int y, final int height, int rotation, final int type, int id, int ticks) {
		addCustomObject(id, x, y, height, type, rotation);
		World.getWorld().submit(new Tick(ticks) {
			public void execute() {
				stop();
				removeCustomObject(x, y, height, type, true);
			}
		});

	}
}
