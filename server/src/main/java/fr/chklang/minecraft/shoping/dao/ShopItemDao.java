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
import fr.chklang.minecraft.shoping.model.ShopItem;
import fr.chklang.minecraft.shoping.model.ShopItemPk;
import lib.PatPeter.SQLibrary.Database;

public class ShopItemDao extends AbstractDao<ShopItem, ShopItemPk> {

	public ShopItemDao() {
		super(ShopItem.class);
	}

	@Override
	public ShopItem get(ShopItemPk pKey) {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT sell, buy, price, margin, quantity FROM shopping_shops_items WHERE idshop = ? AND iditem = ? AND subiditem = ?");
			lStatement.setLong(1, pKey.getIdShop());
			lStatement.setInt(2, pKey.getIdItem());
			lStatement.setInt(3, pKey.getSubIdItem());
			ResultSet lResultSet = lDB.query(lStatement);
			if (!lResultSet.next()) {
				return null;
			} else {
				long lSell = lResultSet.getLong("sell");
				long lBuy = lResultSet.getLong("buy");
				double lPrice = lResultSet.getDouble("price");
				double lMargin = lResultSet.getDouble("margin");
				long lQuantity = lResultSet.getLong("quantity");
				
				return new ShopItem(Shop.DAO.get(pKey.getIdShop()), pKey.getIdItem(), pKey.getSubIdItem(), lSell, lBuy, lPrice, lMargin, lQuantity);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<ShopItem> getAll() {
		try {
			Database lDB = DBManager.getInstance().getDb();
			PreparedStatement lStatement = lDB.prepare("SELECT idshop, iditem, subiditem, sell, buy, price, margin, quantity FROM shopping_shops_items");
			ResultSet lResultSet = lDB.query(lStatement);
			List<ShopItem> lResults = new ArrayList<>();
			while (lResultSet.next()) {
				long lIdShop = lResultSet.getLong("idshop");
				int lIdItem = lResultSet.getInt("iditem");
				int lSubIdItem = lResultSet.getInt("subiditem");
				long lSell = lResultSet.getLong("sell");
				long lBuy = lResultSet.getLong("buy");
				double lPrice = lResultSet.getDouble("price");
				double lMargin = lResultSet.getDouble("margin");
				long lQuantity = lResultSet.getLong("quantity");
				lResults.add(new ShopItem(Shop.DAO.get(lIdShop), lIdItem, lSubIdItem, lSell, lBuy, lPrice, lMargin, lQuantity));
			}
			return lResults;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
