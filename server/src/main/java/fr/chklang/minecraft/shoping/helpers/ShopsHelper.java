package fr.chklang.minecraft.shoping.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.json.events.ShopItemUpdateEvent;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;

public class ShopsHelper {
	
	public static final Map<UUID, NewShop> newShops = new HashMap<>();
	
	public static class NewShop {
		public final Long idShop;
		public List<Position> positions = new ArrayList<>();
		
		public NewShop(Long pIdShop) {
			super();
			this.idShop = pIdShop;
		}
	}
	
	public static void broadcastShopItemUpdateEvent(ShopItemUpdateEvent pEvent) {
		long lIdShop = pEvent.content.idShop;
		LoginHelper.connectedPlayers.values().forEach((pPlayer) -> {
			pPlayer.connexions.forEach((pConnection) -> {
				if (pConnection.getIdShopSubscripted() != null && pConnection.getIdShopSubscripted().longValue() == lIdShop) {
					pConnection.send(pEvent);
				}
			});
		});
	}
	
	public static void broadcastShopUpdateEvent(ShopUpdateEvent pEvent) {
		long lIdShop = pEvent.content.idShop;
		LoginHelper.connectedPlayers.values().forEach((pPlayer) -> {
			pPlayer.connexions.forEach((pConnection) -> {
				if (pConnection.getIdShopSubscripted() != null && pConnection.getIdShopSubscripted().longValue() == lIdShop) {
					pConnection.send(pEvent);
				}
			});
		});
	}

}
