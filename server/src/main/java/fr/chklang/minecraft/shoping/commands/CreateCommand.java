package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;

public class CreateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			UUID lUuid = ((Player) pSender).getUniqueId();
			ShopsHelper.newShops.put(lUuid, new NewShop(null));
			pSender.sendMessage("Creation shop in progess. Please give base blocks.");
			return true;
		}
		return false;
	}

}
