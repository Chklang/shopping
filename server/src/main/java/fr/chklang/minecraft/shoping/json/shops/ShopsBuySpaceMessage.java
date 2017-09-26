package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.Config;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.servlets.IConnexion;
import net.milkbowl.vault.economy.Economy;

public class ShopsBuySpaceMessage extends AbstractMessage<ShopsBuySpaceContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		PlayerConnected lPlayer = pConnexion.getPlayer();
		if (lPlayer == null) {
			System.err.println("Player not connected");
			pConnexion.send(new Response(this, false));
			return;
		}
		Shop lShop = Shop.DAO.get(this.content.idShop);
		if (lShop == null) {
			System.err.println("Shop not found");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (lShop.getOwner() == null) {
			System.err.println("General shops hasn't some space");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (lShop.getOwner() != null && 
				lShop.getOwner().getId() != lPlayer.idUser && 
				!lPlayer.player.isOp()) {
			System.err.println("Player isn't the owner");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (this.content.quantity < 0) {
			System.err.println("You can't buy negative space!");
			pConnexion.send(new Response(this, false));
			return;
		}
		double lPrice = this.content.quantity * Config.getInstance().getSpacePrice();
		Economy lEconomy = this.getEconomy();
		double lMoney = lEconomy.getBalance(lPlayer.player);
		if (lMoney < lPrice) {
			System.err.println("You haven't enough money");
			pConnexion.send(new Response(this, false));
			return;
		}
		lShop.setSpace(lShop.getSpace() + this.content.quantity);
		lEconomy.withdrawPlayer(lPlayer.player, lPrice);
		Long lIdOwner = null;
		if (lShop.owner != null) {
			lIdOwner = lShop.owner.getId();
		}
		ShopUpdateEvent lShopUpdateEvent = new ShopUpdateEvent(lShop);
		ShopsHelper.broadcastShopUpdateEvent(lShopUpdateEvent);
		pConnexion.send(new Response(this, true));
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pIsOk) {
			super(pOrigin);
			this.content = new ResponseContent(pIsOk);
		}
	}

	public static class ResponseContent {
		public final boolean isOk;

		public ResponseContent(boolean pIsOk) {
			super();
			this.isOk = pIsOk;
		}
	}

}
