package io.github.slazurin.utils;

import java.util.List;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldUtils {
    public static boolean isDay(World w) {
        long time = w.getFullTime() % 24000L;
        return time >= 1000L && time < 12541L;
    }
    public static boolean isNight(World w) {
        return !WorldUtils.isDay(w);
    }
    
    public static int getNumberOfSleepingPlayers(World w) {
        List<Player> ps = w.getPlayers();
        int count = 0;
        for (Player p : ps) {
            if (p.isSleeping()) {
                count++;
            }
        }
        return count;
    }
    
    public static void clearWeather(World w) {
        w.setStorm(false);
        w.setThundering(false);
    }
}
