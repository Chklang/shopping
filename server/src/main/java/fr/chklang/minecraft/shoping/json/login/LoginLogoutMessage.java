package fr.chklang.minecraft.shoping.json.login;

import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class LoginLogoutMessage extends AbstractMessage<LoginLogoutContent> {

	public LoginLogoutMessage() {
	}

	@Override
	public String toString() {
		return " LoginLogoutMessage [], super : " + super.toString();
	}

	@Override
	public void execute(IConnexion pConnexion) {
		String lToken = pConnexion.getToken();
		if (lToken == null) {
			return;
		}
		
		PlayerConnected lPlayerConnected = LoginHelper.connectedPlayersByKeyLogin.get(lToken);
		if (lPlayerConnected == null) {
			return;
		}
		lPlayerConnected.connexions.remove(pConnexion);
		LoginHelper.connectedPlayersByKeyLogin.remove(lToken);
		pConnexion.send(new Response(this));
	}
	
	public static class Response extends AbstractResponse<Object> {

		public Response(AbstractMessage<?> pOrigin) {
			super(pOrigin);
		}
	}
}
