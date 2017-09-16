package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;

public class SetCornerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
			if (lPlayerModel == null) {
				lPlayerModel = new fr.chklang.minecraft.shoping.model.Player(lUuid.toString());
				lPlayerModel.save();
			}
			Integer lArgument1 = null;
			switch (pArgs.length) {
			case 1:
				try {
					lArgument1 = Integer.parseInt(pArgs[0]);
				} catch (NumberFormatException e) {
					pSender.sendMessage("The first argument (optional) must be a number!");
					return false;
				}
				if (lArgument1.intValue() < 1 || lArgument1.intValue() > 3) {
					pSender.sendMessage("The first argument (optional) must be a number between 1 and 3.");
					return false;
				}
			}
			NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
			if (lNewShop == null) {
				pSender.sendMessage("No shop in creation was found. Please run 'shopping.create' first");
				return false;
			}
			if (lArgument1 != null) {
				switch (lArgument1.intValue()) {
				case 1:
					lNewShop.newShopPosition1 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
					pSender.sendMessage("First block is updated.");
					return true;
				case 2:
					lNewShop.newShopPosition2 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
					pSender.sendMessage("Second block is updated.");
					return true;
				case 3:
					lNewShop.newShopPosition3 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
					pSender.sendMessage("Third block is updated.");
					return true;
				}
				return false;
			}
			if (lNewShop.newShopPosition1 != null) {
				if (lNewShop.newShopPosition2 != null) {
					if (lNewShop.newShopPosition3 != null) {
						pSender.sendMessage("All points (3) are defined. To validate it send 'shopping.validate'.");
						return false;
					} else {
						lNewShop.newShopPosition3 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
						pSender.sendMessage("Third block has been saved. Type 'shopping.validate' to create the shop.");
						return true;
					}
				} else {
					lNewShop.newShopPosition2 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
					pSender.sendMessage("Second block has been saved.");
					return true;
				}
			} else {
				lNewShop.newShopPosition1 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
				pSender.sendMessage("First block has been saved.");
				return true;
			}
		}
		return false;
	}

}
