package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;

public class StatusCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
			if (lNewShop == null) {
				pSender.sendMessage("There is no shop in create or update state.");
				return true;
			}
			String lMessage = null;
			if (lNewShop.idShop != null) {
				lMessage = "The shop #" + lNewShop.idShop + " is in update state";
			} else {
				lMessage = "A shop is in creation state";
			}
			if (lNewShop.positions.size() == 0) {
				lMessage += " but no points has been defined.";
			} else {
				lMessage += " and " + lNewShop.positions.size() + " has been defined.";
			}
			pSender.sendMessage(lMessage);
			return true;
		}
		return false;
	}

}
