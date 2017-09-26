package fr.chklang.minecraft.shoping;

public class Config {

	private static Config instance;

	public static Config getInstance() {
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	private int port;
	private double spacePrice;
	private long baseSpace;

	private Config() {
		super();
		// Base config
		// Remove it to load from config file
		this.port = 8080;
		this.baseSpace = 500;
		this.spacePrice = 0.1;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Config other = (Config) obj;
		if (this.baseSpace != other.baseSpace)
			return false;
		if (this.port != other.port)
			return false;
		if (Double.doubleToLongBits(this.spacePrice) != Double.doubleToLongBits(other.spacePrice))
			return false;
		return true;
	}

	public long getBaseSpace() {
		return this.baseSpace;
	}

	public int getPort() {
		return this.port;
	}

	public double getSpacePrice() {
		return this.spacePrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.baseSpace ^ (this.baseSpace >>> 32));
		result = prime * result + this.port;
		long temp;
		temp = Double.doubleToLongBits(this.spacePrice);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "Config [port=" + this.port + ", spacePrice=" + this.spacePrice + ", baseSpace=" + this.baseSpace + "]";
	}

}
