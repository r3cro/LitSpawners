package me.recro.litspawners.litspawners.utils;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Created by Giros on 12/27/2018 in untitled.
 * Copyright (c) 2018 Giros
 **/
public class VaultHandler {

    public static VaultHandler instance;
    public static Economy economy;

    static {
        VaultHandler.instance = new VaultHandler();
        VaultHandler.economy = null;
    }

    public static VaultHandler getInstance() {
        return VaultHandler.instance;
    }

    public boolean setupEconomy(){
        if(Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>)Bukkit.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (rsp == null) {
            return false;
        }
        VaultHandler.economy = rsp.getProvider();
        return VaultHandler.economy != null;
    }

}
