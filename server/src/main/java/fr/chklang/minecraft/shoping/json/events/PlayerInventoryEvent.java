package fr.chklang.minecraft.shoping.json.events;

import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.events.PlayerInventoryEvent.PlayerInventoryContent;

public class PlayerInventoryEvent extends AbstractEvent<PlayerInventoryContent> {

	public PlayerInventoryEvent() {
		super(new PlayerInventoryContent());
	}


	public static class PlayerInventoryContent extends AbstractContent {
		public final List<PlayerInventoryItemContent> items = new ArrayList<>();
	}
	
	public static class PlayerInventoryItemContent {
		public final int idItem;
		public final short subIdItem;
		public final long quantity;
		public final String name;
		public final String nameDetails;
		public PlayerInventoryItemContent(int pIdItem, short pSubIdItem, long pQuantity, String pName,
				String pNameDetails) {
			super();
			this.idItem = pIdItem;
			this.subIdItem = pSubIdItem;
			this.quantity = pQuantity;
			this.name = pName;
			this.nameDetails = pNameDetails;
		}
	}
}
