package fr.chklang.minecraft.shoping.json;

import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.model.ShopItem;
import fr.chklang.minecraft.shoping.model.ShopItemPk;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsSetItemMessage extends AbstractMessage<ShopsSetItemContent> {

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
		if (lShop.getOwner() == null && !lPlayer.player.isOp()) {
			System.err.println("Player isn't operator");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (lShop.getOwner().getId() != lPlayer.idUser && !lPlayer.player.isOp()) {
			System.err.println("Player isn't the owner");
			pConnexion.send(new Response(this, false));
			return;
		}
		ShopItemPk lShopItemPk = new ShopItemPk(this.content.idShop, this.content.idItem, this.content.subIdItem);
		ShopItem lShopItem = ShopItem.DAO.get(lShopItemPk);
		boolean lMustDelete = this.content.buy == 0 && this.content.sell == 0 && this.content.margin == null && this.content.price == null;
		if (lShopItem != null && lMustDelete) {
			lShopItem.delete();
		} else if (!lMustDelete){
			if (lShopItem == null) {
				lShopItem = new ShopItem();
				lShopItem.setShop(lShop);
				lShopItem.setIdItem(this.content.idItem);
				lShopItem.setSubIdItem(this.content.subIdItem);
			}
			lShopItem.setBuy(this.content.buy);
			lShopItem.setSell(this.content.sell);
			lShopItem.setMargin(this.content.margin);
			lShopItem.setPrice(this.content.price);
			lShopItem.save();
		}
		pConnexion.send(new Response(this, true));
		return;
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
