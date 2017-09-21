package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

import org.bukkit.Bukkit;

import fr.chklang.minecraft.shoping.Position;
import fr.chklang.minecraft.shoping.helpers.LoginHelper;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.model.Player;
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
			pConnexion.send(new Response(this, false, null, 0, null, 0, 0, 0, 0, false));
			return;
		}
		if (!lTempKey.equals(this.content.key)) {
			pConnexion.send(new Response(this, false, null, 0, null, 0, 0, 0, 0, false));
			return;
		}
		String lKey = UUID.randomUUID().toString().replace("-", "").toUpperCase();

		UUID lPlayerUuid = (UUID) pConnexion.getTempDatas().get("LOGIN_UUID");

		LoginHelper.PlayerConnected lPlayerConnected = LoginHelper.connectedPlayers.get(lPlayerUuid);
		if (lPlayerConnected == null) {
			Player lPlayerBD = Player.DAO.getByUuid(lPlayerUuid.toString());
			lPlayerConnected = new LoginHelper.PlayerConnected();
			lPlayerConnected.idUser = lPlayerBD.getId();
			lPlayerConnected.player = Bukkit.getPlayer(lPlayerUuid);
			lPlayerConnected.position = new Position(0, 0, 0);
			LoginHelper.connectedPlayers.put(lPlayerUuid, lPlayerConnected);
			LoginHelper.connectedPlayersByKeyLogin.put(lKey, lPlayerConnected);
		}

		lPlayerConnected.connexions.add(pConnexion);
		LoginHelper.notConnected.remove(pConnexion);
		pConnexion.setToken(lKey);
		pConnexion.setPlayer(lPlayerConnected);
		
		double lMoney = this.getEconomy().getBalance(lPlayerConnected.player);

		pConnexion.send(new Response(this, true, lKey, lPlayerConnected.idUser, lPlayerConnected.player.getName(), lMoney, lPlayerConnected.player.getLocation().getX(), lPlayerConnected.player.getLocation().getY(),
				lPlayerConnected.player.getLocation().getZ(),lPlayerConnected.player.isOp()));
	}
	
	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pKeyIsOk, String pToken, long pIdPlayer, String pPseudo, double pMoney, double pX, double pY, double pZ, boolean pIsOp) {
			super(pOrigin);
			this.content = new ResponseContent(pKeyIsOk, pToken, pIdPlayer, pPseudo, pMoney, pX, pY, pZ, pIsOp);
		}
	}

	public static class ResponseContent {
		public final boolean keyIsOk;
		public final String token;
		public final long idPlayer;
		public final String pseudo;
		public final double money;
		public final double x;
		public final double y;
		public final double z;
		public final boolean isOp;

		public ResponseContent(boolean pKeyIsOk, String pToken, long pIdPlayer, String pPseudo, double pMoney, double pX, double pY, double pZ, boolean pIsOp) {
			super();
			this.keyIsOk = pKeyIsOk;
			this.token = pToken;
			this.idPlayer = pIdPlayer;
			this.pseudo = pPseudo;
			this.money = pMoney;
			this.x = pX;
			this.y = pY;
			this.z = pZ;
			this.isOp = pIsOp;
		}
	}
}
