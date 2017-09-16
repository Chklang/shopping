package fr.chklang.minecraft.shoping.commands;

import java.util.ArrayList;
import java.util.List;
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
			List<Integer> lPointsDefined = new ArrayList<>();
			if (lNewShop.newShopPosition1 != null) {
				lPointsDefined.add(1);
			}
			if (lNewShop.newShopPosition2 != null) {
				lPointsDefined.add(2);
			}
			if (lNewShop.newShopPosition3 != null) {
				lPointsDefined.add(3);
			}
			String lMessage = null;
			if (lNewShop.idShop != null) {
				lMessage = "The shop #" + lNewShop.idShop + " is in update state";
			} else {
				lMessage = "A shop is in creation state";
			}
			if (lPointsDefined.size() == 0) {
				lMessage += " but no points has been defined.";
			} else if (lPointsDefined.size() == 1) {
				lMessage += " and only the corner number " + lPointsDefined.get(0) + " is defined.";
			} else if (lPointsDefined.size() == 2) {
				lMessage += " and corners number " + lPointsDefined.get(0) + " and " + lPointsDefined.get(1) + " are defined.";
			} else {
				lMessage += " and all corners are defined.";
			}
			pSender.sendMessage(lMessage);
			return true;
		}
		return false;
	}

}
