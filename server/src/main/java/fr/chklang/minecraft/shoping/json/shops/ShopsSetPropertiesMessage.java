package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsSetPropertiesMessage extends AbstractMessage<ShopsSetPropertiesContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		PlayerConnected lPlayer = pConnexion.getPlayer();
		if (lPlayer == null) {
			System.err.println("Player not connected");
			pConnexion.send(new Response(this, false));
			return;
		}
		Shop lShop = Shop.DAO.get(this.content.idShop);
		if (lShop == null) {
			System.err.println("Shop not found");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (lShop.getOwner() == null && !lPlayer.player.isOp()) {
			System.err.println("Player isn't operator");
			pConnexion.send(new Response(this, false));
			return;
		}
		if (lShop.getOwner() != null && 
				lShop.getOwner().getId() != lPlayer.idUser && 
				!lPlayer.player.isOp()) {
			System.err.println("Player isn't the owner");
			pConnexion.send(new Response(this, false));
			return;
		}
		lShop.setName(this.content.name);
		lShop.setBaseMargin(this.content.baseMargin);
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
