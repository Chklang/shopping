package fr.chklang.minecraft.shoping.json;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import fr.chklang.minecraft.shoping.model.Player;
import fr.chklang.minecraft.shoping.servlets.IConnexion;
import net.milkbowl.vault.economy.Economy;

public class PlayersGetPlayersMessage extends AbstractMessage<PlayersGetPlayersContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		Response lResponse = new Response(this);
		Economy lEconomy = this.getEconomy();
		Player.DAO.getAll().forEach((pPlayer) -> {
			OfflinePlayer lPlayer = Bukkit.getOfflinePlayer(UUID.fromString(pPlayer.getUuid()));
			lResponse.content.players.add(new ResponseContentElement(pPlayer.getId(), lPlayer.getName(), lPlayer.isOnline(), lEconomy.getBalance(lPlayer), lPlayer.isOp()));
		});
		pConnexion.send(lResponse);
		return;
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin) {
			super(pOrigin);
			this.content = new ResponseContent();
		}
	}

	public static class ResponseContent {
		public final List<ResponseContentElement> players = new ArrayList<>();
	}

	public static class ResponseContentElement {
		public final long idPlayer;

		public final String pseudo;
		public final boolean isOnline;
		public final double money;
		public final boolean isOp;
		
		public ResponseContentElement(long pIdPlayer, String pPseudo, boolean pIsOnline, double pMoney, boolean pIsOp) {
			super();
			this.idPlayer = pIdPlayer;
			this.pseudo = pPseudo;
			this.isOnline = pIsOnline;
			this.money = pMoney;
			this.isOp = pIsOp;
		}
	}
}
