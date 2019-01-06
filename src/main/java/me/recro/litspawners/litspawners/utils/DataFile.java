package me.recro.litspawners.litspawners.utils;

/**
 * Created by Giros on 1/4/2019 in untitled.
 * Copyright (c) 2019 Giros
 **/
import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.*;
import java.io.*;
import org.bukkit.*;
import java.util.*;

public class DataFile {
    private File file;
    private YamlConfiguration configuration;

    public DataFile(final JavaPlugin plugin, final String name) {
        this.file = new File(plugin.getDataFolder(), name + ".yml");
        if (!this.file.getParentFile().exists()) {
            this.file.getParentFile().mkdir();
        }
        plugin.saveResource(name + ".yml", false);
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void load() {
        this.configuration = YamlConfiguration.loadConfiguration(this.file);
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public double getDouble(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0;
    }

    public int getInt(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public Set<String> getConfigurationSection(final String path, final boolean deep) {
        if (this.configuration.contains(path)) {
            return (Set<String>)this.configuration.getConfigurationSection(path).getKeys(deep);
        }
        return null;
    }

    public boolean getBoolean(final String path) {
        return this.configuration.contains(path) && this.configuration.getBoolean(path);
    }

    public String getString(final String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return "";
    }

    public List<String> getStringList(final String path) {
        if (this.configuration.contains(path)) {
            final List<String> strings = new ArrayList<String>();
            this.configuration.getStringList(path).forEach(string -> strings.add(ChatColor.translateAlternateColorCodes('&', string)));
            return strings;
        }
        return Collections.singletonList("Invalid path.");
    }
}
