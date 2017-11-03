package fr.chklang.minecraft.shoping.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.model.Shop;

public class ShowCommand extends AbstractCommand {
	
	private final Plugin plugin;

	public ShowCommand(Plugin pPlugin) {
		this.plugin = pPlugin;
	}

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			if (pArgs.length < 1) {
				final World lWorld = lPlayer.getWorld();
				Shop.DAO.getAll().forEach((Shop pShop) -> {
					ShopsHelper.showShop(plugin, pShop, lWorld);
				});
				return true;
			}
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
			World lWorld = lPlayer.getWorld();
			ShopsHelper.showShop(plugin, lShop, lWorld);
			return true;
		}
		return false;
	}

}
