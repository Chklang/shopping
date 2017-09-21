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
			pConnexion.send(new Response(this, false, 0, null, 0, 0, 0, false));
			return;
		}
		lPlayerConnected.connexions.add(pConnexion);
		pConnexion.setToken(lToken);
		pConnexion.setPlayer(lPlayerConnected);
		pConnexion.send(new Response(this, true, lPlayerConnected.idUser, lPlayerConnected.player.getName(), lPlayerConnected.player.getLocation().getX(), lPlayerConnected.player.getLocation().getY(),
				lPlayerConnected.player.getLocation().getZ(), lPlayerConnected.player.isOp()));
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pTokenIsOk, long pIdPlayer, String pPseudo, double pX, double pY, double pZ, boolean pIsOp) {
			super(pOrigin);
			this.content = new ResponseContent(pTokenIsOk, pIdPlayer, pPseudo, pX, pY, pZ, pIsOp);
		}
	}

	public static class ResponseContent {
		public final long idPlayer;
		public final boolean tokenIsOk;
		public final String pseudo;
		public final double x;
		public final double y;
		public final double z;
		public final boolean isOp;

		public ResponseContent(boolean pTokenIsOk, long pIdPlayer, String pPseudo, double pX, double pY, double pZ, boolean pIsOp) {
			super();
			this.tokenIsOk = pTokenIsOk;
			this.idPlayer = pIdPlayer;
			this.pseudo = pPseudo;
			this.x = pX;
			this.y = pY;
			this.z = pZ;
			this.isOp = pIsOp;
		}

	}
}
