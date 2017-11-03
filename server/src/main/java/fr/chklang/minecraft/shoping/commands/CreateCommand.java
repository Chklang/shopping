package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.helpers.NewShop;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;

public class CreateCommand extends AbstractCommand {
	
	private final Plugin plugin;

	public CreateCommand(Plugin pPlugin) {
		this.plugin = pPlugin;
	}

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			World lWorld = lPlayer.getWorld();
			NewShop lNewShop = new NewShop(null, lWorld, plugin);
			lNewShop.show();
			ShopsHelper.newShops.put(lUuid, lNewShop);
			pSender.sendMessage("Creation shop in progess. Please give base blocks.");
			return true;
		}
		return false;
	}

}
