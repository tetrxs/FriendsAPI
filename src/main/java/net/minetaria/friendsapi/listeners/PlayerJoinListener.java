package net.minetaria.friendsapi.listeners;

import net.minetaria.friendsapi.FriendsAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        //INIT FRIENDS LIST
        boolean exists = false;
        try {
            ResultSet rs = FriendsAPI.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_list` WHERE `friendsAPI_list`.`uuid` = '" + player.getUniqueId().toString() + "';");
            exists = rs.next();
            rs.close();
            if (!exists) {
                FriendsAPI.getInstance().getSqlUtil().executeUpdate("INSERT INTO `friendsAPI_list` (`uuid`, `friends`) VALUES ('" + player.getUniqueId().toString() + "', '')");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
