package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.json.AbstractContent;

public class ShopsBuyOrSellContent extends AbstractContent {
	
	public static enum ActionType {
		BUY((short)1), SELL((short)2);
		
		public final short value;
		ActionType(final short pValue) {
			this.value = pValue;
		}
	}

	public long idShop;
	public int idItem;
	public short subIdItem;
	public short actionType;
	public int quantity;
}
