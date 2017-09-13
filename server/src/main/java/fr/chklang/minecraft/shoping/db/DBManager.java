package fr.chklang.minecraft.shoping.db;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;

public class DBManager {
	private static Logger logger = Logger.getLogger(DBManager.class.getCanonicalName());
	private static DBManager INSTANCE = null;

	private File folder;
	private Database db;
	
	public static void create(File pFolder) {
		INSTANCE = new DBManager(pFolder);
	}
	
	public static DBManager getInstance() {
		return INSTANCE;
	}

	private DBManager(File pFolder) {
		this.folder = pFolder;
	}

	public void start() {
		int lVersion = 0;
		this.db = new SQLite(logger, "db", this.folder.getAbsolutePath(), "Shopping", ".sqlite");
		if (!this.db.open()) {
			logger.log(Level.SEVERE, "Cannot open database!");
		}
		try {
			PreparedStatement lGetVersion = this.db.prepare("SELECT version FROM shopping_versions WHERE name=?");
			lGetVersion.setString(1, "global");
			ResultSet lResult = this.db.query(lGetVersion);
			if (lResult.next()) {
				lVersion = lResult.getInt("version");
			}
		} catch (SQLException e) {
			//Ignore
		}
		try {
			this.update(lVersion);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Cannot update database", e);
		}
	}
	
	public boolean isShopExists(int pIdShop) {
		ResultSet lResult = null;
		try {
		PreparedStatement lStatement = this.db.prepare("SELECT * FROM shopping_shops WHERE id=?");
		lStatement.setInt(1, pIdShop);
		lResult = this.db.query(lStatement);
		return lResult.next();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (lResult != null) {
				try {
					lResult.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void update(int pCurrentVersion) throws SQLException {
		switch (pCurrentVersion) {
		case 0:{
			//Create db
			this.db.query("CREATE TABLE shopping_versions ("
					+ "name TEXT, "
					+ "version INTEGER, "
					+ "PRIMARY KEY(name)"
					+ ")");
			PreparedStatement lStatement = this.db.prepare("INSERT INTO shopping_versions (name, version) VALUES (?, ?)");
			lStatement.setString(1, "global");
			lStatement.setInt(2, 1);
			this.db.query(lStatement);
			this.db.query("CREATE TABLE shopping_players ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "player TEXT"
					+ ")");
			this.db.query("CREATE TABLE shopping_shops ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "x_min INTEGER NOT NULL, "
					+ "x_max INTEGER NOT NULL, "
					+ "y_min INTEGER NOT NULL, "
					+ "y_max INTEGER NOT NULL, "
					+ "z_min INTEGER NOT NULL, "
					+ "z_max INTEGER NOT NULL, "
					+ "owner TEXT"
					+ ")");
			this.db.query("CREATE TABLE shopping_shops_items ("
					+ "idshop INTEGER, "
					+ "iditem INTEGER, "
					+ "sell INTEGER, "
					+ "buy INTEGER, "
					+ "price REAL, "
					+ "margin REAL NOT NULL, "
					+ "quantity INTEGER NOT NULL, "
					+ "PRIMARY KEY(idshop, iditem)"
					+ ")");
			this.db.query("CREATE TABLE shopping_transport ("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ "idfrom INTEGER, "
					+ "idto INTEGER, "
					+ "fromtype INTEGER, "
					+ "totype INTEGER, "
					+ "iditem INTEGER, "
					+ "quantity INTEGER NOT NULL, "
					+ "startat INTEGER, "
					+ "arriveat NUMBER"
					+ ")");
		}
		}
	}

	public void close() {
		this.db.close();
	}
}
