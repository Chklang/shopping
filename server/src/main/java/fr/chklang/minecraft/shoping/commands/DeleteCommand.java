package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.model.Shop;

public class DeleteCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			if (pArgs.length < 1) {
				pSender.sendMessage("Usage : /shopping.delete [idShop to delete]");
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
				pSender.sendMessage("Only an admin can delete general shops");
				return false;
			} else if (!lPlayer.isOp() && lShop.owner.getId() != lPlayerModel.getId()) {
				pSender.sendMessage("It's not your shop!");
				return false;
			}
			lShop.delete();
			pSender.sendMessage("Shop #"+lIdShop+" has been deleted!");
			return true;
		}
		return false;
	}

}
