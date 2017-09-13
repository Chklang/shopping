package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

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
			pConnexion.send(new Response(this, false, null));
		}
		if (!lTempKey.equals(this.content.key)) {
			pConnexion.send(new Response(this, false, null));
		}
		String lKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		LoginHelper.PlayerConnected lPlayerConnected = new LoginHelper.PlayerConnected();
		lPlayerConnected.connexions.add(pConnexion);
		lPlayerConnected.player = (String) pConnexion.getTempDatas().get("LOGIN_PSEUDO");
		LoginHelper.connectedPlayers.put(lKey, lPlayerConnected);
		pConnexion.send(new Response(this, true, null));
	}
	
	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pKeyisOk, String pToken) {
			super(pOrigin);
			this.content = new ResponseContent(pKeyisOk, pToken);
		}
	}
	
	public static class ResponseContent {
		public final boolean keyIsOk;
		public final String token;
		public ResponseContent(boolean pKeyIsOk, String pToken) {
			super();
			this.keyIsOk = pKeyIsOk;
			this.token = pToken;
		}
		
	}
}
