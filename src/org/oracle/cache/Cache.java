package org.oracle.cache;

import org.oracle.utilities.Logger;
import org.oracle.utilities.Logger.Type;

public class Cache {

    public static void init() {
        try {
            CacheManager.load(CacheConstants.PATH);
            Logger.log(Type.CACHE, "Cached loaded!");
        } catch (Exception e) {
        	Logger.log(Type.CACHE, "Cache failed to load.");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public static int getAmountOfItems() {
        return CacheManager.cacheCFCount(CacheConstants.ITEMDEF_IDX_ID);
    }

    public static int getAmountOfObjects() {
        return CacheManager.cacheCFCount(CacheConstants.OBJECTDEF_IDX_ID);
    }

    public static int getAmountOfInterfaces() {
        return CacheManager.containerCount(CacheConstants.INTERFACEDEF_IDX_ID);
    }

    public static int getAmountOfNpcs() {
        return CacheManager.cacheCFCount2(CacheConstants.NPCDEF_IDX_ID);
    }

    public static int getAmountOfGraphics() {
        return CacheManager.cacheCFCount(CacheConstants.GFX_IDX_ID);
    }

    public static int getAmountOfAnims() {
        return CacheManager.cacheCFCount2(CacheConstants.ANIM_IDX_ID);
    }

}
