package fr.chklang.minecraft.shoping.json.shops;

import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class ShopsSubscribeEventsMessage extends AbstractMessage<ShopsSubscribeEventsContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		pConnexion.subscribeEventsShop(this.content.idShop);
	}

}
