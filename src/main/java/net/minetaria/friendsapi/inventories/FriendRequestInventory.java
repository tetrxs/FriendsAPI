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

import java.util.Arrays;
import java.util.UUID;

public class FriendRequestInventory extends InventoryUtil {

    private String senderUUID;
    private String targetUUID;

    public FriendRequestInventory(String inventoryTitle, int inventorySize, String senderUUID, String targetUUID) {
        super(inventoryTitle, inventorySize);
        this.senderUUID = senderUUID;
        this.targetUUID = targetUUID;
    }

    @Override
    public void OnOpenInventory(Player player) {
        for (int i = 0; i < getInventory().getSize(); i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }
        ItemStack senderIs = SkullUtil.getPlayerSkull(UUID.fromString(senderUUID));
        ItemMeta senderIm = senderIs.getItemMeta();
        senderIm.setItemName("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(senderUUID)));
        senderIm.addEnchant(Enchantment.POWER,1,true);
        senderIs.setItemMeta(senderIm);
        getInventory().setItem(13,senderIs);
        getInventory().setItem(11, new ItemUtil(Material.GREEN_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequestsAccept")).build());
        getInventory().setItem(15, new ItemUtil(Material.RED_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequestsDecline")).build());
    }

    @Override
    public void OnCloseInventory(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void OnThisInventoryClicked(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS)) {
                FriendsAPI.executeBungeeCommand(getInventoryPlayer(),"/friend accept " + FriendsAPI.getNamefromUUID(UUID.fromString(senderUUID)));
                getInventoryPlayer().closeInventory();
            } else if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS)) {
                FriendsAPI.executeBungeeCommand(getInventoryPlayer(),"/friend decline " + FriendsAPI.getNamefromUUID(UUID.fromString(senderUUID)));
                Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        new FriendsInventory(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu"),54).openInventory(getInventoryPlayer());
                    }
                },1);
                getInventoryPlayer().closeInventory();
            }
        }
    }
}
