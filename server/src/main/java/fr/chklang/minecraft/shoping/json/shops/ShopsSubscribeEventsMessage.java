package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsSubscribeEventsMessage extends AbstractMessage<ShopsSubscribeEventsContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		pConnexion.subscribeEventsShop(this.content.idShop);
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
