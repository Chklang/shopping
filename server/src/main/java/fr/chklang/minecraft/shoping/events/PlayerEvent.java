package fr.chklang.minecraft.shoping.events;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.PositionMessage;

public class PlayerEvent implements Listener {
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		UUID lUUID = e.getPlayer().getUniqueId();
		PlayerConnected lConnexions = LoginHelper.connectedPlayers.get(lUUID);
		if (lConnexions == null) {
			return;
		}
		lConnexions.position.setLocation(e.getTo().getX(),e.getTo().getY(),e.getTo().getZ());
		PositionMessage lPositionMessage = new PositionMessage(lConnexions.position.x, lConnexions.position.y, lConnexions.position.z);
		lConnexions.connexions.forEach((pConnexion) -> {
			pConnexion.send(lPositionMessage);
		});
	}
}
