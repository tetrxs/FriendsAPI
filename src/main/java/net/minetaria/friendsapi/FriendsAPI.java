package net.minetaria.friendsapi;

import de.gaunercools.languageapibukkit.mysql.LanguageAPI;
import net.minetaria.friendsapi.inventories.FriendsInventory;
import net.minetaria.friendsapi.listeners.PlayerJoinListener;
import net.minetaria.friendsapi.utils.SQLUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public final class FriendsAPI extends JavaPlugin {

    public static FriendsAPI instance;
    private SQLUtil sqlUtil;

    public final static String prefix = "§5§lFriends §8• §7";

    @Override
    public void onEnable() {
        instance = this;

        sqlUtil = new SQLUtil("jdbc:mysql://116.202.235.165:3306/FriendsAPI", "minetaria", "2S3jYDYC9rfLi4P7");
        try {
            sqlUtil.executeUpdate("CREATE TABLE IF NOT EXISTS `friendsAPI_playerData` (`uuid` VARCHAR(100) NOT NULL , `name` VARCHAR(100) NOT NULL , `online` INT NOT NULL , `server` VARCHAR(100) NOT NULL , `lastLogin` BIGINT NOT NULL ) ENGINE = InnoDB;");
            sqlUtil.executeUpdate("CREATE TABLE IF NOT EXISTS `friendsAPI_list` (`uuid` VARCHAR(100) NOT NULL , `friends` LONGTEXT NOT NULL) ENGINE = InnoDB;");
            sqlUtil.executeUpdate("CREATE TABLE IF NOT EXISTS `friendsAPI_requests` (`senderUUID` VARCHAR(100) NOT NULL , `targetUUID` VARCHAR(100) NOT NULL ) ENGINE = InnoDB;");
            Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    try {
                        sqlUtil.executeQuery("SELECT 1");
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, 0, 15 * 20);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public static void executeBungeeCommand(Player player, String command){
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("bungeeCommandExecutionFriendsAPI");
            out.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
    }

    //OPEN MENU
    public final static void openFriendsMenu(Player player) {
        new FriendsInventory(LanguageAPI.getTranslatedMessage(player,"friends_friendsMenu"),54).openInventory(player);
    }

    public final static UUID getUUIDfromName(String name) {
        UUID endReturn = null;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `name` = '" + name + "';");
            if (rs.next()) {
                endReturn = UUID.fromString(rs.getString("uuid"));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static String getNamefromUUID(UUID uuid) {
        String endReturn = null;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getString("name");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static boolean isOnline(UUID uuid) {
        boolean endReturn = false;
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getBoolean("online");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static String getServerName(UUID uuid) {
        String endReturn = "";
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getString("server");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    public final static long getLastLogin(UUID uuid) {
        long endReturn = System.currentTimeMillis();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `uuid` = '" + uuid.toString() + "';");
            if (rs.next()) {
                endReturn = rs.getLong("lastLogin");
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    //CREATE FRIENDS
    public final static boolean pairFriends(String uuid1, String uuid2) {
        boolean succes = false;
        succes = addUuidToFriendList(uuid1,uuid2);
        succes = addUuidToFriendList(uuid2,uuid1);
        return succes;
    }
    private final static boolean addUuidToFriendList(String uuid1, String uuid2) {
        boolean succes = false;
        ArrayList<String> gettedFriends = getFriends(uuid1);
        StringBuilder newFriends = new StringBuilder("");
        if (!gettedFriends.isEmpty()) {
            for (String s:gettedFriends) {
                newFriends.append(s + ";");
            }
        }
        if (!newFriends.toString().contains(uuid2)) {
            newFriends.append(uuid2 + ";");
            try {
                getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_list` SET `friendsAPI_list`.`friends` = '" + newFriends.toString() + "' WHERE `friendsAPI_list`.`uuid` = '" + uuid1 + "';");
                succes = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return succes;
    }

    //DESTROY FRIENDS
    public final static boolean removeFriends(String uuid1, String uuid2) {
        boolean succes = false;
        succes = removeUuidToFriendList(uuid1,uuid2);
        succes = removeUuidToFriendList(uuid2,uuid1);
        return succes;
    }
    private final static boolean removeUuidToFriendList(String uuid1, String uuid2) {
        boolean succes = false;
        ArrayList<String> gettedFriends = getFriends(uuid1);
        StringBuilder newFriends = new StringBuilder("");
        if (!gettedFriends.isEmpty()) {
            for (String s:gettedFriends) {
                newFriends.append(s + ";");
            }
        }
        if (newFriends.toString().contains(uuid2)) {
            String temp = newFriends.toString();
            temp = temp.replaceAll(uuid2 + ";", "");
            try {
                getInstance().getSqlUtil().executeUpdate("UPDATE `friendsAPI_list` SET `friendsAPI_list`.`friends` = '" + temp + "' WHERE `friendsAPI_list`.`uuid` = '" + uuid1 + "';");
                succes = true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return succes;
    }

    //GET FRIENDS
    public final static ArrayList<String> getFriends(String uuid) {
        ArrayList<String> endReturn = new ArrayList<>();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_list` WHERE `friendsAPI_list`.`uuid` = '" + uuid + "';");
            if (rs.next()) {
                String[] temp = rs.getString("friends").split(";");
                for (String t:temp) {
                    if (t.length() > 5) {
                        endReturn.add(t);
                    }
                }
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }

    //SEND FRIEND REQUEST
    public final static boolean sendFriendRequest(String senderUUID, String targetUUID) {
        boolean succes = false;
        boolean exists = false;
        try {
            ResultSet rs = FriendsAPI.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_requests` WHERE (`friendsAPI_requests`.`senderUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`targetUUID` = '" + targetUUID + "') OR (`friendsAPI_requests`.`targetUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`senderUUID` = '" + targetUUID + "');");
            exists = rs.next();
            rs.close();
            if (!exists) {
                FriendsAPI.getInstance().getSqlUtil().executeUpdate("INSERT INTO `friendsAPI_requests` (`senderUUID`, `targetUUID`) VALUES ('" + senderUUID + "', '" + targetUUID + "')");
                succes = true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return succes;
    }

    //REMOVE FRIEND REQUEST
    public final static boolean removeFriendRequest(String senderUUID, String targetUUID) {
        boolean succes = false;
        boolean exists = false;
        try {
            FriendsAPI.getInstance().getSqlUtil().executeUpdate("DELETE FROM `friendsAPI_requests` WHERE `friendsAPI_requests`.`senderUUID` = '" + senderUUID + "' AND `friendsAPI_requests`.`targetUUID` = '" + targetUUID + "';");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return succes;
    }

    //GET FRIEND REQUESTS
    public final static ArrayList<String> getFriendRequests(String uuid) {
        ArrayList<String> endReturn = new ArrayList<>();
        try {
            ResultSet rs = getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_requests` WHERE `friendsAPI_requests`.`targetUUID` = '" + uuid + "';");
            if (rs.next()) {
                endReturn.add(rs.getString("senderUUID"));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return endReturn;
    }




    @Override
    public void onDisable() {
        try {
            sqlUtil.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static FriendsAPI getInstance() {
        return instance;
    }

    public SQLUtil getSqlUtil() {
        return sqlUtil;
    }
}
