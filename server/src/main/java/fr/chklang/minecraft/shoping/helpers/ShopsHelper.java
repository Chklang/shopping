package fr.chklang.minecraft.shoping.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.chklang.minecraft.shoping.Position;

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

}
