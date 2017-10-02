package fr.chklang.minecraft.shoping.events;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.helpers.BlocksHelper;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper.Element;
import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.helpers.MessagesHelper;
import fr.chklang.minecraft.shoping.json.events.PlayerInventoryEvent;

public class InventoryEvent {
	
	public static class ItemId implements Comparable<ItemId> {
		public final int id;
		public final short subId;

		public ItemId(int pId, short pSubId) {
			super();
			this.id = pId;
			this.subId = pSubId;
		}
		
		@Override
		public int compareTo(ItemId pItemId) {
			if (pItemId.id != this.id) {
				return this.id - pItemId.id;
			}
			return this.subId - pItemId.subId;
		}

		@Override
		public String toString() {
			return "ItemId [id=" + this.id + ", subId=" + this.subId + "]";
		}
	}

	public Map<UUID, Map<ItemId, Long>> inventories;

	private Plugin plugin;
	private Integer schedulerTaskId;

	public InventoryEvent(Plugin pPlugin) {
		this.plugin = pPlugin;
	}

	public void start() {
		this.inventories = new HashMap<>();
		this.setupOnEconomyChange();
	}

	private void setupOnEconomyChange() {
		this.schedulerTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
			LoginHelper.connectedPlayers.forEach((UUID pUUID, PlayerConnected pPlayerConnected) -> {
				if (pPlayerConnected.connexions.isEmpty()) {
					//No web connection
					return;
				}
				PlayerInventory lInventory = pPlayerConnected.player.getInventory();
				TreeMap<ItemId, Long> lNewElements = new TreeMap<>();
				lInventory.forEach((ItemStack pItemStack) -> {
					if (pItemStack == null) {
						//Ignore
						return;
					}
					ItemId lItemId = new ItemId(pItemStack.getTypeId(), pItemStack.getDurability());
					Long lOriginalQuantity = lNewElements.get(lItemId);
					long lNewQuantity = 0;
					if (lOriginalQuantity != null) {
						lNewQuantity = lOriginalQuantity.longValue();
					}
					lNewQuantity += pItemStack.getAmount();
					lNewElements.put(lItemId, Long.valueOf(lNewQuantity));
				});
				Map<ItemId, Long> lElementsKnown = this.inventories.get(pUUID);
				final Map<ItemId, Long> lElementsDiff = new TreeMap<>();
				if (lElementsKnown == null) {
					lElementsDiff.putAll(lNewElements);
					lElementsKnown = new TreeMap<>();
					this.inventories.put(pUUID, lElementsKnown);
				} else {
					lElementsKnown.forEach((ItemId pItemId, Long pOriginalQuantity) -> {
						Long lNewQuantity = lNewElements.get(pItemId);
						if (lNewQuantity == null) {
							lElementsDiff.put(pItemId, Long.valueOf(0));
						} else if(lNewQuantity.longValue() != pOriginalQuantity.longValue()) {
							lElementsDiff.put(pItemId, lNewQuantity);
						}
						lNewElements.remove(pItemId);
					});
					//Add all elements not found in previous inventory
					lElementsDiff.putAll(lNewElements);
				}
				if (lElementsDiff.isEmpty()) {
					//No update to do
					return;
				}
				
				final Map<ItemId, Long> lElementsKnownFinal = lElementsKnown;
				
				final PlayerInventoryEvent lPlayerInventoryEvent= new PlayerInventoryEvent();
				lElementsDiff.forEach((ItemId pItemId, Long pQuantity) -> {
					Element lElement = BlocksHelper.getElement(pItemId.id, pItemId.subId);
					if (lElement == null) {
						//Ignore element
						return;
					}
					PlayerInventoryEvent.PlayerInventoryItemContent lItem = new PlayerInventoryEvent.PlayerInventoryItemContent(pItemId.id, pItemId.subId, pQuantity, lElement.name, lElement.nameDetails);
					lPlayerInventoryEvent.content.items.add(lItem);
					if (pQuantity.longValue() == 0) {
						lElementsKnownFinal.remove(pItemId);
					} else {
						lElementsKnownFinal.put(pItemId, pQuantity);
					}
				});
				MessagesHelper.broadcastEventToAPlayer(lPlayerInventoryEvent, pPlayerConnected);
			});
		}, 20, 20);
	}
	
	public Map<ItemId, Long> getItems(UUID pPlayerId) {
		return this.inventories.get(pPlayerId);
	}

	public void stop() {
		if (this.schedulerTaskId != null) {
			Bukkit.getServer().getScheduler().cancelTask(this.schedulerTaskId.intValue());
		}
	}
}
