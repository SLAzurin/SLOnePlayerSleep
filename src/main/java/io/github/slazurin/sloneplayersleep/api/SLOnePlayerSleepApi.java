package io.github.slazurin.sloneplayersleep.api;

import io.github.slazurin.utils.WorldUtils;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class SLOnePlayerSleepApi {
    private final Plugin plugin;
    private boolean sleepCanceled;
    private int sleepTask;
    private long sleepCooldownResetTaskEndTime;
    
    public SLOnePlayerSleepApi(Plugin plugin) {
        this.plugin = plugin;
        this.sleepCanceled = false;
        this.sleepTask = -1;
        this.sleepCooldownResetTaskEndTime = -1;
    }

    public boolean isSleepCanceled() {
        return sleepCanceled;
    }

    public void setSleepCanceled(boolean sleepCanceled) {
        this.sleepCanceled = sleepCanceled;
    }

    public int getSleepTask() {
        return sleepTask;
    }

    public void setSleepTask(int sleepTask) {
        this.sleepTask = sleepTask;
    }
    
    // returns true if the task started, otherwise false
    public boolean startNightToDayTask(World world) {
        
        // Check if there is already a sleep task or if the sleep task was canceled recently
        if (this.getSleepTask() > 0 || this.isSleepCanceled()) {
            return false;
        }
        
        int sleepdelay = this.plugin.getConfig().getInt("sleepdelay", 5);
        
        if (!this.isSleepCanceled()) {
            this.setSleepTask(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                long offsetUntilMorning = 25000L - world.getTime();
                WorldUtils.clearWeather(world);
                world.setFullTime(world.getFullTime() + offsetUntilMorning);
                this.setSleepTask(-1);
            }, sleepdelay * 20));
            return true;
        }
        return false;
    }
    
    // returns true if the task started, otherwise false
    public boolean startStopThunderingTask(World world) {
        
        // Check if there is already a sleep task or if the sleep task was canceled recently
        if (this.getSleepTask() > 0 || this.isSleepCanceled()) {
            return false;
        }
        
        int sleepdelay = this.plugin.getConfig().getInt("sleepdelay", 5);
        
        if (!this.isSleepCanceled()) {
            this.setSleepTask(this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                WorldUtils.clearWeather(world);
                this.setSleepTask(-1);
            }, sleepdelay * 20));
        }
        return false;
    }
    
    public boolean cancelSleepTask() {
        if (this.getSleepTask() > 0) {
            this.plugin.getServer().getScheduler().cancelTask(this.getSleepTask());
            this.setSleepTask(-1);
            return true;
        }
        return false;
    }
    
    public boolean cancelSleep(World w) {
        boolean success = false;
        
        if (this.isSleepCanceled()) {
            return false;
        }
        
        if (this.cancelSleepTask()) {
            success = true;
        }
        
        if (this.kickPlayersOffBed()) {
            success = true;
        }
        
        if (success) {
            this.setSleepCanceled(true);
            this.startSleepCooldownResetTask(w);
        }
        
        return success;
    }

    public long getSleepCooldownResetTaskEndTime() {
        return sleepCooldownResetTaskEndTime;
    }

    public void setSleepCooldownResetTaskEndTime(long sleepCooldownResetTaskEndTime) {
        this.sleepCooldownResetTaskEndTime = sleepCooldownResetTaskEndTime;
    }
    
    public long getTimeUntilCanSleep(World w) {
        return (this.getSleepCooldownResetTaskEndTime() - w.getFullTime()) / 20 + 1;
    }

    private boolean kickPlayersOffBed() {
        boolean kickPlayerOffBed = false;
        for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            if (p.isSleeping()) {
                kickPlayerOffBed = true;
                GameMode oldGameMode = null;
                if (p.getGameMode() != GameMode.SURVIVAL) {
                    oldGameMode = p.getGameMode();
                }
                p.setGameMode(GameMode.SURVIVAL);
                p.damage(0);
                if (oldGameMode != null) {
                    p.setGameMode(oldGameMode);
                }
            }
        }
        
        return kickPlayerOffBed;
    }

    private void startSleepCooldownResetTask(World w) {
        // Set timer to reset sleep cooldown
        int sleepcooldownreset = this.plugin.getConfig().getInt("sleepcooldownreset", 10);

        this.setSleepCooldownResetTaskEndTime(w.getFullTime() + (sleepcooldownreset * 20));

        this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.setSleepCanceled(false);
                this.setSleepCooldownResetTaskEndTime(-1);
            }, sleepcooldownreset * 20);
    }
}
