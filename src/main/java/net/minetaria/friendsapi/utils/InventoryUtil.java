package net.minetaria.friendsapi.utils;

import net.minetaria.friendsapi.FriendsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public abstract class InventoryUtil implements Listener {

    private org.bukkit.inventory.Inventory inventory;
    private Player inventoryPlayer;
    private String inventoryTitle;

    private boolean opened;
    private boolean clicked;

    public InventoryUtil(String inventoryTitle, int inventorySize) {
        this.inventoryTitle = inventoryTitle;
        inventory = Bukkit.createInventory(null, inventorySize, inventoryTitle);
    }

    public abstract void OnOpenInventory(Player player);
    public abstract void OnCloseInventory(Player player, InventoryCloseEvent event);
    public abstract void OnThisInventoryClicked(Player player, InventoryClickEvent event);

    public void openInventory(Player player) {
        inventoryPlayer = player;
        OnOpenInventory(player);
        opened = true;
        clicked = false;
        Bukkit.getPluginManager().registerEvents(this, FriendsAPI.getInstance());
        player.openInventory(inventory);
    }

    public org.bukkit.inventory.Inventory getInventory() {
        return inventory;
    }

    public Player getInventoryPlayer() {
        return inventoryPlayer;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    @EventHandler
    public void OnInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equals(inventoryTitle) && event.getView().getPlayer().equals(inventoryPlayer)) {
            if (!clicked) {
                clicked = true;
                OnThisInventoryClicked(player,event);
                Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        clicked = false;
                    }
                },1);
            }
        }
    }

    @EventHandler
    public void OnInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getView().getTitle().equals(inventoryTitle) && event.getView().getPlayer().equals(inventoryPlayer) && opened) {
            opened = false;
            HandlerList.unregisterAll(this);
            OnCloseInventory(player, event);
        }
    }
}


