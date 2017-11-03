package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Config;
import fr.chklang.minecraft.shoping.helpers.MessagesHelper;
import fr.chklang.minecraft.shoping.helpers.NewShop;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;
import fr.chklang.minecraft.shoping.model.Shop;

public class ValidateCommand extends AbstractCommand {

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		if (pSender instanceof Player) {
			Player lPlayer = (Player) pSender;
			UUID lUuid = lPlayer.getUniqueId();
			NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
			if (lNewShop == null) {
				pSender.sendMessage(
						"Before to call 'shopping.validate' you must call 'shopping.create' then 'shopping.setcorner' three times to define the cube of shop. The first corner isn't defined.");
				return false;
			}
			if (lNewShop.getNbPositions() < 2) {
				pSender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' at least two times to define the cube of shop.");
				return false;
			}
			System.out.println("Création du magasin, player uuid = " + lUuid.toString());
			fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
			System.out.println("Player found : " + lPlayerModel);
			if (lPlayerModel == null) {
				lPlayerModel = new fr.chklang.minecraft.shoping.model.Player(lUuid.toString());
				lPlayerModel.save();
				System.out.println("Player créé : " + lPlayerModel);
			}
			Shop lShop = null;
			if (lNewShop.idShop == null) {
				lShop = new Shop();
				lShop.setName("");
				lShop.setSpace(Config.getInstance().getBaseSpace());
			} else {
				lShop = Shop.DAO.get(lNewShop.idShop);
				if (lShop == null) {
					pSender.sendMessage("The shop #" + lNewShop.idShop + " doesn't exist!");
					return false;
				}
			}
			long lXMin = lNewShop.getxMin();
			long lXMax = lNewShop.getxMax();
			long lYMin = lNewShop.getyMin();
			long lYMax = lNewShop.getyMax();
			long lZMin = lNewShop.getzMin();
			long lZMax = lNewShop.getzMax();
			
			lShop.setX_min(lXMin);
			lShop.setX_max(lXMax);
			lShop.setY_min(lYMin);
			lShop.setY_max(lYMax);
			lShop.setZ_min(lZMin);
			lShop.setZ_max(lZMax);
			lShop.setOwner(lPlayerModel);
			lShop.save();
			lNewShop.hide();
			ShopsHelper.newShops.remove(lUuid);
			pSender.sendMessage("Shop id #" + lShop.getId() + " was created. You can manage it into the web interface.");

			MessagesHelper.broadcastEventToAllPlayers(new ShopUpdateEvent(lShop.getId(), lShop.getName(), lPlayerModel.getId(), lShop.getX_min(), lShop.getX_max(), lShop.getY_min(), lShop.getY_max(), lShop.getZ_min(), lShop.getZ_max(), lShop.getSpace()), false);
			return true;
		}
		return false;
	}

}
