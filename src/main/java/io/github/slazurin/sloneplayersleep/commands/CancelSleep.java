package io.github.slazurin.sloneplayersleep.commands;

import io.github.slazurin.sloneplayersleep.SLOnePlayerSleep;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

/**
 *
 * @author Azuri
 */
public class CancelSleep implements TabExecutor {
    private final SLOnePlayerSleep plugin;
    
    public CancelSleep(SLOnePlayerSleep plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender == null) return true;
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
        }
        
        if (!sender.hasPermission("slops.cancelsleep")) {
            sender.sendMessage("You do not have permission use this command.");
            return true;
        }
        
        
        if (this.plugin.getApi().cancelSleep(((Player) sender).getWorld())) {
            this.plugin.getServer().sendMessage(net.kyori.adventure.text.Component.text(ChatColor.RED + sender.getName() + " canceled sleep."));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
        return new ArrayList<>();
    }
    
}
