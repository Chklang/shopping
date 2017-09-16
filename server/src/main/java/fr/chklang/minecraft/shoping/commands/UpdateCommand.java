package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;
import fr.chklang.minecraft.shoping.model.Shop;

public class UpdateCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			if (pArgs.length < 1) {
				pSender.sendMessage("Usage : /shopping.update [idShop to update]");
				return false;
			}
			fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
			long lIdShop = 0;
			try {
				lIdShop = Long.parseLong(pArgs[0]);
			} catch (NumberFormatException e) {
				pSender.sendMessage("The first argument (optional) must be a number!");
				return false;
			}
			Shop lShop = Shop.DAO.get(lIdShop);
			if (lShop == null) {
				pSender.sendMessage("Shop " + lIdShop + " doesn't exists!");
				return false;
			}
			if (!lPlayer.isOp() && lShop.owner == null) {
				pSender.sendMessage("Only an admin can modify general shops");
				return false;
			} else if (!lPlayer.isOp() && lShop.owner.getId() != lPlayerModel.getId()) {
				pSender.sendMessage("It's not your shop!");
				return false;
			}
			ShopsHelper.newShops.put(lUuid, new NewShop(lIdShop));
			pSender.sendMessage("Update shop in progess. Please give new base blocks.");
			return true;
		}
		return false;
	}

}
