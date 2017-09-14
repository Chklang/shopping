package fr.chklang.minecraft.shoping.json;

import fr.chklang.minecraft.shoping.servlets.IConnexion;

public class PositionMessage extends AbstractMessage<PositionMessage.PositionContent> {
	
	public PositionMessage(double pX, double pY, double pZ) {
		super();
		this.content = new PositionContent(pX, pY, pZ);
	}

	public static class PositionContent extends AbstractContent {
		public double x;
		public double y;
		public double z;
		public PositionContent(double pX, double pY, double pZ) {
			super();
			this.x = pX;
			this.y = pY;
			this.z = pZ;
		}
	}

	@Override
	public void execute(IConnexion pConnexion) {
	}
}
