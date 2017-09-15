package fr.chklang.minecraft.shoping.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import fr.chklang.minecraft.shoping.dao.CaravanDao;
import fr.chklang.minecraft.shoping.db.DBManager;
import lib.PatPeter.SQLibrary.Database;

public class Caravan extends AbstractModel<Caravan> {

	public static final CaravanDao DAO = new CaravanDao();
	
	private long id;
	private long idFrom;
	private long idTo;
	private DestinationType fromType;
	private DestinationType toType;
	private int idItem;
	private int subIdItem;
	private long quantity;
	private long startAt;
	private long arriveAt;
	private Player owner;

	public Caravan() {
		super();
	}

	public Caravan(long pId, long pIdFrom, long pIdTo, DestinationType pFromType, DestinationType pToType, int pIdItem, int pSubIdItem, long pQuantity, long pStartAt, long pArriveAt, Player pOwner) {
		super();
		this.id = pId;
		this.idFrom = pIdFrom;
		this.idTo = pIdTo;
		this.fromType = pFromType;
		this.toType = pToType;
		this.idItem = pIdItem;
		this.subIdItem = pSubIdItem;
		this.quantity = pQuantity;
		this.startAt = pStartAt;
		this.arriveAt = pArriveAt;
		this.owner = pOwner;
		this.isExistsIntoDB = true;
	}

	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("DELETE FROM shopping_caravans WHERE id = ?");
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
		Caravan other = (Caravan) obj;
		if (this.arriveAt != other.arriveAt)
			return false;
		if (this.fromType != other.fromType)
			return false;
		if (this.id != other.id)
			return false;
		if (this.idFrom != other.idFrom)
			return false;
		if (this.idItem != other.idItem)
			return false;
		if (this.idTo != other.idTo)
			return false;
		if (this.owner == null) {
			if (other.owner != null)
				return false;
		} else if (!this.owner.equals(other.owner))
			return false;
		if (this.quantity != other.quantity)
			return false;
		if (this.startAt != other.startAt)
			return false;
		if (this.subIdItem != other.subIdItem)
			return false;
		if (this.toType != other.toType)
			return false;
		return true;
	}

	public long getArriveAt() {
		return this.arriveAt;
	}

	public DestinationType getFromType() {
		return this.fromType;
	}

	public long getId() {
		return this.id;
	}

	public long getIdFrom() {
		return this.idFrom;
	}

	public int getIdItem() {
		return this.idItem;
	}

	public long getIdTo() {
		return this.idTo;
	}

	public Player getOwner() {
		return this.owner;
	}

	public long getQuantity() {
		return this.quantity;
	}

	public long getStartAt() {
		return this.startAt;
	}

	public int getSubIdItem() {
		return this.subIdItem;
	}

	public DestinationType getToType() {
		return this.toType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.arriveAt ^ (this.arriveAt >>> 32));
		result = prime * result + ((this.fromType == null) ? 0 : this.fromType.hashCode());
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + (int) (this.idFrom ^ (this.idFrom >>> 32));
		result = prime * result + this.idItem;
		result = prime * result + (int) (this.idTo ^ (this.idTo >>> 32));
		result = prime * result + ((this.owner == null) ? 0 : this.owner.hashCode());
		result = prime * result + (int) (this.quantity ^ (this.quantity >>> 32));
		result = prime * result + (int) (this.startAt ^ (this.startAt >>> 32));
		result = prime * result + this.subIdItem;
		result = prime * result + ((this.toType == null) ? 0 : this.toType.hashCode());
		return result;
	}

	@Override
	public Caravan save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				PreparedStatement lStatement = lDB
						.prepare("INSERT INTO shopping_caravans (idfrom, idto, fromtype, totype, iditem, subiditem, quantity, startat, arriveat, owner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				lStatement.setLong(1, this.idFrom);
				lStatement.setLong(2, this.idTo);
				lStatement.setInt(3, this.fromType.id);
				lStatement.setInt(4, this.toType.id);
				lStatement.setInt(5, this.idItem);
				lStatement.setInt(6, this.subIdItem);
				lStatement.setLong(7, this.quantity);
				lStatement.setLong(8, this.startAt);
				lStatement.setLong(9, this.arriveAt);

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
				PreparedStatement lStatement = lDB.prepare(
						"UPDATE shopping_caravans SET idfrom = ?, idto = ?, fromtype = ?, totype = ?, iditem = ?, subiditem = ?, quantity = ?, startat = ?, arriveat = ?, owner = ? WHERE id = ?");
				lStatement.setLong(2, this.idTo);
				lStatement.setInt(3, this.fromType.id);
				lStatement.setInt(4, this.toType.id);
				lStatement.setInt(5, this.idItem);
				lStatement.setInt(6, this.subIdItem);
				lStatement.setLong(7, this.quantity);
				lStatement.setLong(8, this.startAt);
				lStatement.setLong(9, this.arriveAt);

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

	public Caravan setArriveAt(long pArriveAt) {
		this.arriveAt = pArriveAt;
		return this;
	}

	public Caravan setFromType(DestinationType pFromType) {
		this.fromType = pFromType;
		return this;
	}

	public Caravan setId(long pId) {
		this.id = pId;
		return this;
	}

	public Caravan setIdFrom(long pIdFrom) {
		this.idFrom = pIdFrom;
		return this;
	}

	public Caravan setIdItem(int pIdItem) {
		this.idItem = pIdItem;
		return this;
	}

	public Caravan setIdTo(long pIdTo) {
		this.idTo = pIdTo;
		return this;
	}

	public Caravan setOwner(Player pOwner) {
		this.owner = pOwner;
		return this;
	}

	public Caravan setQuantity(long pQuantity) {
		this.quantity = pQuantity;
		return this;
	}

	public Caravan setStartAt(long pStartAt) {
		this.startAt = pStartAt;
		return this;
	}

	public Caravan setSubIdItem(int pSubIdItem) {
		this.subIdItem = pSubIdItem;
		return this;
	}

	public Caravan setToType(DestinationType pToType) {
		this.toType = pToType;
		return this;
	}
}
