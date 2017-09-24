package fr.chklang.minecraft.shoping.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import fr.chklang.minecraft.shoping.dao.ShopDao;
import fr.chklang.minecraft.shoping.db.DBManager;
import lib.PatPeter.SQLibrary.Database;

public class Shop extends AbstractModel<Shop> {

	public static final ShopDao DAO = new ShopDao();

	private long id;
	
	private String name;

	private long x_min;
	private long x_max;
	private long y_min;
	private long y_max;
	private long z_min;
	private long z_max;
	
	public Player owner;
	
	private double baseMargin;
	private long space;

	public Shop() {
		super();
	}


	public Shop(long pId, String pName, long pX_min, long pX_max, long pY_min, long pY_max, long pZ_min, long pZ_max, Player pOwner, double pBaseMargin, long pSpace) {
		super();
		this.id = pId;
		this.name = pName;
		this.x_min = pX_min;
		this.x_max = pX_max;
		this.y_min = pY_min;
		this.y_max = pY_max;
		this.z_min = pZ_min;
		this.z_max = pZ_max;
		this.owner = pOwner;
		this.baseMargin = pBaseMargin;
		this.space = pSpace;
		this.isExistsIntoDB = true;
	}

	public String getName() {
		return this.name;
	}


	public Shop setName(String pName) {
		this.name = pName;
		return this;
	}


	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatementItems = lDB.prepare("DELETE FROM shopping_shops_items WHERE idshop = ?");
			lStatementItems.setLong(1, this.id);
			lDB.query(lStatementItems);
			PreparedStatement lStatement = lDB.prepare("DELETE FROM shopping_shops WHERE id = ?");
			lStatement.setLong(1, this.id);
			lDB.query(lStatement);
			this.isExistsIntoDB = false;
			this.id = 0;
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
		Shop other = (Shop) obj;
		if (Double.doubleToLongBits(this.baseMargin) != Double.doubleToLongBits(other.baseMargin))
			return false;
		if (this.id != other.id)
			return false;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		if (this.owner == null) {
			if (other.owner != null)
				return false;
		} else if (!this.owner.equals(other.owner))
			return false;
		if (this.space != other.space)
			return false;
		if (this.x_max != other.x_max)
			return false;
		if (this.x_min != other.x_min)
			return false;
		if (this.y_max != other.y_max)
			return false;
		if (this.y_min != other.y_min)
			return false;
		if (this.z_max != other.z_max)
			return false;
		if (this.z_min != other.z_min)
			return false;
		return true;
	}


	public double getBaseMargin() {
		return this.baseMargin;
	}

	public long getId() {
		return this.id;
	}


	public Player getOwner() {
		return this.owner;
	}


	public long getSpace() {
		return this.space;
	}


	public long getX_max() {
		return this.x_max;
	}


	public long getX_min() {
		return this.x_min;
	}


	public long getY_max() {
		return this.y_max;
	}


	public long getY_min() {
		return this.y_min;
	}


	public long getZ_max() {
		return this.z_max;
	}


	public long getZ_min() {
		return this.z_min;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.baseMargin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
		result = prime * result + (int) (this.space ^ (this.space >>> 32));
		result = prime * result + (int) (this.x_max ^ (this.x_max >>> 32));
		result = prime * result + (int) (this.x_min ^ (this.x_min >>> 32));
		result = prime * result + (int) (this.y_max ^ (this.y_max >>> 32));
		result = prime * result + (int) (this.y_min ^ (this.y_min >>> 32));
		result = prime * result + (int) (this.z_max ^ (this.z_max >>> 32));
		result = prime * result + (int) (this.z_min ^ (this.z_min >>> 32));
		return result;
	}


	@Override
	public Shop save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				PreparedStatement lStatement = lDB.prepare("INSERT INTO shopping_shops (name, x_min, x_max, y_min, y_max, z_min, z_max, owner, basemargin, space) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				lStatement.setString(1, this.name);
				lStatement.setLong(2, this.x_min);
				lStatement.setLong(3, this.x_max);
				lStatement.setLong(4, this.y_min);
				lStatement.setLong(5, this.y_max);
				lStatement.setLong(6, this.z_min);
				lStatement.setLong(7, this.z_max);
				if (this.owner != null) {
					lStatement.setLong(8, this.owner.getId());
				} else {
					lStatement.setNull(8, Types.INTEGER);
				}
				lStatement.setDouble(9, this.baseMargin);
				lStatement.setLong(10, this.space);
				List<Long> lIds = lDB.insert(lStatement);
				if (lIds.size() < 1) {
					throw new RuntimeException("Insertion failed!");
				}
				this.id = lIds.get(0);
				this.isExistsIntoDB = true;
			} else {
				// Update
				PreparedStatement lStatement = lDB.prepare("UPDATE shopping_shops SET name = ?, x_min = ?, x_max = ?, y_min = ?, y_max = ?, z_min = ?, z_max = ?, owner = ?, basemargin = ?, space = ? WHERE id = ?");
				lStatement.setString(1, this.name);
				lStatement.setLong(2, this.x_min);
				lStatement.setLong(3, this.x_max);
				lStatement.setLong(4, this.y_min);
				lStatement.setLong(5, this.y_max);
				lStatement.setLong(6, this.z_min);
				lStatement.setLong(7, this.z_max);
				if (this.owner != null) {
					lStatement.setLong(8, this.owner.getId());
				} else {
					lStatement.setNull(8, Types.INTEGER);
				}
				lStatement.setDouble(9, this.baseMargin);
				lStatement.setLong(10, this.space);
				lStatement.setLong(11, this.id);
				lDB.query(lStatement);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return this;
	}


	public Shop setBaseMargin(double pBaseMargin) {
		this.baseMargin = pBaseMargin;
		return this;
	}


	public Shop setId(long pId) {
		this.id = pId;
		return this;
	}


	public Shop setOwner(Player pOwner) {
		this.owner = pOwner;
		return this;
	}


	public Shop setSpace(long pSpace) {
		this.space = pSpace;
		return this;
	}


	public Shop setX_max(long pX_max) {
		this.x_max = pX_max;
		return this;
	}


	public Shop setX_min(long pX_min) {
		this.x_min = pX_min;
		return this;
	}


	public Shop setY_max(long pY_max) {
		this.y_max = pY_max;
		return this;
	}


	public Shop setY_min(long pY_min) {
		this.y_min = pY_min;
		return this;
	}


	public Shop setZ_max(long pZ_max) {
		this.z_max = pZ_max;
		return this;
	}


	public Shop setZ_min(long pZ_min) {
		this.z_min = pZ_min;
		return this;
	}


	@Override
	public String toString() {
		return "Shop [id=" + this.id + ", name=" + this.name + ", x_min=" + this.x_min + ", x_max=" + this.x_max + ", y_min=" + this.y_min + ", y_max=" + this.y_max + ", z_min=" + this.z_min
				+ ", z_max=" + this.z_max + ", owner=" + this.owner + ", baseMargin=" + this.baseMargin + ", space=" + this.space + "]";
	}

}
