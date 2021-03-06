package fr.chklang.minecraft.shoping.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import fr.chklang.minecraft.shoping.dao.ShopItemDao;
import fr.chklang.minecraft.shoping.db.DBManager;
import lib.PatPeter.SQLibrary.Database;

public class ShopItem extends AbstractModel<ShopItem> {
	
	public static final ShopItemDao DAO = new ShopItemDao();

	private Shop shop;
	private int idItem;
	private int subIdItem;
	private long sell;
	private long buy;
	private Double price;
	private Double margin;
	private long quantity;

	public ShopItem() {
		super();
	}

	public ShopItem(Shop pShop, int pIdItem, int pSubIdItem, long pSell, long pBuy, Double pPrice, Double pMargin,
			long pQuantity) {
		super();
		this.shop = pShop;
		this.idItem = pIdItem;
		this.subIdItem = pSubIdItem;
		this.sell = pSell;
		this.buy = pBuy;
		this.price = pPrice;
		this.margin = pMargin;
		this.quantity = pQuantity;
		this.isExistsIntoDB = true;
	}

	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("DELETE FROM shopping_shops_items WHERE idshop = ?");
			lStatement.setLong(1, this.shop.getId());
			lDB.query(lStatement);
			this.isExistsIntoDB = false;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShopItem other = (ShopItem) obj;
		if (this.buy != other.buy)
			return false;
		if (this.idItem != other.idItem)
			return false;
		if (this.margin == null) {
			if (other.margin != null)
				return false;
		} else if (!this.margin.equals(other.margin))
			return false;
		if (this.price == null) {
			if (other.price != null)
				return false;
		} else if (!this.price.equals(other.price))
			return false;
		if (this.quantity != other.quantity)
			return false;
		if (this.sell != other.sell)
			return false;
		if (this.shop == null) {
			if (other.shop != null)
				return false;
		} else if (!this.shop.equals(other.shop))
			return false;
		if (this.subIdItem != other.subIdItem)
			return false;
		return true;
	}

	public long getBuy() {
		return this.buy;
	}

	public int getIdItem() {
		return this.idItem;
	}

	public Double getMargin() {
		return this.margin;
	}

	public Double getPrice() {
		return this.price;
	}

	public long getQuantity() {
		return this.quantity;
	}

	public long getSell() {
		return this.sell;
	}

	public Shop getShop() {
		return this.shop;
	}

	public int getSubIdItem() {
		return this.subIdItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.buy ^ (this.buy >>> 32));
		result = prime * result + this.idItem;
		result = prime * result + ((this.margin == null) ? 0 : this.margin.hashCode());
		result = prime * result + ((this.price == null) ? 0 : this.price.hashCode());
		result = prime * result + (int) (this.quantity ^ (this.quantity >>> 32));
		result = prime * result + (int) (this.sell ^ (this.sell >>> 32));
		result = prime * result + ((this.shop == null) ? 0 : this.shop.hashCode());
		result = prime * result + this.subIdItem;
		return result;
	}

	@Override
	public ShopItem save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				System.out.println("Create " + this.toString());
				PreparedStatement lStatement = lDB.prepare("INSERT INTO shopping_shops_items (idshop, iditem, subiditem, sell, buy, price, margin, quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
				lStatement.setLong(1, this.shop.getId());
				lStatement.setInt(2, this.idItem);
				lStatement.setInt(3, this.subIdItem);
				lStatement.setLong(4, this.sell);
				lStatement.setLong(5, this.buy);
				if (this.price == null) {
					lStatement.setNull(6, Types.INTEGER);
				} else {
					lStatement.setDouble(6, this.price);
				}
				if (this.margin == null) {
					lStatement.setNull(7, Types.INTEGER);
				} else {
					lStatement.setDouble(7, this.margin);
				}
				lStatement.setLong(8, this.quantity);
				List<Long> lIds = lDB.insert(lStatement);
				if (lIds.size() < 1) {
					throw new RuntimeException("Insertion failed!");
				}
				this.isExistsIntoDB = true;
			} else {
				// Update
				System.out.println("Update " + this.toString());
				PreparedStatement lStatement = lDB
						.prepare("UPDATE shopping_shops_items SET sell = ?, buy = ?, price = ?, margin = ?, quantity = ? WHERE idshop = ? AND iditem = ? AND subiditem = ?");
				lStatement.setLong(1, this.sell);
				lStatement.setLong(2, this.buy);
				if (this.price == null) {
					lStatement.setNull(3, Types.INTEGER);
				} else {
					lStatement.setDouble(3, this.price);
				}
				if (this.margin == null) {
					lStatement.setNull(4, Types.INTEGER);
				} else {
					lStatement.setDouble(4, this.margin);
				}
				lStatement.setLong(5, this.quantity);
				lStatement.setLong(6, this.shop.getId());
				lStatement.setInt(7, this.idItem);
				lStatement.setInt(8, this.subIdItem);
				lDB.query(lStatement);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return this;
	}


	public ShopItem setShop(Shop pShop) {
		this.shop = pShop;
		return this;
	}

	public ShopItem setIdItem(int pIdItem) {
		this.idItem = pIdItem;
		return this;
	}

	public ShopItem setSubIdItem(int pSubIdItem) {
		this.subIdItem = pSubIdItem;
		return this;
	}

	public ShopItem setSell(long pSell) {
		this.sell = pSell;
		return this;
	}

	public ShopItem setBuy(long pBuy) {
		this.buy = pBuy;
		return this;
	}

	public ShopItem setPrice(Double pPrice) {
		this.price = pPrice;
		return this;
	}

	public ShopItem setMargin(Double pMargin) {
		this.margin = pMargin;
		return this;
	}

	public ShopItem setQuantity(long pQuantity) {
		this.quantity = pQuantity;
		return this;
	}

	@Override
	public String toString() {
		return "ShopItem [shop=" + this.shop + ", idItem=" + this.idItem + ", subIdItem=" + this.subIdItem + ", sell=" + this.sell + ", buy=" + this.buy + ", price=" + this.price + ", margin="
				+ this.margin + ", quantity=" + this.quantity + "]";
	}
}
