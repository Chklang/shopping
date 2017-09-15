package fr.chklang.minecraft.shoping.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.chklang.minecraft.shoping.Position;

public class ShopsHelper {
	
	public static final Map<UUID, NewShop> newShops = new HashMap<>();
	
	public static class NewShop {
		public final Long idShop;
		public Position newShopPosition1 = null;
		public Position newShopPosition2 = null;
		public Position newShopPosition3 = null;
		
		public NewShop(Long pIdShop) {
			super();
			this.idShop = pIdShop;
		}
	}

}
