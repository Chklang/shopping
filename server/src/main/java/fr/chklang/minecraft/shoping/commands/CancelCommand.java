package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.helpers.NewShop;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;

public class CancelCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			UUID lUuid = ((Player) pSender).getUniqueId();
			NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
			if (lNewShop != null) {
				lNewShop.hide();
				ShopsHelper.newShops.remove(lUuid);
				return true;
			} else {
				pSender.sendMessage("No shop to cancel");
				return true;
			}
		}
		return false;
	}

}
