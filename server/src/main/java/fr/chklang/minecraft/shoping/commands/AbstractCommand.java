package fr.chklang.minecraft.shoping.commands;

import org.bukkit.command.CommandExecutor;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.json.AbstractEvent;

public abstract class AbstractCommand implements CommandExecutor {

	protected void broadcastEvent(AbstractEvent<?> pMessage) {
		LoginHelper.connectedPlayers.values().forEach((pPlayerConnected) -> {
			pPlayerConnected.connexions.forEach((pConnexion) -> {
				pConnexion.send(pMessage);
			});
		});
	}
}
