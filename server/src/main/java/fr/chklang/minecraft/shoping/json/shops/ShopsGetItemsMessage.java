package fr.chklang.minecraft.shoping.json.shops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.chklang.minecraft.shoping.helpers.BlocksHelper;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.model.ShopItem;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsGetItemsMessage extends AbstractMessage<ShopsGetItemsContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		Shop lShop = Shop.DAO.get(this.content.idShop);
		if (lShop == null) {
			return;
		}
		List<ShopItem> lItems = ShopItem.DAO.getByShop(lShop);
		Map<String, ResponseContentElement> lItemsToSend = new HashMap<>();
		lItems.forEach((pShopItem) -> {
			String lId = pShopItem.getIdItem() + "_" + pShopItem.getSubIdItem();
			lItemsToSend.put(lId, new ResponseContentElement(pShopItem.getIdItem(), pShopItem.getSubIdItem(), pShopItem.getSell(), pShopItem.getBuy(), pShopItem.getPrice(), pShopItem.getMargin(),
					pShopItem.getQuantity()));
		});

		BlocksHelper.getElements().forEach((pElement) -> {
			String lBaseId = pElement.id + "_";
			pElement.subElements.forEach((pSubElement) -> {
				String lId = lBaseId + pSubElement.id;
				ResponseContentElement lResponseContentElement = lItemsToSend.get(lId);
				if (lResponseContentElement == null) {
					lItemsToSend.put(lId, new ResponseContentElement(pElement.id, pSubElement.id, 0, 0, pSubElement.price, null, 0));
				}
			});
			String lId = lBaseId + "0";
			ResponseContentElement lResponseContentElement = lItemsToSend.get(lId);
			if (lResponseContentElement == null) {
				lItemsToSend.put(lId, new ResponseContentElement(pElement.id, 0, 0, 0, pElement.price, null, 0));
			}
		});
		Response lResponse = new Response(this);
		lItemsToSend.values().forEach((pShopItem) -> {
			lResponse.content.items.add(pShopItem);
		});
		pConnexion.send(lResponse);
		return;
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin) {
			super(pOrigin);
			this.content = new ResponseContent();
		}
	}

	public static class ResponseContent {
		public final List<ResponseContentElement> items = new ArrayList<>();
	}

	public static class ResponseContentElement {
		public final int idItem;
		public final int subIdItem;
		public final long sell;
		public final long buy;
		public final double price;
		public final Double margin;
		public final long quantity;

		public ResponseContentElement(int pIdItem, int pSubIdItem, long pSell, long pBuy, double pPrice, Double pMargin, long pQuantity) {
			super();
			this.idItem = pIdItem;
			this.subIdItem = pSubIdItem;
			this.sell = pSell;
			this.buy = pBuy;
			this.price = pPrice;
			this.margin = pMargin;
			this.quantity = pQuantity;
		}
	}

}
