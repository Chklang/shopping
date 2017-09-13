package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.json.login.LoginSendTokenMessage.ResponseContent;
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
		String lKey = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
		System.out.println("Player : " + this.content.pseudo + ", key : " + lKey);
		pConnexion.getTempDatas().put("LOGIN_KEY", lKey);
		pConnexion.getTempDatas().put("LOGIN_PSEUDO", this.content.pseudo);
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
