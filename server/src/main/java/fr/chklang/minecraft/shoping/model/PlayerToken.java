package fr.chklang.minecraft.shoping.model;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import fr.chklang.minecraft.shoping.db.DBManager;
import lib.PatPeter.SQLibrary.Database;

public class PlayerToken extends AbstractModel<PlayerToken> {

	private String token;
	private Player player;

	public PlayerToken() {
		super();
	}

	public PlayerToken(String pToken, Player pPlayer) {
		super();
		this.token = pToken;
		this.player = pPlayer;
	}

	@Override
	public void delete() {
		try {
			if (!this.isExistsIntoDB) {
				throw new RuntimeException("This object doesn't exists into DB! : " + this.toString());
			}
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("DELETE FROM shopping_players_tokens WHERE token = ?");
			lStatement.setString(1, this.token);
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
		PlayerToken other = (PlayerToken) obj;
		if (this.player == null) {
			if (other.player != null)
				return false;
		} else if (!this.player.equals(other.player))
			return false;
		if (this.token == null) {
			if (other.token != null)
				return false;
		} else if (!this.token.equals(other.token))
			return false;
		return true;
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getToken() {
		return this.token;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.player == null) ? 0 : this.player.hashCode());
		result = prime * result + ((this.token == null) ? 0 : this.token.hashCode());
		return result;
	}

	@Override
	public PlayerToken save() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			if (!this.isExistsIntoDB) {
				// Create
				PreparedStatement lStatement = lDB.prepare("INSERT INTO shopping_players_tokens (token, idplayer) VALUES (?, ?)");
				lStatement.setString(1, this.token);
				lStatement.setLong(2, this.player.getId());
				List<Long> lIds = lDB.insert(lStatement);
				if (lIds.size() < 1) {
					throw new RuntimeException("Insertion failed!");
				}
				this.isExistsIntoDB = true;
			} else {
				// Update
				PreparedStatement lStatement = lDB.prepare("UPDATE shopping_players_tokens SET idplayer = ? WHERE token = ?");
				lStatement.setLong(1, this.player.getId());
				lStatement.setString(2, this.token);
				lDB.query(lStatement);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	public PlayerToken setPlayer(Player pPlayer) {
		this.player = pPlayer;
		return this;
	}

	public PlayerToken setToken(String pToken) {
		this.token = pToken;
		return this;
	}

	@Override
	public String toString() {
		return "PlayerToken [token=" + this.token + ", player=" + this.player + "], " + super.toString();
	}

}
