package fr.chklang.minecraft.shoping.json.events;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.events.ShopItemUpdateEvent.ShopItemUpdateEventContent;
import fr.chklang.minecraft.shoping.model.ShopItem;

public class ShopItemUpdateEvent extends AbstractEvent<ShopItemUpdateEventContent>{
	
	public static class ShopItemUpdateEventContent extends AbstractContent {
		public final long idShop;
		public final int idItem;
		public final int subIdItem;
		public Double margin;
		public Double price;
		public long buy;
		public long sell;
		public long quantity;
		public ShopItemUpdateEventContent(long pIdShop, int pIdItem, int pSubIdItem, Double pMargin, Double pPrice, long pBuy, long pSell, long pQuantity) {
			super();
			this.idShop = pIdShop;
			this.idItem = pIdItem;
			this.subIdItem = pSubIdItem;
			this.margin = pMargin;
			this.price = pPrice;
			this.buy = pBuy;
			this.sell = pSell;
			this.quantity = pQuantity;
		}
		public ShopItemUpdateEventContent(ShopItem pShopItem) {
			this(pShopItem.getShop().getId(), pShopItem.getIdItem(), pShopItem.getSubIdItem(), pShopItem.getMargin(), pShopItem.getPrice(), pShopItem.getBuy(), pShopItem.getSell(), pShopItem.getQuantity());
		}
	}

	public ShopItemUpdateEvent(long pIdShop, int pIdItem, int pSubIdItem, Double pMargin, Double pPrice, long pBuy, long pSell, long pQuantity) {
		super(new ShopItemUpdateEventContent(pIdShop, pIdItem, pSubIdItem, pMargin, pPrice, pBuy, pSell, pQuantity));
	}
	
	public ShopItemUpdateEvent(ShopItem pShopItem) {
		super(new ShopItemUpdateEventContent(pShopItem));
	}
}
