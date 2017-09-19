package fr.chklang.minecraft.shoping.json.events;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent.ShopUpdateEventContent;

public class ShopUpdateEvent extends AbstractEvent<ShopUpdateEventContent>{
	
	public static class ShopUpdateEventContent extends AbstractContent {
		public final long idShop;
		public final Long idOwner;
		public final String name;
		public final long xMin;
		public final long xMax;
		public final long yMin;
		public final long yMax;
		public final long zMin;
		public final long zMax;
		public ShopUpdateEventContent(long pIdShop, String pName, Long pIdOwner, long pXMin, long pXMax, long pYMin, long pYMax, long pZMin, long pZMax) {
			super();
			this.idShop = pIdShop;
			this.idOwner = pIdOwner;
			this.name = pName;
			this.xMin = pXMin;
			this.xMax = pXMax;
			this.yMin = pYMin;
			this.yMax = pYMax;
			this.zMin = pZMin;
			this.zMax = pZMax;
		}
	}

	public ShopUpdateEvent(long pIdShop, String pName, Long pIdOwner, long pXMin, long pXMax, long pYMin, long pYMax, long pZMin, long pZMax) {
		super(new ShopUpdateEventContent(pIdShop, pName, pIdOwner, pXMin, pXMax, pYMin, pYMax, pZMin, pZMax));
	}
}
