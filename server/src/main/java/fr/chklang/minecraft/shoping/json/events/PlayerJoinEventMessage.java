package fr.chklang.minecraft.shoping.json.events;

import fr.chklang.minecraft.shoping.json.AbstractContent;
import fr.chklang.minecraft.shoping.json.AbstractEvent;

public class PlayerJoinEventMessage extends AbstractEvent<AbstractContent>{
	
	public static enum JoinType {
		CONNEXION(1),
		DECONNEXION(2);
		
		public final int type;
		JoinType(int pType) {
			this.type = pType;
		}
	}

	public static class PlayerJoinEventContent extends AbstractContent {
		public final long idPlayer;
		public final int joinType;
		public final String uuid;
		public final String name;
		public final long money;
		public PlayerJoinEventContent(long pIdPlayer, JoinType pJoinType, String pUuid, String pName, long pMoney) {
			super();
			this.idPlayer = pIdPlayer;
			this.joinType = pJoinType.type;
			this.uuid = pUuid;
			this.name = pName;
			this.money = pMoney;
		}
	}

	public PlayerJoinEventMessage(long pIdPlayer, JoinType pJoinType, String pUuid, String pName, long pMoney) {
		super(new PlayerJoinEventContent(pIdPlayer, pJoinType, pUuid, pName, pMoney));
	}
}
