package fr.chklang.minecraft.shoping.json.login;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginCheckConnexionMessage extends AbstractMessage<LoginCheckConnexionContent> {

	public LoginCheckConnexionMessage() {
	}

	@Override
	public String toString() {
		return "LoginCheckConnexionMessage [], super : " + super.toString();
	}

	@Override
	public void execute(IConnexion pConnexion) {
		String lToken = this.content.token;
		PlayerConnected lPlayerConnected = LoginHelper.connectedPlayersByKeyLogin.get(lToken);
		if (lPlayerConnected == null) {
			pConnexion.send(new Response(this, false, null));
			return;
		}
		lPlayerConnected.connexions.add(pConnexion);
		pConnexion.setToken(lToken);
		pConnexion.send(new Response(this, true, lPlayerConnected.player));
	}
	
	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pTokenIsOk, String pPseudo) {
			super(pOrigin);
			this.content = new ResponseContent(pTokenIsOk, pPseudo);
		}
	}
	
	public static class ResponseContent {
		public final boolean tokenIsOk;
		public final String pseudo;
		public ResponseContent(boolean pTokenIsOk, String pPseudo) {
			super();
			this.tokenIsOk = pTokenIsOk;
			this.pseudo = pPseudo;
		}
		
	}
}
