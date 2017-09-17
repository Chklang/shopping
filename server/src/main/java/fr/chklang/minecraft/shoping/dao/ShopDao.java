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
			PreparedStatement lStatement = lDB.prepare("SELECT x_min, x_max, y_min, y_max, z_min, z_max, owner, basemargin, space FROM shopping_shops WHERE id = ?");
			lStatement.setLong(1, pKey);
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lX_min = lResultSet.getLong("x_min");
				long lX_max = lResultSet.getLong("x_max");
				long lY_min = lResultSet.getLong("y_min");
				long lY_max = lResultSet.getLong("y_max");
				long lZ_min = lResultSet.getLong("z_min");
				long lZ_max = lResultSet.getLong("z_max");
				Long lIdOwner = lResultSet.getLong("owner");
				double lBaseMargin = lResultSet.getLong("basemargin");
				long lSpace = lResultSet.getLong("space");
				
				Player lOwner = null;
				if (lIdOwner != null) {
					lOwner = Player.DAO.get(lIdOwner);
				}
				return new Shop(pKey, lX_min, lX_max, lY_min, lY_max, lZ_min, lZ_max, lOwner, lBaseMargin, lSpace);
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
			PreparedStatement lStatement = lDB.prepare("SELECT id, x_min, x_max, y_min, y_max, z_min, z_max, basemargin, space FROM shopping_shops WHERE owner = ?");
			if (pPlayer == null) {
				lStatement.setNull(1, Types.INTEGER);
			} else {
				lStatement.setLong(1, pPlayer.getId());
			}
			ResultSet lResultSet = lDB.query(lStatement);
			List<Shop> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lX_min = lResultSet.getLong("x_min");
				long lX_max = lResultSet.getLong("x_max");
				long lY_min = lResultSet.getLong("y_min");
				long lY_max = lResultSet.getLong("y_max");
				long lZ_min = lResultSet.getLong("z_min");
				long lZ_max = lResultSet.getLong("z_max");
				double lBaseMargin = lResultSet.getLong("basemargin");
				long lSpace = lResultSet.getLong("space");
				lResults.add(new Shop(lId, lX_min, lX_max, lY_min, lY_max, lZ_min, lZ_max, pPlayer, lBaseMargin, lSpace));
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
			PreparedStatement lStatement = lDB.prepare("SELECT id, x_min, x_max, y_min, y_max, z_min, z_max, owner, basemargin, space FROM shopping_shops");
			ResultSet lResultSet = lDB.query(lStatement);
			List<Shop> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lId = lResultSet.getLong("id");
				long lX_min = lResultSet.getLong("x_min");
				long lX_max = lResultSet.getLong("x_max");
				long lY_min = lResultSet.getLong("y_min");
				long lY_max = lResultSet.getLong("y_max");
				long lZ_min = lResultSet.getLong("z_min");
				long lZ_max = lResultSet.getLong("z_max");
				Long lIdOwner = lResultSet.getLong("owner");
				double lBaseMargin = lResultSet.getLong("basemargin");
				long lSpace = lResultSet.getLong("space");
				
				Player lOwner = null;
				if (lIdOwner != null) {
					lOwner = Player.DAO.get(lIdOwner);
				}
				lResults.add(new Shop(lId, lX_min, lX_max, lY_min, lY_max, lZ_min, lZ_max, lOwner, lBaseMargin, lSpace));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
