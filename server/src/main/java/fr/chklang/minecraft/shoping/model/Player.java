package fr.chklang.minecraft.shoping.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import fr.chklang.minecraft.shoping.dao.PlayerDao;
import fr.chklang.minecraft.shoping.db.DBManager;
import lib.PatPeter.SQLibrary.Database;

public class Player extends AbstractModel<Player> {
	
	public static final PlayerDao DAO = new PlayerDao();
	
	private long id;
	private String uuid;

	public Player() {
		super();
	}

	public Player(long pId, String pUuid) {
		super();
		this.id = pId;
		this.uuid = pUuid;
		this.isExistsIntoDB = true;
	}

	public Player(String pUuid) {
		super();
		this.uuid = pUuid;
	}

	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("DELETE FROM shopping_players WHERE id = ?");
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
		Player other = (Player) obj;
		if (this.id != other.id)
			return false;
		if (this.uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!this.uuid.equals(other.uuid))
			return false;
		return true;
	}

	public long getId() {
		return this.id;
	}

	public String getUuid() {
		return this.uuid;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (this.id ^ (this.id >>> 32));
		result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
		return result;
	}

	@Override
	public Player save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				PreparedStatement lStatement = lDB.prepare("INSERT INTO shopping_players (uuid) VALUES (?)");
				lStatement.setString(1, this.uuid);
				List<Long> lIds = lDB.insert(lStatement);
				if (lIds.size() < 1) {
					throw new RuntimeException("Insertion failed!");
				}
				this.id = lIds.get(0);
				this.isExistsIntoDB = true;
			} else {
				// Update
				PreparedStatement lStatement = lDB
						.prepare("UPDATE shopping_players SET uuid = ? WHERE id = ?");
				lStatement.setString(1, this.uuid);
				lStatement.setLong(2, this.id);
				lDB.query(lStatement);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return this;
	}


	public Player setId(long pId) {
		this.id = pId;
		return this;
	}

	public Player setUuid(String pUuid) {
		this.uuid = pUuid;
		return this;
	}

	@Override
	public String toString() {
		return "Player [id=" + this.id + ", uuid=" + this.uuid + "], " + super.toString();
	}
}
