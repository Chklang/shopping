package fr.chklang.minecraft.shoping.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.model.Player;
import fr.chklang.minecraft.shoping.model.Shop;
import lib.PatPeter.SQLibrary.Database;

public class ShopDao extends AbstractDao<Shop, Long> {

	public ShopDao() {
		super(Shop.class);
	}

	@Override
	public Shop get(Long pKey) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT x1, y1, z1, x2, y2, z2, x3, y3, z3, owner FROM shopping_shops WHERE id = ?");
			lStatement.setLong(1, pKey);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lX1 = lResultSet.getLong("x1");
				long lY1 = lResultSet.getLong("y1");
				long lZ1 = lResultSet.getLong("z1");
				long lX2 = lResultSet.getLong("x2");
				long lY2 = lResultSet.getLong("y2");
				long lZ2 = lResultSet.getLong("z2");
				long lX3 = lResultSet.getLong("x3");
				long lY3 = lResultSet.getLong("y3");
				long lZ3 = lResultSet.getLong("z3");
				Long lIdOwner = lResultSet.getLong("owner");
				
				Player lOwner = null;
				if (lIdOwner != null) {
					lOwner = Player.DAO.get(lIdOwner);
				}
				return new Shop(pKey, lX1, lY1, lZ1, lX2, lY2, lZ2, lX3, lY3, lZ3, lOwner);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Shop> getGlobalsShops() {
		return this.getShopsByOwner(null);
	}
	
	public List<Shop> getShopsByOwner(Player pPlayer) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id, x1, y1, z1, x2, y2, z2, x3, y3, z3 FROM shopping_shops WHERE owner = ?");
			if (pPlayer == null) {
				lStatement.setNull(1, Types.INTEGER);
			} else {
				lStatement.setLong(1, pPlayer.getId());
			}
			ResultSet lResultSet = lDB.query(lStatement);
			List<Shop> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lX1 = lResultSet.getLong("x1");
				long lY1 = lResultSet.getLong("y1");
				long lZ1 = lResultSet.getLong("z1");
				long lX2 = lResultSet.getLong("x2");
				long lY2 = lResultSet.getLong("y2");
				long lZ2 = lResultSet.getLong("z2");
				long lX3 = lResultSet.getLong("x3");
				long lY3 = lResultSet.getLong("y3");
				long lZ3 = lResultSet.getLong("z3");
				lResults.add(new Shop(lId, lX1, lY1, lZ1, lX2, lY2, lZ2, lX3, lY3, lZ3, pPlayer));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Shop> getAll() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT id, x1, y1, z1, x2, y2, z2, x3, y3, z3, owner FROM shopping_shops");
			ResultSet lResultSet = lDB.query(lStatement);
			List<Shop> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lX1 = lResultSet.getLong("x1");
				long lY1 = lResultSet.getLong("y1");
				long lZ1 = lResultSet.getLong("z1");
				long lX2 = lResultSet.getLong("x2");
				long lY2 = lResultSet.getLong("y2");
				long lZ2 = lResultSet.getLong("z2");
				long lX3 = lResultSet.getLong("x3");
				long lY3 = lResultSet.getLong("y3");
				long lZ3 = lResultSet.getLong("z3");
				Long lIdOwner = lResultSet.getLong("owner");
				
				Player lOwner = null;
				if (lIdOwner != null) {
					lOwner = Player.DAO.get(lIdOwner);
				}
				lResults.add(new Shop(lId, lX1, lY1, lZ1, lX2, lY2, lZ2, lX3, lY3, lZ3, lOwner));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
