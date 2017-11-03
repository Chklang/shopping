package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.NewShop;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;

public class SetCornerCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO
					.getByUuid(lUuid.toString());
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
				int lIndexPosition = lArgument1.intValue() - 1;
				if (lNewShop.setPosition(lIndexPosition, new Position(lPlayer.getLocation().getX(),
						lPlayer.getLocation().getY(), lPlayer.getLocation().getZ()))) {
					pSender.sendMessage("Block " + (lIndexPosition + 1) + " is updated.");
					return true;
				}
				return false;
			}
			lNewShop.addPosition(new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(),
					lPlayer.getLocation().getZ()));
			pSender.sendMessage("Block " + lNewShop.getNbPositions()
					+ " is added. If it's done type 'shopping.validate' to create the shop.");
			return true;
		}
		return false;
	}

}
