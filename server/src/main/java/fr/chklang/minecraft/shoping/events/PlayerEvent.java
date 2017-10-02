package fr.chklang.minecraft.shoping.events;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.helpers.MessagesHelper;
import fr.chklang.minecraft.shoping.json.PositionMessage;
import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage;
import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage.JoinType;
import net.milkbowl.vault.economy.Economy;

public class PlayerEvent implements Listener {

	private Plugin plugin;
	private Economy economy;
	
	public PlayerEvent(Plugin pPlugin, Economy pEconomy) {
		this.plugin = pPlugin;
		this.economy = pEconomy;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		UUID lUUID = e.getPlayer().getUniqueId();
		PlayerConnected lConnexions = LoginHelper.connectedPlayers.get(lUUID);
		if (lConnexions == null) {
			return;
		}
		lConnexions.position.setLocation(e.getTo().getX(), e.getTo().getY(), e.getTo().getZ());
		PositionMessage lPositionMessage = new PositionMessage(lConnexions.position.x, lConnexions.position.y, lConnexions.position.z);
		lConnexions.connexions.forEach((pConnexion) -> {
			pConnexion.send(lPositionMessage);
		});
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player lPlayer = e.getPlayer();
		String lUuid = lPlayer.getUniqueId().toString();
		fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid);
		if (lPlayerDB == null) {
			lPlayerDB = new fr.chklang.minecraft.shoping.model.Player(lUuid);
			lPlayerDB.save();
		}
		
		MessagesHelper.broadcastEventToAllPlayers(new PlayerJoinEventMessage(lPlayerDB.getId(), JoinType.CONNEXION, lUuid, lPlayer.getName(), lPlayer.isOp()), true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player lPlayer = e.getPlayer();
		String lUuid = lPlayer.getUniqueId().toString();
		fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid);
		MessagesHelper.broadcastEventToAllPlayers(new PlayerJoinEventMessage(lPlayerDB.getId(), JoinType.DECONNEXION, lUuid, lPlayer.getName(), lPlayer.isOp()), true);
	}
	
	@EventHandler
	public void onInventoryEvent(InventoryEvent e) {
		Inventory lInventory = e.getInventory();
		System.out.println("Inventory event, type : " + lInventory.getType());
		if (lInventory.getType() != InventoryType.PLAYER) {
			return;
		}
		List<HumanEntity> lViewer = lInventory.getViewers();
		lViewer.forEach((HumanEntity pViewer) -> {
			System.out.println("Inventory event : " + pViewer.getName());
		});
	}
}
