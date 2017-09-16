package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginGetTokenMessage extends AbstractMessage<LoginGetTokenContent> {

	public LoginGetTokenMessage() {
	}

	@Override
	public String toString() {
		return "LoginGetTokenMessage [], super : " + super.toString();
	}

	@Override
	public void execute(IConnexion pConnexion) {
		final long lIdPlayer = this.content.idPlayer;
		fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.get(lIdPlayer);
		if (lPlayerDB == null) {
			pConnexion.send(new Response(this, false));
			return;
		}
		Player lPlayer = Bukkit.getPlayer(UUID.fromString(lPlayerDB.getUuid()));
		if (lPlayer == null) {
			pConnexion.send(new Response(this, false));
			return;
		}
		String lKey = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
		lPlayer.sendMessage("Id for web connexion : " + lKey);
		System.out.println("Player : " + lPlayer.getName() + ", key : " + lKey);
		pConnexion.getTempDatas().put("LOGIN_KEY", lKey);
		pConnexion.getTempDatas().put("LOGIN_UUID", lPlayer.getUniqueId());
		pConnexion.send(new Response(this, true));
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pPlayerFound) {
			super(pOrigin);
			this.content = new ResponseContent(pPlayerFound);
		}
	}

	public static class ResponseContent {
		public final boolean playerFound;

		public ResponseContent(boolean pPlayerFound) {
			super();
			this.playerFound = pPlayerFound;
		}

	}
}
