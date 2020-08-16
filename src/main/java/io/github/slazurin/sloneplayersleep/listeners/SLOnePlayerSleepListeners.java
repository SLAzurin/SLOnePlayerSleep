package io.github.slazurin.sloneplayersleep.listeners;

import io.github.slazurin.sloneplayersleep.SLOnePlayerSleep;
import io.github.slazurin.utils.WorldUtils;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerBedLeaveEvent;

public class SLOnePlayerSleepListeners implements Listener {
    private final SLOnePlayerSleep plugin;
    
    public SLOnePlayerSleepListeners(SLOnePlayerSleep plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerBedEnterEvent(PlayerBedEnterEvent e) {
        // Never use default behavior if SuperVanish is used in the server.
        // Only 1 person online, the one who entered the bed
        // if (this.plugin.getServer().getOnlinePlayers().size() == 1) {
        //     // enable default behavior
        //     return;
        // }
        
        if (e.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            // Player did not enter bed
            return;
        }
        
        if (this.plugin.getApi().isSleepCanceled()) {
            e.setCancelled(true);
            long cdresettime = this.plugin.getApi().getTimeUntilCanSleep(e.getPlayer().getWorld());
            e.getPlayer().sendMessage(ChatColor.RED + "Please try again in " + cdresettime + " seconds");
            return;
        }
        
        Player p = e.getPlayer();
        World world = p.getWorld();
        int playerCount = this.plugin.getServer().getOnlinePlayers().size();
        
        Bukkit.getServer().spigot().broadcast(this.getSleepMsg(p));
        if (WorldUtils.isNight(world)) {
            if (playerCount != 1) {
                this.plugin.getApi().startNightToDayTask(world);
            }
            return;
        }
        
        if (world.isThundering()) {
            if (playerCount != 1) {
                this.plugin.getApi().startStopThunderingTask(world);
            }
        }
        
    }
    
    @EventHandler
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent e) {
        // cancel task (not cancelSleep!) only if no one is sleeping anymore
        int sleepingPlayerCount = WorldUtils.getNumberOfSleepingPlayers(e.getPlayer().getWorld());
        if (sleepingPlayerCount == 0) {
            this.plugin.getApi().cancelSleepTask();
        }
    }
    
    private BaseComponent[] getSleepMsg(Player p) {
        // Create clickable event: "player_name is sleeping.[CANCEL]"
        TextComponent cancelButton = new TextComponent("CANCEL");
        cancelButton.setBold(Boolean.TRUE);
        cancelButton.setColor(ChatColor.RED);
        cancelButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/cancelsleep"));
        cancelButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text( "Cancel sleep" )));
            
        ComponentBuilder fullMsg = 
                new ComponentBuilder(p.getDisplayName())
                .color(ChatColor.WHITE).append(" is sleeping. ")
                .color(ChatColor.YELLOW).append("[").color(ChatColor.WHITE)
                .append(cancelButton)
                .append("]").color(ChatColor.WHITE).bold(false)
                .event((HoverEvent)null)
                .event((ClickEvent)null);
        return fullMsg.create();
    }
    
}
