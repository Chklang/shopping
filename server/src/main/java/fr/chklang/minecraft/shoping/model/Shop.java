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

	private long x1;
	private long y1;
	private long z1;
	
	private long x2;
	private long y2;
	private long z2;
	
	private long x3;
	private long y3;
	private long z3;
	
	public Player owner;

	public Shop() {
		super();
	}

	public Shop(long pId, long pX1, long pY1, long pZ1, long pX2, long pY2, long pZ2, long pX3, long pY3, long pZ3,
			Player pOwner) {
		super();
		this.id = pId;
		this.x1 = pX1;
		this.y1 = pY1;
		this.z1 = pZ1;
		this.x2 = pX2;
		this.y2 = pY2;
		this.z2 = pZ2;
		this.x3 = pX3;
		this.y3 = pY3;
		this.z3 = pZ3;
		this.owner = pOwner;
	}
	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
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
		if (this.id != other.id)
			return false;
		if (this.owner == null) {
			if (other.owner != null)
				return false;
		} else if (!this.owner.equals(other.owner))
			return false;
		if (this.x1 != other.x1)
			return false;
		if (this.x2 != other.x2)
			return false;
		if (this.x3 != other.x3)
			return false;
		if (this.y1 != other.y1)
			return false;
		if (this.y2 != other.y2)
			return false;
		if (this.y3 != other.y3)
			return false;
		if (this.z1 != other.z1)
			return false;
		if (this.z2 != other.z2)
			return false;
		if (this.z3 != other.z3)
			return false;
		return true;
	}

	public long getId() {
		return this.id;
	}

	public Player getOwner() {
		return this.owner;
	}

	public long getX1() {
		return this.x1;
	}

	public long getX2() {
		return this.x2;
	}

	public long getX3() {
		return this.x3;
	}

	public long getY1() {
		return this.y1;
	}

	public long getY2() {
		return this.y2;
	}

	public long getY3() {
		return this.y3;
	}

	public long getZ1() {
		return this.z1;
	}

	public long getZ2() {
		return this.z2;
	}

	public long getZ3() {
		return this.z3;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
		result = prime * result + (int) (this.x1 ^ (this.x1 >>> 32));
		result = prime * result + (int) (this.x2 ^ (this.x2 >>> 32));
		result = prime * result + (int) (this.x3 ^ (this.x3 >>> 32));
		result = prime * result + (int) (this.y1 ^ (this.y1 >>> 32));
		result = prime * result + (int) (this.y2 ^ (this.y2 >>> 32));
		result = prime * result + (int) (this.y3 ^ (this.y3 >>> 32));
		result = prime * result + (int) (this.z1 ^ (this.z1 >>> 32));
		result = prime * result + (int) (this.z2 ^ (this.z2 >>> 32));
		result = prime * result + (int) (this.z3 ^ (this.z3 >>> 32));
		return result;
	}

	@Override
	public Shop save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				PreparedStatement lStatement = lDB.prepare("INSERT INTO shopping_shops (x1, y1, z1, x2, y2, z2, x3, y3, z3, owner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				lStatement.setLong(1, this.x1);
				lStatement.setLong(2, this.y1);
				lStatement.setLong(3, this.z1);
				lStatement.setLong(4, this.x2);
				lStatement.setLong(5, this.y2);
				lStatement.setLong(6, this.z2);
				lStatement.setLong(7, this.x3);
				lStatement.setLong(8, this.y3);
				lStatement.setLong(9, this.z3);
				if (this.owner != null) {
					lStatement.setLong(10, this.owner.getId());
				} else {
					lStatement.setNull(10, Types.INTEGER);
				}
				List<Long> lIds = lDB.insert(lStatement);
				if (lIds.size() < 1) {
					throw new RuntimeException("Insertion failed!");
				}
				this.id = lIds.get(0);
				this.isExistsIntoDB = true;
			} else {
				// Update
				PreparedStatement lStatement = lDB
						.prepare("UPDATE shopping_shops SET x1 = ?, y1 = ?, z1 = ?, x2 = ?, y2 = ?, z2 = ?, x3 = ?, y3 = ?, z3 = ?, owner = ? WHERE id = ?");
				lStatement.setLong(1, this.x1);
				lStatement.setLong(2, this.y1);
				lStatement.setLong(3, this.z1);
				lStatement.setLong(4, this.x2);
				lStatement.setLong(5, this.y2);
				lStatement.setLong(6, this.z2);
				lStatement.setLong(7, this.x3);
				lStatement.setLong(8, this.y3);
				lStatement.setLong(9, this.z3);
				if (this.owner != null) {
					lStatement.setLong(10, this.owner.getId());
				} else {
					lStatement.setNull(10, Types.INTEGER);
				}
				lStatement.setLong(11, this.id);
				lDB.query(lStatement);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public void setId(long pId) {
		this.id = pId;
	}

	public void setOwner(Player pOwner) {
		this.owner = pOwner;
	}

	public void setX1(long pX1) {
		this.x1 = pX1;
	}

	public void setX2(long pX2) {
		this.x2 = pX2;
	}

	public void setX3(long pX3) {
		this.x3 = pX3;
	}

	public void setY1(long pY1) {
		this.y1 = pY1;
	}

	public void setY2(long pY2) {
		this.y2 = pY2;
	}

	public void setY3(long pY3) {
		this.y3 = pY3;
	}

	public void setZ1(long pZ1) {
		this.z1 = pZ1;
	}

	public void setZ2(long pZ2) {
		this.z2 = pZ2;
	}

	public void setZ3(long pZ3) {
		this.z3 = pZ3;
	}

	@Override
	public String toString() {
		return "Shop [id=" + this.id + ", x1=" + this.x1 + ", y1=" + this.y1 + ", z1=" + this.z1 + ", x2=" + this.x2
				+ ", y2=" + this.y2 + ", z2=" + this.z2 + ", x3=" + this.x3 + ", y3=" + this.y3 + ", z3=" + this.z3
				+ ", owner=" + this.owner + "], " + super.toString();
	}
}
