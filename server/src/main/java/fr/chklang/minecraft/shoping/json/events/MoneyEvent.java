package fr.chklang.minecraft.shoping.json.events;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;
import fr.chklang.minecraft.shoping.json.events.MoneyEvent.MoneyEventContent;

public class MoneyEvent extends AbstractEvent<MoneyEventContent> {

	public MoneyEvent(long pIdPlayer, double pMoney) {
		super(new MoneyEventContent(pIdPlayer, pMoney));
	}

	public static class MoneyEventContent extends AbstractContent {
		public final long idPlayer;
		public final double money;
		public MoneyEventContent(long pIdPlayer, double pMoney) {
			super();
			this.idPlayer = pIdPlayer;
			this.money = pMoney;
		}
	}
	
}
