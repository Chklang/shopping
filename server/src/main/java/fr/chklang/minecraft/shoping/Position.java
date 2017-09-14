package fr.chklang.minecraft.shoping;

public class Position {

	public double x;
	public double y;
	public double z;
	public Position() {
		super();
	}
	public Position(double pX, double pY, double pZ) {
		super();
		this.x = pX;
		this.y = pY;
		this.z = pZ;
	}
	public void setLocation(double pX, double pY, double pZ) {
		this.x = pX;
		this.y = pY;
		this.z = pZ;
	}
}
