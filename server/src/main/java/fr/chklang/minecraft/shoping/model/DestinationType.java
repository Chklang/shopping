package fr.chklang.minecraft.shoping.model;

public enum DestinationType {

	SHOP(1),
	PLAYER(2);
	
	public final int id;
	DestinationType(int pId) {
		this.id = pId;
	}
	
	public static DestinationType getByType(int pType) {
		for (DestinationType lDestinationType : DestinationType.values()) {
			if (lDestinationType.id == pType) {
				return lDestinationType;
			}
		}
		return null;
	}
}
