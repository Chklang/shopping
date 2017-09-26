package fr.chklang.minecraft.shoping.json.events;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent.ShopUpdateEventContent;
import fr.chklang.minecraft.shoping.model.Shop;

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
		public final long space;
		public ShopUpdateEventContent(long pIdShop, String pName, Long pIdOwner, long pXMin, long pXMax, long pYMin, long pYMax, long pZMin, long pZMax, long pSpace) {
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
			this.space = pSpace;
		}
		public ShopUpdateEventContent(Shop pShop) {
			this(pShop.getId(), pShop.getName(), pShop.getOwner()==null?null:pShop.getOwner().getId(), pShop.getX_min(), pShop.getX_max(), pShop.getY_min(), pShop.getY_max(), pShop.getZ_min(), pShop.getZ_max(), pShop.getSpace());
		}
	}

	public ShopUpdateEvent(long pIdShop, String pName, Long pIdOwner, long pXMin, long pXMax, long pYMin, long pYMax, long pZMin, long pZMax, long pSpace) {
		super(new ShopUpdateEventContent(pIdShop, pName, pIdOwner, pXMin, pXMax, pYMin, pYMax, pZMin, pZMax, pSpace));
	}
	public ShopUpdateEvent(Shop pShop) {
		super(new ShopUpdateEventContent(pShop));
	}
}
