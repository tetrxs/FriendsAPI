package net.minetaria.friendsapi.inventories;

import de.gaunercools.languageapibukkit.mysql.LanguageAPI;
import eu.thesimplecloud.api.CloudAPI;
import kotlin.Unit;
import net.minetaria.friendsapi.FriendsAPI;
import net.minetaria.friendsapi.utils.InventoryUtil;
import net.minetaria.friendsapi.utils.ItemUtil;
import net.minetaria.friendsapi.utils.SkullUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class FriendInventory extends InventoryUtil {

    private String playerUUID;

    public FriendInventory(String inventoryTitle, int inventorySize, String playerUUID) {
        super(inventoryTitle, inventorySize);
        this.playerUUID = playerUUID;
    }

    @Override
    public void OnOpenInventory(Player player) {
        for (int i = 0; i < 9; i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }
        for (int i = 18; i < 27; i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }
        getInventory().setItem(4, new ItemUtil(Material.RED_DYE).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendClose")).build());
        getInventory().setItem(17, new ItemUtil(Material.REDSTONE_BLOCK).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendMenu_removeFriend")).build());
        if (FriendsAPI.isOnline(UUID.fromString(playerUUID))) {
            ItemStack onlineFriendIs = SkullUtil.getPlayerSkull(UUID.fromString(playerUUID));
            ItemMeta onlineFriendIm = onlineFriendIs.getItemMeta();
            onlineFriendIm.setItemName("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(playerUUID)));
            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOnlineStatusLore").replaceAll("%STATUS%", "§aOnline").replaceAll("%SERVER%", "§e" + FriendsAPI.getServerName(UUID.fromString(playerUUID))).replaceAll("%LANGUAGE%", "§2" + LanguageAPI.getMySQLLanguage(playerUUID)).split(";");
            onlineFriendIm.setLore(Arrays.asList(lore1));
            onlineFriendIm.addEnchant(Enchantment.POWER,1,true);
            onlineFriendIs.setItemMeta(onlineFriendIm);
            getInventory().setItem(22,onlineFriendIs);
            getInventory().setItem(9, new ItemUtil(Material.CAKE).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendMenu_partyInvite")).build());
            getInventory().setItem(10, new ItemUtil(Material.CYAN_BANNER).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendMenu_clanInvite")).build());
            getInventory().setItem(11, new ItemUtil(Material.GOLDEN_BOOTS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendMenu_jumpTo")).build());
            getInventory().setItem(12, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(13, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(14, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(15, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(16, new ItemUtil(Material.BARRIER).setName("§1").build());
        } else {
            long yourmilliseconds = FriendsAPI.getLastLogin(UUID.fromString(playerUUID));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOfflineStatusLore").replaceAll("%STATUS%", "§cOffline").replaceAll("%LASTLOGIN%", "§e" + sdf.format(resultdate)).split(";");
            ItemStack offlineFriendIs = new ItemUtil(Material.SKELETON_SKULL).setName("§7" + FriendsAPI.getNamefromUUID(UUID.fromString(playerUUID))).setLore(lore1).build();
            getInventory().setItem(22,offlineFriendIs);
            getInventory().setItem(9, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(10, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(11, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(12, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(13, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(14, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(15, new ItemUtil(Material.BARRIER).setName("§1").build());
            getInventory().setItem(16, new ItemUtil(Material.BARRIER).setName("§1").build());
        }
    }

    @Override
    public void OnCloseInventory(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void OnThisInventoryClicked(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            if (event.getCurrentItem().getType().equals(Material.RED_DYE)) {
                Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        new FriendsInventory(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu"),54).openInventory(player);
                    }
                },1);
                getInventoryPlayer().closeInventory();
            } else if (event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
                Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        new FriendRemoveInventory(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendMenu_removeFriend"),27,playerUUID).openInventory(getInventoryPlayer());
                    }
                },1);
                getInventoryPlayer().closeInventory();
            } else if (event.getCurrentItem().getType().equals(Material.GOLDEN_BOOTS)) {
                FriendsAPI.executeBungeeCommand(player,"/friend jump " + FriendsAPI.getNamefromUUID(UUID.fromString(playerUUID)));
                getInventoryPlayer().closeInventory();
            }
        }
    }
}
