package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;

public class CreateCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			NewShop lNewShop = new NewShop(null);
			lNewShop.positions.add(new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ()));
			ShopsHelper.newShops.put(lUuid, lNewShop);
			pSender.sendMessage("Creation shop in progess. Please give base blocks.");
			return true;
		}
		return false;
	}

}
