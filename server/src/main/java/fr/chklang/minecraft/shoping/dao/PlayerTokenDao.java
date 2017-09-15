package fr.chklang.minecraft.shoping.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.model.Player;
import fr.chklang.minecraft.shoping.model.PlayerToken;
import lib.PatPeter.SQLibrary.Database;

public class PlayerTokenDao extends AbstractDao<PlayerToken, String> {

	public PlayerTokenDao() {
		super(PlayerToken.class);
	}

	@Override
	public PlayerToken get(String pKey) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT idplayer FROM shopping_players_tokens WHERE token = ?");
			lStatement.setString(1, pKey);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lIdPlayer = lResultSet.getLong("idplayer");
				return new PlayerToken(pKey, Player.DAO.get(lIdPlayer));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<PlayerToken> getByPlayer(Player pPlayer) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT token FROM shopping_players_tokens WHERE idplayer = ?");
			lStatement.setLong(1, pPlayer.getId());
			ResultSet lResultSet = lDB.query(lStatement);
			List<PlayerToken> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				String lToken = lResultSet.getString("token");
				lResults.add(new PlayerToken(lToken, pPlayer));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<PlayerToken> getAll() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT token, idplayer FROM shopping_players_tokens");
			ResultSet lResultSet = lDB.query(lStatement);
			List<PlayerToken> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				String lToken = lResultSet.getString("token");
				long lIdPlayer = lResultSet.getLong("idplayer");
				lResults.add(new PlayerToken(lToken, Player.DAO.get(lIdPlayer)));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
