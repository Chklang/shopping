package fr.chklang.minecraft.shoping.json.login;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.chklang.minecraft.shoping.Wrapper;
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
		final String lPseudo = this.content.pseudo;
		if (lPseudo == null) {
			return;
		}
		Wrapper<Player> lPlayerUuid = new Wrapper<>(null);
		Bukkit.getServer().getOnlinePlayers().forEach((pPlayer) -> {
			if (lPseudo.equalsIgnoreCase(pPlayer.getName())) {
				lPlayerUuid.e = pPlayer;
			}
		});
		if (lPlayerUuid.e == null) {
			pConnexion.send(new Response(this, false));
			return;
		}
		Player lPlayer = lPlayerUuid.e;
		String lKey = UUID.randomUUID().toString().replace("-", "").substring(0, 5).toUpperCase();
		lPlayer.sendMessage("Id for web connexion : " + lKey);
		System.out.println("Player : " + this.content.pseudo + ", key : " + lKey);
		pConnexion.getTempDatas().put("LOGIN_KEY", lKey);
		pConnexion.getTempDatas().put("LOGIN_UUID", lPlayerUuid.e.getUniqueId());
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
