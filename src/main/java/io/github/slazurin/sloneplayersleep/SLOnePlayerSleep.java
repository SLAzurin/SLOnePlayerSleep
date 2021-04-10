package io.github.slazurin.sloneplayersleep;

import io.github.slazurin.sloneplayersleep.listeners.SLOnePlayerSleepListeners;
import io.github.slazurin.sloneplayersleep.api.SLOnePlayerSleepApi;
import io.github.slazurin.sloneplayersleep.commands.CancelSleep;
import io.github.slazurin.sloneplayersleep.commands.DebugInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SLOnePlayerSleep extends JavaPlugin {
    private SLOnePlayerSleepApi api;
    
    @Override
    public void onEnable() {
        this.api = new SLOnePlayerSleepApi(this);
        this.loadConfig();
        this.registerListeners();
        this.registerCommands();
        getLogger().info("Enabled SLOnePlayerSleep");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("Disabled SLOnePlayerSleep");
    }
    
    private void loadConfig() {
        this.saveDefaultConfig();
    }
    
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new SLOnePlayerSleepListeners(this), this);
    }
    
    private void registerCommands() {
        this.getCommand("cancelsleep").setExecutor(new CancelSleep(this));
        this.getCommand("debuginfo").setExecutor(new DebugInfo(this));
    }

    public SLOnePlayerSleepApi getApi() {
        return api;
    }
}
