package fr.chklang.minecraft.shoping.helpers;

import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractEvent;

public class MessagesHelper {

	public static void broadcastEventToAllPlayers(AbstractEvent<?> pMessage, boolean pSendToNotConnected) {
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

	public static void broadcastEventToAPlayer(AbstractEvent<?> pMessage, PlayerConnected pPlayerConnected) {
		pPlayerConnected.connexions.forEach((pConnexion) -> {
			pConnexion.send(pMessage);
		});
	}
}
