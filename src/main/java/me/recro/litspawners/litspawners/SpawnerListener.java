package me.recro.litspawners.litspawners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.RelationParticipator;
import com.massivecraft.factions.struct.Relation;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerBreakEvent;
import de.dustplanet.silkspawners.events.SilkSpawnersSpawnerPlaceEvent;
import de.dustplanet.silkspawners.listeners.SilkSpawnersBlockListener;
import me.recro.litspawners.litspawners.utils.VaultHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giros on 1/4/2019 in untitled.
 * Copyright (c) 2019 Giros
 **/
public class SpawnerListener implements Listener {

    public ArrayList<Location> spawnerLocations = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(final CreatureSpawnEvent event) {
        final Faction faction = Board.getInstance().getFactionAt(new FLocation(event.getEntity().getLocation()));
        for (String string : LitSpawners.getInstance().getConfigFile().getStringList("worlds-spawning-enabled")) {
            if (event.getEntity().getLocation().getWorld().getName().equalsIgnoreCase(string)) {
                if (faction.isWilderness() && event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawnerBreak(SilkSpawnersSpawnerBreakEvent event) {
        final Player player = event.getPlayer();
        final FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        final int radius = LitSpawners.getInstance().getConfigFile().getInt("radius");
        final String closeMessage = LitSpawners.getInstance().getConfigFile().getString("enemy-close");
        final String minedMessage = LitSpawners.getInstance().getConfigFile().getString("mined");


        final String noMoney = LitSpawners.getInstance().getConfigFile().getString("no-money");
        final String notax = LitSpawners.getInstance().getConfigFile().getString("no-tax");

        final List<Player> nearbyPlayers = this.getNearbyPlayers(player, radius);
        for (final Player near : nearbyPlayers) {
            final FPlayer fNear = FPlayers.getInstance().getByPlayer(near);
            if (fPlayer.getRelationTo(fNear).equals(Relation.ENEMY)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', String.valueOf(closeMessage.replaceAll("%radius%", new StringBuilder(String.valueOf(radius)).toString()).replaceAll("%newline%", "\n"))));
                return;
            }
        }

        if (spawnerLocations.contains(event.getBlock().getLocation())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', notax).replaceAll("%newline%", "\n"));
            return;
        }

        final int tax = LitSpawners.getInstance().getConfigFile().getInt("spawner-tax.worldname." + player.getLocation().getWorld().getName() + "." + event.getSpawner().getCreatureTypeName().toLowerCase());
        if (tax == 0) return;
        if (VaultHandler.economy.getBalance(event.getPlayer()) >= tax) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', minedMessage.replaceAll("%cost%", String.valueOf(tax)).replaceAll("%newline%", "\n")));
            VaultHandler.economy.withdrawPlayer(player, tax);
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', noMoney.replaceAll("%cost%", String.valueOf(tax)).replaceAll("%newline%", "\n")));
            event.setCancelled(true);
        }
    }

    private List<Player> getNearbyPlayers(final Player player, final double radius) {
        final List<Player> players = new ArrayList<>();
        for (final Entity ent : player.getNearbyEntities(radius, radius, radius)) {
            if (ent instanceof Player) {
                final Player entPlayer = (Player) ent;
                players.add(entPlayer);
            }
        }
        return players;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(SilkSpawnersSpawnerPlaceEvent event) {
        final Player player = event.getPlayer();
        final String taxinfo = LitSpawners.getInstance().getConfigFile().getString("placed");
        final int tax = LitSpawners.getInstance().getConfigFile().getInt("spawner-tax.worldname." + player.getLocation().getWorld().getName() + "." + event.getSpawner().getCreatureTypeName().toLowerCase());
        final String name = LitSpawners.getInstance().getConfigFile().getString("spawner-tax.worldname." + player.getLocation().getWorld().getName() + "." + event.getSpawner().getCreatureTypeName().toLowerCase() + ".spawnername");
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', taxinfo.replaceAll("%cost%", String.valueOf(tax)).replaceAll("%newline%", "\n")));
        spawnerLocations.add(event.getBlock().getLocation());
        final BukkitScheduler scheduler = LitSpawners.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(LitSpawners.getInstance(), new Runnable() {
            @Override
            public void run() {
                spawnerLocations.remove(event.getBlock().getLocation());
            }
        }, (long) (LitSpawners.getInstance().getConfigFile().getInt("no-tax-time") * 20));
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Action action = event.getAction();
        final String taxinfo = LitSpawners.getInstance().getConfigFile().getString("tax-info");
        if (action == Action.LEFT_CLICK_BLOCK) {
            if (!event.getClickedBlock().getType().equals(Material.MOB_SPAWNER)) {
                return;
            } else {
                final CreatureSpawner cs = (CreatureSpawner) event.getClickedBlock().getState();
                final EntityType spawnerType = cs.getSpawnedType();
                event.getPlayer().sendMessage(taxinfo.replaceAll("%cost%", LitSpawners.getInstance().getConfigFile().getString("spawner-tax.worldname." + event.getPlayer().getLocation().getWorld().getName() + "." + spawnerType.name().toLowerCase())).replaceAll("%newline%", "\n"));
            }
        }
    }

}