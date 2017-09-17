package fr.chklang.minecraft.shoping.events;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.PositionMessage;
import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage;
import fr.chklang.minecraft.shoping.json.events.PlayerJoinEventMessage.JoinType;

public class PlayerEvent implements Listener {

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
		this.broadcastEvent(new PlayerJoinEventMessage(lPlayerDB.getId(), JoinType.CONNEXION, lUuid, lPlayer.getName(), 0), true);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player lPlayer = e.getPlayer();
		String lUuid = lPlayer.getUniqueId().toString();
		fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid);
		this.broadcastEvent(new PlayerJoinEventMessage(lPlayerDB.getId(), JoinType.DECONNEXION, lUuid, lPlayer.getName(), 0), true);
	}
	
	private void broadcastEvent(AbstractEvent<?> pMessage, boolean pSendToNotConnected) {
		LoginHelper.connectedPlayers.values().forEach((pPlayerConnected) -> {
			pPlayerConnected.connexions.forEach((pConnexion) -> {
				pConnexion.send(pMessage);
			});
		});
		if (pSendToNotConnected) {
			LoginHelper.notConnected.forEach((pConnexion) -> {
				pConnexion.send(pMessage);
			});
		}
	}
}
