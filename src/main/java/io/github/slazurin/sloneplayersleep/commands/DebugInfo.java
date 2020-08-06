package io.github.slazurin.sloneplayersleep.commands;

import io.github.slazurin.sloneplayersleep.SLOnePlayerSleep;
import io.github.slazurin.sloneplayersleep.api.SLOnePlayerSleepApi;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class DebugInfo implements TabExecutor {
    private final SLOnePlayerSleep plugin;
    
    public DebugInfo(SLOnePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String s, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("Only players may use this command!");
            return true;
        }
        
        if (!cs.hasPermission("slops.debuginfo")) {
            cs.sendMessage("You do not have permission use this command.");
            return true;
        }
        
        if (args.length == 0) {
            apiDump(cs);
            return true;
        } else {
            cs.sendMessage("Usage with arguments not supported yet.");
            return true;
        }
        
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
        // No arguments supported yet.
        return new ArrayList<>();
    }

    private void apiDump(CommandSender cs) {
        SLOnePlayerSleepApi api = this.plugin.getApi();
        cs.sendMessage(ChatColor.GREEN + "SLOnePlayerSleep Version: " + this.plugin.getDescription().getVersion());
        cs.sendMessage(ChatColor.GREEN + "SleepTask ID: " + api.getSleepTask());
        cs.sendMessage(ChatColor.GREEN + "isSleepCanceled: " + api.isSleepCanceled());
        cs.sendMessage(ChatColor.GREEN + "getTimeUntilCanSleep: " + 
                (api.getTimeUntilCanSleep(((Player)cs).getWorld()) > 0 ?
                        api.getTimeUntilCanSleep(((Player)cs).getWorld()) 
                        : 
                        0));
    }
}
