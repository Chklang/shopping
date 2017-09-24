package fr.chklang.minecraft.shoping.commands;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.helpers.MessagesHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;
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
			if (lNewShop.newShopPosition1 == null) {
				pSender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The first corner isn't defined.");
				return false;
			}
			if (lNewShop.newShopPosition2 == null) {
				pSender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The second corner isn't defined.");
				return false;
			}
			if (lNewShop.newShopPosition3 == null) {
				pSender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The third corner isn't defined.");
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
			} else {
				lShop = Shop.DAO.get(lNewShop.idShop);
				if (lShop == null) {
					pSender.sendMessage("The shop #" + lNewShop.idShop + " doesn't exist!");
					return false;
				}
			}
			long lXMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.x)).longValue();
			long lXMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.x)).longValue();
			long lYMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.y)).longValue();
			long lYMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.y)).longValue();
			long lZMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.z)).longValue();
			long lZMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.z)).longValue();

			long lCurrentValue = 0;
			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.x)).longValue();
			if (lXMin > lCurrentValue) {
				lXMin = lCurrentValue;
			}
			if (lXMax < lCurrentValue) {
				lXMax = lCurrentValue;
			}
			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.y)).longValue();
			if (lYMin > lCurrentValue) {
				lYMin = lCurrentValue;
			}
			if (lYMax < lCurrentValue) {
				lYMax = lCurrentValue;
			}
			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.z)).longValue();
			if (lZMin > lCurrentValue) {
				lZMin = lCurrentValue;
			}
			if (lZMax < lCurrentValue) {
				lZMax = lCurrentValue;
			}

			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.x)).longValue();
			if (lXMin > lCurrentValue) {
				lXMin = lCurrentValue;
			}
			if (lXMax < lCurrentValue) {
				lXMax = lCurrentValue;
			}
			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.y)).longValue();
			if (lYMin > lCurrentValue) {
				lYMin = lCurrentValue;
			}
			if (lYMax < lCurrentValue) {
				lYMax = lCurrentValue;
			}
			lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.z)).longValue();
			if (lZMin > lCurrentValue) {
				lZMin = lCurrentValue;
			}
			if (lZMax < lCurrentValue) {
				lZMax = lCurrentValue;
			}

			lShop.setX_min(lXMin);
			lShop.setX_max(lXMax);
			lShop.setY_min(lYMin);
			lShop.setY_max(lYMax);
			lShop.setZ_min(lZMin);
			lShop.setZ_max(lZMax);
			lShop.setOwner(lPlayerModel);
			System.out.println("Magasin à créer : " + lShop);
			lShop.save();
			System.out.println("Magasin créé : " + lShop);
			ShopsHelper.newShops.remove(lUuid);
			pSender.sendMessage("Shop id #" + lShop.getId() + " was created. You can manage it into the web interface.");

			MessagesHelper.broadcastEventToAllPlayers(new ShopUpdateEvent(lShop.getId(), lShop.getName(), lPlayerModel.getId(), lShop.getX_min(), lShop.getX_max(), lShop.getY_min(), lShop.getY_max(), lShop.getZ_min(), lShop.getZ_max()), false);
			return true;
		}
		return false;
	}

}
