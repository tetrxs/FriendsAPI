package net.minetaria.friendsapi.inventories;

import de.gaunercools.languageapibukkit.mysql.LanguageAPI;
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
import java.util.UUID;

public class FriendRemoveInventory extends InventoryUtil {

    private String removeUUID;

    public FriendRemoveInventory(String inventoryTitle, int inventorySize, String removeUUID) {
        super(inventoryTitle, inventorySize);
        this.removeUUID = removeUUID;
    }

    @Override
    public void OnOpenInventory(Player player) {
        for (int i = 0; i < getInventory().getSize(); i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }
        if (FriendsAPI.isOnline(UUID.fromString(removeUUID))) {
            ItemStack onlineFriendIs = SkullUtil.getPlayerSkull(UUID.fromString(removeUUID));
            ItemMeta onlineFriendIm = onlineFriendIs.getItemMeta();
            onlineFriendIm.setItemName("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(removeUUID)));
            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOnlineStatusLore").replaceAll("%STATUS%", "§aOnline").replaceAll("%SERVER%", "§e" + FriendsAPI.getServerName(UUID.fromString(removeUUID))).replaceAll("%LANGUAGE%", "§2" + LanguageAPI.getMySQLLanguage(removeUUID)).split(";");
            onlineFriendIm.setLore(Arrays.asList(lore1));
            onlineFriendIm.addEnchant(Enchantment.POWER,1,true);
            onlineFriendIs.setItemMeta(onlineFriendIm);
            getInventory().setItem(13,onlineFriendIs);
        } else {
            long yourmilliseconds = FriendsAPI.getLastLogin(UUID.fromString(removeUUID));
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
            Date resultdate = new Date(yourmilliseconds);
            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOfflineStatusLore").replaceAll("%STATUS%", "§cOffline").replaceAll("%LASTLOGIN%", "§e" + sdf.format(resultdate)).split(";");
            ItemStack offlineFriendIs = new ItemUtil(Material.SKELETON_SKULL).setName("§7" + FriendsAPI.getNamefromUUID(UUID.fromString(removeUUID))).setLore(lore1).build();
            getInventory().setItem(13,offlineFriendIs);
        }
        getInventory().setItem(11, new ItemUtil(Material.GREEN_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRemoveConfirm")).build());
        getInventory().setItem(15, new ItemUtil(Material.RED_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRemoveCancel")).build());
    }

    @Override
    public void OnCloseInventory(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void OnThisInventoryClicked(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS)) {
                Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        new FriendInventory("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(removeUUID)),27,UUID.fromString(removeUUID).toString()).openInventory(player);
                    }
                },1);
                getInventoryPlayer().closeInventory();
            } else if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS)) {
                FriendsAPI.executeBungeeCommand(getInventoryPlayer(),"/friend remove " + FriendsAPI.getNamefromUUID(UUID.fromString(removeUUID)));
                getInventoryPlayer().closeInventory();
            }
        }
    }
}
