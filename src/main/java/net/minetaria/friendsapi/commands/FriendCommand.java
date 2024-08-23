package net.minetaria.friendsapi.commands;

import de.gaunercools.languageapibukkit.mysql.LanguageAPI;
import net.minetaria.friendsapi.inventories.FriendsInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            new FriendsInventory(LanguageAPI.getTranslatedMessage(player,"friends_friendsMenu"),54).openInventory(player);
        }
        return false;
    }
}
