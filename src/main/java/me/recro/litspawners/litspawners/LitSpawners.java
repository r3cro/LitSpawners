package me.recro.litspawners.litspawners;

import me.recro.litspawners.litspawners.utils.DataFile;
import me.recro.litspawners.litspawners.utils.VaultHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class LitSpawners extends JavaPlugin {

    public static LitSpawners instance;
    private DataFile configFile;

    @Override
    public void onEnable() {
        instance = this;
        this.configFile = new DataFile(this, "config");
        this.getServer().getPluginManager().registerEvents(new SpawnerListener(), this);
        if(!VaultHandler.getInstance().setupEconomy()) {
            this.getLogger().severe(String.format("Disabled due to no Vault dependency found!", this.getDescription().getName()));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {

    }

    public static LitSpawners getInstance() {
        return LitSpawners.instance;
    }

    public DataFile getConfigFile() {
        return this.configFile;
    }
}
