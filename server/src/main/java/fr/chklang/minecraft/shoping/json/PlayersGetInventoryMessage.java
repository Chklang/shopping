package fr.chklang.minecraft.shoping.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;

import fr.chklang.minecraft.shoping.events.InventoryEvent;
import fr.chklang.minecraft.shoping.events.InventoryEvent.ItemId;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper.Element;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class PlayersGetInventoryMessage extends AbstractMessage<PlayersGetInventoryContent>{

	@Override
	public void execute(IConnexion pConnexion) {
		fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.get(this.content.idPlayer);
		if (lPlayerDB == null) {
			System.err.println("Player don't exists");
			pConnexion.send(new Response(this, false));
			return;
		}
		InventoryEvent lInventoryEvent = Bukkit.getServicesManager().getRegistration(InventoryEvent.class).getProvider();
		Map<ItemId, Long> lItems = lInventoryEvent.getItems(UUID.fromString(lPlayerDB.getUuid()));
		
		Response lResponse = new Response(this, true);
		lItems.entrySet().forEach((pItem) -> {
			Element lElement = BlocksHelper.getElement(pItem.getKey().id, pItem.getKey().subId);
			lResponse.content.items.add(new ItemResponse(pItem.getKey().id, pItem.getKey().subId, pItem.getValue().longValue(), lElement.name, lElement.nameDetails));
		});
		pConnexion.send(lResponse);
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pIsOk) {
			super(pOrigin);
			this.content = new ResponseContent(pIsOk);
		}
	}

	public static class ResponseContent {
		public final boolean isOk;
		public final List<ItemResponse> items = new ArrayList<>();
		public ResponseContent(boolean pIsOk) {
			super();
			this.isOk = pIsOk;
		}
	}

	public static class ItemResponse {
		public final int idItem;
		public final int subIdItem;
		public final long quantity;
		public final String name;
		public final String nameDetails;
		public ItemResponse(int pIdItem, int pSubIdItem, long pQuantity, String pName, String pNameDetails) {
			super();
			this.idItem = pIdItem;
			this.subIdItem = pSubIdItem;
			this.quantity = pQuantity;
			this.name = pName;
			this.nameDetails = pNameDetails;
		}
	}
}
