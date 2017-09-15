package fr.chklang.minecraft.shoping.model;

public class ShopItemPk {

	private long idShop;

	private int idItem;

	private int subIdItem;

	public ShopItemPk() {
		super();
	}

	public ShopItemPk(long pIdShop, int pIdItem, int pSubIdItem) {
		super();
		this.idShop = pIdShop;
		this.idItem = pIdItem;
		this.subIdItem = pSubIdItem;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShopItemPk other = (ShopItemPk) obj;
		if (this.idItem != other.idItem)
			return false;
		if (this.idShop != other.idShop)
			return false;
		if (this.subIdItem != other.subIdItem)
			return false;
		return true;
	}

	public int getIdItem() {
		return this.idItem;
	}

	public long getIdShop() {
		return this.idShop;
	}

	public int getSubIdItem() {
		return this.subIdItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.idItem;
		result = prime * result + (int) (this.idShop ^ (this.idShop >>> 32));
		result = prime * result + this.subIdItem;
		return result;
	}

	public ShopItemPk setIdItem(int pIdItem) {
		this.idItem = pIdItem;
		return this;
	}

	public ShopItemPk setIdShop(long pIdShop) {
		this.idShop = pIdShop;
		return this;
	}

	public ShopItemPk setSubIdItem(int pSubIdItem) {
		this.subIdItem = pSubIdItem;
		return this;
	}

	@Override
	public String toString() {
		return "ShopItemPk [idShop=" + this.idShop + ", idItem=" + this.idItem + ", subIdItem=" + this.subIdItem + "], " + super.toString();
	}
}
