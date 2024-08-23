package net.minetaria.friendsapi.utils;

import net.minetaria.friendsapi.FriendsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class LocationUtil {

    private File file;
    private FileConfiguration cfg;

    public LocationUtil() {
        file = new File(FriendsAPI.getInstance().getDataFolder().getPath(), "locations.yml");
        cfg = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Location getLocation(String locationPath) {
        Location endReturn = new Location(Bukkit.getWorld("world"),0,0,0);
        refresh();
        if (getConfiguration().getString(locationPath + ".World") != null)  {
            World world = Bukkit.getWorld(getConfiguration().getString(locationPath + ".World"));
            double x = getConfiguration().getDouble(locationPath + ".X");
            double y = getConfiguration().getDouble(locationPath + ".Y");
            double z = getConfiguration().getDouble(locationPath + ".Z");
            float yaw = (float) getConfiguration().getDouble(locationPath + ".Yaw");
            float pitch = (float) getConfiguration().getDouble(locationPath + ".Pitch");
            endReturn = new Location(world, x, y, z, yaw, pitch);
        }
        return endReturn;
    }

    public void setLocation(String locationPath, Location location) {
        refresh();
        getConfiguration().set(locationPath + ".World", location.getWorld().getName());
        getConfiguration().set(locationPath + ".X", location.getX());
        getConfiguration().set(locationPath + ".Y", location.getY());
        getConfiguration().set(locationPath + ".Z", location.getZ());
        getConfiguration().set(locationPath + ".Pitch", location.getPitch());
        getConfiguration().set(locationPath + ".Yaw", location.getYaw());
        save();
    }

    public void refresh() {
        try {
            cfg.load(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public FileConfiguration getConfiguration() {
        return cfg;
    }

    public File getFile() {
        return file;
    }
}
