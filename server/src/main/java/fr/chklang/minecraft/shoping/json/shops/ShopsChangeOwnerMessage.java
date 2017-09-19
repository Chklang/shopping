package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.model.Player;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsChangeOwnerMessage extends AbstractMessage<ShopsChangeOwnerContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		PlayerConnected lPlayer = pConnexion.getPlayer();
		if (lPlayer == null) {
			System.err.println("Player not connected");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (!lPlayer.player.isOp()) {
			System.err.println("You must be an admin");
			pConnexion.send(new Response(this, false));
			return;
		}
		Shop lShop = Shop.DAO.get(this.content.idShop);
		if (lShop == null) {
			System.err.println("Shop not found");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (this.content.idOwner == null) {
			lShop.setOwner(null);
		} else {
			Player lNewOwner = Player.DAO.get(this.content.idOwner);
			if (lNewOwner == null) {
				System.err.println("New owner not found");
				pConnexion.send(new Response(this, false));
				return;
			}
			lShop.setOwner(lNewOwner);
		}
		lShop.save();
		pConnexion.send(new Response(this, true));
		return;
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pIsOk) {
			super(pOrigin);
			this.content = new ResponseContent(pIsOk);
		}
	}

	public static class ResponseContent {
		public final boolean isOk;

		public ResponseContent(boolean pIsOk) {
			super();
			this.isOk = pIsOk;
		}
	}

}
