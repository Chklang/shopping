package fr.chklang.minecraft.shoping.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.model.Player;
import lib.PatPeter.SQLibrary.Database;

public class PlayerDao extends AbstractDao<Player, Long> {

	public PlayerDao() {
		super(Player.class);
	}

	@Override
	public Player get(Long pKey) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT uuid FROM shopping_players WHERE id = ?");
			lStatement.setLong(1, pKey);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				String lUuid = lResultSet.getString("uuid");
				return new Player(pKey, lUuid);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Player getByUuid(String pUuid) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id FROM shopping_players WHERE uuid = ?");
			lStatement.setString(1, pUuid);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lId = lResultSet.getLong("id");
				return new Player(lId, pUuid);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Player> getAll() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id, uuid FROM shopping_players");
			ResultSet lResultSet = lDB.query(lStatement);
			List<Player> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				String lUuid = lResultSet.getString("uuid");
				lResults.add(new Player(lId, lUuid));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
