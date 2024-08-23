package net.minetaria.friendsapi.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StringUtil {

    private JavaPlugin instance;
    private FileConfiguration config;

    private String endReturn = "";

    public StringUtil(JavaPlugin instance) {
        this.instance = instance;
        instance.getConfig().options().copyDefaults(true);
        instance.saveDefaultConfig();
        this.config = instance.getConfig();
    }

    public StringUtil getConfigString(String stringName) {
        this.endReturn = this.config.getString(stringName);
        return this;
    }

    public StringUtil replaceChars(String a, String b) {
        this.endReturn = this.endReturn.replaceAll(a,b);
        return this;
    }

    public String getPrefix() {
        return this.config.getString("messages.prefix").replaceAll("§","&");
    }

    public String build() {
        String temp = this.endReturn;
        this.endReturn = "";
        return temp;
    }
}
