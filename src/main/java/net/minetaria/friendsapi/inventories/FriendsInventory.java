package net.minetaria.friendsapi.inventories;

import de.gaunercools.languageapibukkit.mysql.LanguageAPI;
import net.minetaria.friendsapi.FriendsAPI;
import net.minetaria.friendsapi.utils.InventoryUtil;
import net.minetaria.friendsapi.utils.ItemUtil;
import net.minetaria.friendsapi.utils.SkullUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import sun.tools.jconsole.Plotter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FriendsInventory extends InventoryUtil {

    private FriendsMenuState friendsMenuState;
    private int site;
    private ArrayList<ItemStack> siteDisplayableItems;

    public FriendsInventory(String inventoryTitle, int inventorySize) {
        super(inventoryTitle, inventorySize);
        friendsMenuState = FriendsMenuState.FRIENDS;
        site = 1;
        siteDisplayableItems = new ArrayList<>();
    }

    private void initFriendsMenu() {

        site = 1;
        siteDisplayableItems.clear();
        getInventory().clear();

        for (int i = 0; i < 9; i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }
        for (int i = 45; i < 54; i++) {
            getInventory().setItem(i, new ItemUtil(Material.GRAY_STAINED_GLASS_PANE).setName("§7").setAmount(1).build());
        }

        ItemStack arrowRightIs = SkullUtil.getCustomSkullFromURL("1a20d6ef126813066a2577ccc5df7102683f1af7c3e94e45ab9ad67edbb52");
        ItemMeta arrowRightIm = arrowRightIs.getItemMeta();
        int max = 1;
        if (siteDisplayableItems.size() > 36) {
            max = siteDisplayableItems.size()/36;
        }
        String temp = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_arrowRight").replaceAll("%SITE%", String.valueOf(site)).replaceAll("%MAX%", String.valueOf(max));
        arrowRightIm.setItemName(temp);
        arrowRightIs.setItemMeta(arrowRightIm);
        getInventory().setItem(53,arrowRightIs);

        ItemStack arrowLeftIs = SkullUtil.getCustomSkullFromURL("656439c3f65e9fe41f2d8be47bf493f143acadfd5dbea86fea6dbe19e435ea");
        ItemMeta arrowLeftIm = arrowLeftIs.getItemMeta();
        String temp1 = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_arrowLeft").replaceAll("%SITE%", String.valueOf(site)).replaceAll("%MAX%", String.valueOf(max));
        arrowLeftIm.setItemName(temp1);
        arrowLeftIs.setItemMeta(arrowLeftIm);
        getInventory().setItem(45,arrowLeftIs);

        ItemStack skullIs = SkullUtil.getPlayerSkull(getInventoryPlayer().getUniqueId());
        ItemMeta skullIm = skullIs.getItemMeta();
        skullIm.setItemName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"lobby_profileItem"));
        skullIm.addEnchant(Enchantment.POWER,1,true);
        skullIs.setItemMeta(skullIm);
        getInventory().setItem(49, skullIs);

        switch (friendsMenuState) {
            case FRIENDS:
                ItemStack addFriendIs = SkullUtil.getCustomSkullFromURL("5ff31431d64587ff6ef98c0675810681f8c13bf96f51d9cb07ed7852b2ffd1");
                ItemMeta addFriendIm = addFriendIs.getItemMeta();
                addFriendIm.setItemName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu"));
                addFriendIs.setItemMeta(addFriendIm);
                getInventory().setItem(4,addFriendIs);
                String[] lore = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequestsLore").split(";");
                lore[2] = lore[2].replaceAll("%COUNT%", String.valueOf(FriendsAPI.getFriendRequests(getInventoryPlayer().getUniqueId().toString()).size()));
                getInventory().setItem(48,new ItemUtil(Material.PAPER).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequests")).setLore(lore).build());
                getInventory().setItem(50, new ItemUtil(Material.COMPARATOR).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendSettings")).build());

                ArrayList<String> friends = FriendsAPI.getFriends(getInventoryPlayer().getUniqueId().toString());

                if (!friends.isEmpty()) {
                    for (String s:friends) {
                        if (FriendsAPI.isOnline(UUID.fromString(s))) {
                            ItemStack onlineFriendIs = SkullUtil.getPlayerSkull(UUID.fromString(s));
                            ItemMeta onlineFriendIm = onlineFriendIs.getItemMeta();
                            onlineFriendIm.setItemName("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(s)));
                            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOnlineStatusLore").replaceAll("%STATUS%", "§aOnline").replaceAll("%SERVER%", "§e" + FriendsAPI.getServerName(UUID.fromString(s))).replaceAll("%LANGUAGE%", "§2" + LanguageAPI.getMySQLLanguage(s)).split(";");
                            onlineFriendIm.setLore(Arrays.asList(lore1));
                            onlineFriendIm.addEnchant(Enchantment.POWER,1,true);
                            onlineFriendIs.setItemMeta(onlineFriendIm);
                            siteDisplayableItems.add(onlineFriendIs);
                        } else {
                            long yourmilliseconds = FriendsAPI.getLastLogin(UUID.fromString(s));
                            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                            Date resultdate = new Date(yourmilliseconds);
                            String lore1[] = LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendOfflineStatusLore").replaceAll("%STATUS%", "§cOffline").replaceAll("%LASTLOGIN%", "§e" + sdf.format(resultdate)).split(";");
                            ItemStack offlineFriendIs = new ItemUtil(Material.SKELETON_SKULL).setName("§7" + FriendsAPI.getNamefromUUID(UUID.fromString(s))).setLore(lore1).build();
                            siteDisplayableItems.add(offlineFriendIs);
                        }
                    }
                    for (int i=9;i<45;i++) {
                        if (siteDisplayableItems.size() > (((site-1)*36)+(i-9))) {
                            if (siteDisplayableItems.get(((site-1)*36)+(i-9)) != null) getInventory().setItem(i,siteDisplayableItems.get(((site-1)*36)+(i-9)));
                        }
                    }
                }
                break;
            case REQUESTS:
                getInventory().setItem(4, new ItemUtil(Material.RED_DYE).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendClose")).build());
                getInventory().setItem(0, new ItemUtil(Material.GREEN_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequestsAcceptAll")).build());
                getInventory().setItem(1, new ItemUtil(Material.RED_STAINED_GLASS).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequestsDeclineAll")).build());

                ArrayList<String> requests = FriendsAPI.getFriendRequests(getInventoryPlayer().getUniqueId().toString());
                if (!requests.isEmpty()) {
                    for (String s:requests) {
                        ItemStack offlineFriendIs = SkullUtil.getPlayerSkull(UUID.fromString(s));
                        ItemMeta offlineFriendIm = offlineFriendIs.getItemMeta();
                        offlineFriendIm.setItemName("§a§l" + FriendsAPI.getNamefromUUID(UUID.fromString(s)));
                        offlineFriendIs.setItemMeta(offlineFriendIm);
                        siteDisplayableItems.add(offlineFriendIs);
                    }
                    for (int i=9;i<45;i++) {
                        if (siteDisplayableItems.size() > (((site-1)*36)+(i-9))) {
                            getInventory().setItem(i,siteDisplayableItems.get(((site-1)*36)+(i-9)));
                        }
                    }
                }
                break;
            case SETTINGS:
                getInventory().setItem(4, new ItemUtil(Material.RED_DYE).setName(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendClose")).build());
                break;
        }
    }

    @Override
    public void OnOpenInventory(Player player) {
        initFriendsMenu();
    }

    @Override
    public void OnCloseInventory(Player player, InventoryCloseEvent event) {

    }

    @Override
    public void OnThisInventoryClicked(Player player, InventoryClickEvent event) {
        if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
            switch (friendsMenuState) {
                case FRIENDS:
                    if (event.getCurrentItem().getType().equals(Material.PAPER) && event.getCurrentItem().getItemMeta().getDisplayName().equals(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequests"))) {
                        friendsMenuState = FriendsMenuState.REQUESTS;
                        initFriendsMenu();
                        getInventoryPlayer().updateInventory();
                    } else if (event.getCurrentItem().getType().equals(Material.COMPARATOR) && event.getCurrentItem().getItemMeta().getDisplayName().equals(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendSettings"))) {
                        friendsMenuState = FriendsMenuState.SETTINGS;
                        initFriendsMenu();
                        getInventoryPlayer().updateInventory();
                    } else if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getRawSlot() == 4) {
                        Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new AnvilGUI.Builder()
                                        .onClick((slot, stateSnapshot) -> {
                                            if(slot != AnvilGUI.Slot.OUTPUT) {
                                                return Collections.emptyList();
                                            }
                                            String targetUUID = null;
                                            try {
                                                ResultSet rs = FriendsAPI.getInstance().getSqlUtil().executeQuery("SELECT * FROM `friendsAPI_playerData` WHERE `name` = '" + stateSnapshot.getText() + "';");
                                                if (rs.next()) {
                                                    targetUUID = rs.getString("uuid");
                                                }
                                                rs.close();
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                            if(targetUUID != null) {
                                                if (!FriendsAPI.getFriends(getInventoryPlayer().getUniqueId().toString()).contains(targetUUID)) {
                                                    if (!FriendsAPI.getFriendRequests(targetUUID).contains(getInventoryPlayer().getUniqueId().toString())) {
                                                        if (!FriendsAPI.getFriendRequests(getInventoryPlayer().getUniqueId().toString()).contains(targetUUID)) {
                                                            FriendsAPI.sendFriendRequest(getInventoryPlayer().getUniqueId().toString(),targetUUID);
                                                            getInventoryPlayer().sendMessage(FriendsAPI.prefix + LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu_playerAdd").replaceAll("%PLAYER%",stateSnapshot.getText()));
                                                        } else {
                                                            player.sendMessage(FriendsAPI.prefix + LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu_playerAlreadyRequested"));
                                                        }
                                                    } else {
                                                        player.sendMessage(FriendsAPI.prefix + LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu_playerAlreadyRequested"));
                                                    }
                                                } else {
                                                    getInventoryPlayer().sendMessage(FriendsAPI.prefix + LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu_playerAlreadyFriend"));
                                                }
                                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                                            } else {
                                                getInventoryPlayer().sendMessage(FriendsAPI.prefix + LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_addFriendMenu_playerNotFound"));
                                                return Arrays.asList(AnvilGUI.ResponseAction.close());
                                            }
                                        })
                                        .title(LanguageAPI.getTranslatedMessage(player,"friends_friendsMenu_addFriendMenu"))
                                        .text("Name")
                                        .plugin(FriendsAPI.getInstance())
                                        .open(getInventoryPlayer());
                            }
                        },1);
                    } else if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getRawSlot() >= 9 && event.getRawSlot() <= 44) {
                        String playerName = event.getCurrentItem().getItemMeta().getItemName().substring(4);
                        Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new FriendInventory("§a§l" + playerName,27,FriendsAPI.getUUIDfromName(playerName).toString()).openInventory(player);
                            }
                        },1);
                    } else if (event.getCurrentItem().getType().equals(Material.SKELETON_SKULL) && event.getRawSlot() >= 9 && event.getRawSlot() <= 44) {
                        String playerName = event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
                        Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new FriendInventory("§a§l" + playerName,27,FriendsAPI.getUUIDfromName(playerName).toString()).openInventory(player);
                            }
                        },1);
                    }
                    break;
                case REQUESTS:
                    if (event.getCurrentItem().getType().equals(Material.RED_DYE) && event.getCurrentItem().getItemMeta().getDisplayName().equals(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendClose"))) {
                        friendsMenuState = FriendsMenuState.FRIENDS;
                        initFriendsMenu();
                        getInventoryPlayer().updateInventory();
                    } else if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD) && event.getRawSlot() >= 9 && event.getRawSlot() <= 44) {
                        String playerName = event.getCurrentItem().getItemMeta().getItemName().substring(4);
                        Bukkit.getScheduler().runTaskLater(FriendsAPI.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                new FriendRequestInventory(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendRequests"),27,FriendsAPI.getUUIDfromName(playerName).toString(),getInventoryPlayer().getUniqueId().toString()).openInventory(getInventoryPlayer());
                            }
                        },1);
                    } else if (event.getCurrentItem().getType().equals(Material.GREEN_STAINED_GLASS)) {
                        ArrayList<String> friendRequests = FriendsAPI.getFriendRequests(getInventoryPlayer().getUniqueId().toString());
                        if (!friendRequests.isEmpty()) {
                            for (String s:friendRequests) {
                                FriendsAPI.removeFriendRequest(s,getInventoryPlayer().getUniqueId().toString());
                                FriendsAPI.pairFriends(getInventoryPlayer().getUniqueId().toString(),s);
                            }
                            initFriendsMenu();
                            getInventoryPlayer().updateInventory();
                        }
                    } else if (event.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS)) {
                        ArrayList<String> friendRequests = FriendsAPI.getFriendRequests(getInventoryPlayer().getUniqueId().toString());
                        if (!friendRequests.isEmpty()) {
                            for (String s:friendRequests) {
                                FriendsAPI.removeFriendRequest(s,getInventoryPlayer().getUniqueId().toString());
                            }
                            initFriendsMenu();
                            getInventoryPlayer().updateInventory();
                        }
                    }
                    break;
                case SETTINGS:
                    if (event.getCurrentItem().getType().equals(Material.RED_DYE) && event.getCurrentItem().getItemMeta().getDisplayName().equals(LanguageAPI.getTranslatedMessage(getInventoryPlayer(),"friends_friendsMenu_friendClose"))) {
                        friendsMenuState = FriendsMenuState.FRIENDS;
                        initFriendsMenu();
                        getInventoryPlayer().updateInventory();
                    }
                    break;
            }
        }
    }

    private enum FriendsMenuState {
        FRIENDS,
        REQUESTS,
        SETTINGS
    }
}
