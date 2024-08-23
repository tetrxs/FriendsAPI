package net.minetaria.friendsapi.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullUtil {

    // Methode für Skull über Spielernamen
    public static ItemStack getPlayerSkull(String playerName) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer != null) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));
            skull.setItemMeta(meta);
            return skull;
        } else {
            return new ItemStack(Material.SKELETON_SKULL);
        }
    }

    // Methode für Skull über Spieler-UUID
    public static ItemStack getPlayerSkull(UUID playerUUID) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        skull.setItemMeta(meta);

        return skull;
    }

    // Methode für Skull über Base64-Value
    public static ItemStack getCustomSkullFromValue(String value) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), null);
        PlayerTextures textures = profile.getTextures();

        try {
            // JSON-String aus dem Base64-dekodierten Wert
            String json = new String(Base64.getDecoder().decode(value));

            // Extrahiere die URL aus der JSON-Struktur
            String urlPart = json.split("\"url\":\"")[1].split("\"")[0];

            // Vollständige URL konstruieren, falls nur ein Hash übergeben wurde
            URL url;
            if (!urlPart.startsWith("http")) {
                url = new URL("http://textures.minecraft.net/texture/" + urlPart);
            } else {
                url = new URL(urlPart);
            }

            // Setze die Skin-URL im PlayerTextures-Objekt
            textures.setSkin(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();  // Fehlerbehandlung bei ungültiger URL
        } catch (Exception e) {
            e.printStackTrace();  // Allgemeine Fehlerbehandlung
        }

        profile.setTextures(textures);
        meta.setOwnerProfile(profile);
        skull.setItemMeta(meta);

        return skull;
    }

    // Methode für Skull über Minecraft-URL
    public static ItemStack getCustomSkullFromURL(String url) {
        try {
            String base64 = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes());
            return getCustomSkullFromValue(base64);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
