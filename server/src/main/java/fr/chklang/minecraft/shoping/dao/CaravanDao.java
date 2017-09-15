package fr.chklang.minecraft.shoping.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.model.Caravan;
import fr.chklang.minecraft.shoping.model.DestinationType;
import fr.chklang.minecraft.shoping.model.Player;
import lib.PatPeter.SQLibrary.Database;

public class CaravanDao extends AbstractDao<Caravan, Long> {

	public CaravanDao() {
		super(Caravan.class);
	}

	@Override
	public Caravan get(Long pKey) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT idfrom, idto, fromtype, totype, iditem, subiditem, quantity, startat, arriveat, owner FROM shopping_caravans WHERE id = ?");
			lStatement.setLong(1, pKey);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lIdFrom = lResultSet.getLong("idfrom");
				long lIdTo = lResultSet.getLong("idto");
				int lFromType = lResultSet.getInt("fromtype");
				int lToType = lResultSet.getInt("totype");
				int lIdItem = lResultSet.getInt("iditem");
				int lSubIdItem = lResultSet.getInt("subiditem");
				long lQuantity = lResultSet.getLong("quantity");
				long lStartAt = lResultSet.getLong("startat");
				long lArriveAt = lResultSet.getLong("arriveat");
				Long lOwner = lResultSet.getLong("owner");

				Player lPlayer = null;
				if (lOwner != null) {
					lPlayer = Player.DAO.get(lOwner);
				}
				return new Caravan(pKey, lIdFrom, lIdTo, DestinationType.getByType(lFromType), DestinationType.getByType(lToType), lIdItem, lSubIdItem, lQuantity, lStartAt, lArriveAt, lPlayer);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Caravan> getByPlayer(Player pPlayer) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id, uuid, idfrom, idto, fromtype, totype, iditem, subiditem, quantity, startat, arriveat FROM shopping_caravans WHERE owner = ?");
			if (pPlayer == null) {
				lStatement.setNull(1, Types.INTEGER);
			} else {
				lStatement.setLong(1, pPlayer.getId());
			}
			ResultSet lResultSet = lDB.query(lStatement);
			List<Caravan> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lIdFrom = lResultSet.getLong("idfrom");
				long lIdTo = lResultSet.getLong("idto");
				int lFromType = lResultSet.getInt("fromtype");
				int lToType = lResultSet.getInt("totype");
				int lIdItem = lResultSet.getInt("iditem");
				int lSubIdItem = lResultSet.getInt("subiditem");
				long lQuantity = lResultSet.getLong("quantity");
				long lStartAt = lResultSet.getLong("startat");
				long lArriveAt = lResultSet.getLong("arriveat");

				lResults.add(new Caravan(lId, lIdFrom, lIdTo, DestinationType.getByType(lFromType), DestinationType.getByType(lToType), lIdItem, lSubIdItem, lQuantity, lStartAt, lArriveAt, pPlayer));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Caravan> getAll() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id, uuid, idfrom, idto, fromtype, totype, iditem, subiditem, quantity, startat, arriveat, owner FROM shopping_caravans");
			ResultSet lResultSet = lDB.query(lStatement);
			List<Caravan> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lIdFrom = lResultSet.getLong("idfrom");
				long lIdTo = lResultSet.getLong("idto");
				int lFromType = lResultSet.getInt("fromtype");
				int lToType = lResultSet.getInt("totype");
				int lIdItem = lResultSet.getInt("iditem");
				int lSubIdItem = lResultSet.getInt("subiditem");
				long lQuantity = lResultSet.getLong("quantity");
				long lStartAt = lResultSet.getLong("startat");
				long lArriveAt = lResultSet.getLong("arriveat");
				Long lOwner = lResultSet.getLong("owner");

				Player lPlayer = null;
				if (lOwner != null) {
					lPlayer = Player.DAO.get(lOwner);
				}

				lResults.add(new Caravan(lId, lIdFrom, lIdTo, DestinationType.getByType(lFromType), DestinationType.getByType(lToType), lIdItem, lSubIdItem, lQuantity, lStartAt, lArriveAt, lPlayer));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
