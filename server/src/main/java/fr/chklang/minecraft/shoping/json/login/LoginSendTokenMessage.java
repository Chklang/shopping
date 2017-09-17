package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

import org.bukkit.Bukkit;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginSendTokenMessage extends AbstractMessage<LoginSendTokenContent> {

	public LoginSendTokenMessage() {
	}

	@Override
	public String toString() {
		return "LoginSendTokenMessage [], super : " + super.toString();
	}

	@Override
	public void execute(IConnexion pConnexion) {
		String lTempKey = (String) pConnexion.getTempDatas().get("LOGIN_KEY");
		if (lTempKey == null) {
			pConnexion.send(new Response(this, false, null, null));
			return;
		}
		if (!lTempKey.equals(this.content.key)) {
			pConnexion.send(new Response(this, false, null, null));
			return;
		}
		String lKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		
		UUID lPlayerUuid = (UUID) pConnexion.getTempDatas().get("LOGIN_UUID");
		
		LoginHelper.PlayerConnected lPlayerConnected = LoginHelper.connectedPlayers.get(lPlayerUuid);
		if (lPlayerConnected == null) {
			lPlayerConnected = new LoginHelper.PlayerConnected();
			lPlayerConnected.player = Bukkit.getPlayer(lPlayerUuid);
			lPlayerConnected.position = new Position(0, 0, 0);
			LoginHelper.connectedPlayers.put(lPlayerUuid, lPlayerConnected);
			LoginHelper.connectedPlayersByKeyLogin.put(lKey, lPlayerConnected);
		}
		
		lPlayerConnected.connexions.add(pConnexion);
		LoginHelper.notConnected.remove(pConnexion);
		pConnexion.setToken(lKey);
		pConnexion.send(new Response(this, true, lKey, lPlayerConnected.player.getName()));
	}
	
	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pKeyisOk, String pToken, String pPseudo) {
			super(pOrigin);
			this.content = new ResponseContent(pKeyisOk, pToken, pPseudo);
		}
	}
	
	public static class ResponseContent {
		public final boolean keyIsOk;
		public final String token;
		public final String pseudo;
		public ResponseContent(boolean pKeyIsOk, String pToken, String pPseudo) {
			super();
			this.keyIsOk = pKeyIsOk;
			this.token = pToken;
			this.pseudo = pPseudo;
		}
		
	}
}
