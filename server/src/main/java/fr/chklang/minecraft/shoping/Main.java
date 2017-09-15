package fr.chklang.minecraft.shoping;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.events.PlayerEvent;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper.NewShop;
import fr.chklang.minecraft.shoping.model.Shop;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	private Economy econ = null;

	private Thread server;

	public Main() {
	}

	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");

		System.out.println("Load sql");
		DBManager.create(getDataFolder());
		try {
			DBManager.getInstance().start();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error on db initialization", e);
		}

		System.out.println("Load sql OK");

		// Get all Players
		for (OfflinePlayer lPlayer : this.getServer().getOfflinePlayers()) {
			String lUuid = lPlayer.getUniqueId().toString();
			fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid);
			if (lPlayerDB == null) {
				lPlayerDB = new fr.chklang.minecraft.shoping.model.Player(lUuid);
				lPlayerDB.save();
			}
		}
		if (!setupEconomy()) {
			this.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		if (this.server != null) {
			this.server.interrupt();
		}
		this.server = new Thread(() -> {
			WebServer lWebServer = new WebServer();
			try {
				lWebServer.main(getLogger());
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Error on web server start", e);
				e.printStackTrace();
			}
		});
		getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
		this.server.start();
		System.out.println("Load OK");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().log(Level.SEVERE, "Plugin Vault not found");
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			getLogger().log(Level.SEVERE, "Plugin Vault - Economy not found");
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public void onDisable() {
		getLogger().info(
				"onDisable has been invoked!");/*
												 * this.server.interrupt();
												 * this.server = null;
												 * DBManager.getInstance().close
												 * ();
												 */
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		System.out.println("Execute command " + cmd.getName());
		if ("shopping.create".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.create");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				ShopsHelper.newShops.put(lUuid, new NewShop(null));
				sender.sendMessage("Creation shop in progess. Please give base blocks.");
				return true;
			}
			return false;
		} else if ("shopping.update".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.update");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				if (args.length < 1) {
					sender.sendMessage("Usage : /shopping.update [idShop to update]");
					return false;
				}
				Player lPlayer = (Player) sender;
				fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
				long lIdShop = 0;
				try {
					lIdShop = Long.parseLong(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage("The first argument (optional) must be a number!");
					return false;
				}
				Shop lShop = Shop.DAO.get(lIdShop);
				if (lShop == null) {
					sender.sendMessage("Shop " + lIdShop + " doesn't exists!");
					return false;
				}
				if (!lPlayer.isOp() && lShop.owner == null) {
					sender.sendMessage("Only an admin can modify general shops");
					return false;
				} else if (!lPlayer.isOp() && lShop.owner.getId() != lPlayerModel.getId()) {
					sender.sendMessage("It's not your shop!");
					return false;
				}
				ShopsHelper.newShops.put(lUuid, new NewShop(lIdShop));
				sender.sendMessage("Update shop in progess. Please give new base blocks.");
				return true;
			}
			return false;
		} else if ("shopping.setcorner".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.setcorner");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				Player lPlayer = (Player) sender;
				fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
				if (lPlayerModel == null) {
					lPlayerModel = new fr.chklang.minecraft.shoping.model.Player(lUuid.toString());
					lPlayerModel.save();
				}
				Integer lArgument1 = null;
				switch (args.length) {
				case 1:
					try {
						lArgument1 = Integer.parseInt(args[0]);
					} catch (NumberFormatException e) {
						sender.sendMessage("The first argument (optional) must be a number!");
						return false;
					}
					if (lArgument1.intValue() < 1 || lArgument1.intValue() > 3) {
						sender.sendMessage("The first argument (optional) must be a number between 1 and 3.");
						return false;
					}
				}
				NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
				if (lNewShop == null) {
					sender.sendMessage("No shop in creation was found. Please run 'shopping.create' first");
					return false;
				}
				if (lArgument1 != null) {
					switch (lArgument1.intValue()) {
					case 1:
						lNewShop.newShopPosition1 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
						sender.sendMessage("First block is updated.");
						return true;
					case 2:
						lNewShop.newShopPosition2 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
						sender.sendMessage("Second block is updated.");
						return true;
					case 3:
						lNewShop.newShopPosition3 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
						sender.sendMessage("Third block is updated.");
						return true;
					}
					return false;
				}
				if (lNewShop.newShopPosition1 != null) {
					if (lNewShop.newShopPosition2 != null) {
						if (lNewShop.newShopPosition3 != null) {
							sender.sendMessage("All points (3) are defined. To validate it send 'shopping.validate'.");
							return false;
						} else {
							lNewShop.newShopPosition3 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
							sender.sendMessage("Third block has been saved. Type 'shopping.validate' to create the shop.");
							return true;
						}
					} else {
						lNewShop.newShopPosition2 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
						sender.sendMessage("Second block has been saved.");
						return true;
					}
				} else {
					lNewShop.newShopPosition1 = new Position(lPlayer.getLocation().getX(), lPlayer.getLocation().getY(), lPlayer.getLocation().getZ());
					sender.sendMessage("First block has been saved.");
					return true;
				}
			}
		} else if ("shopping.validate".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.validate");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				Player lPlayer = (Player) sender;
				NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
				if (lNewShop == null) {
					sender.sendMessage(
							"Before to call 'shopping.validate' you must call 'shopping.create' then 'shopping.setcorner' three times to define the cube of shop. The first corner isn't defined.");
					return false;
				}
				if (lNewShop.newShopPosition1 == null) {
					sender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The first corner isn't defined.");
					return false;
				}
				if (lNewShop.newShopPosition2 == null) {
					sender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The second corner isn't defined.");
					return false;
				}
				if (lNewShop.newShopPosition3 == null) {
					sender.sendMessage("Before to call 'shopping.validate' you must call 'shopping.setcorner' three times to define the cube of shop. The third corner isn't defined.");
					return false;
				}
				fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
				if (lPlayerModel == null) {
					lPlayerModel = new fr.chklang.minecraft.shoping.model.Player(lUuid.toString());
					lPlayerModel.save();
				}
				Shop lShop = null;
				if (lNewShop.idShop == null) {
					lShop = new Shop();
				} else {
					lShop = Shop.DAO.get(lNewShop.idShop);
					if (lShop == null) {
						sender.sendMessage("The shop #" + lNewShop.idShop + " doesn't exist!");
						return false;
					}
				}
				long lXMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.x)).longValue();
				long lXMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.x)).longValue();
				long lYMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.y)).longValue();
				long lYMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.y)).longValue();
				long lZMin = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.z)).longValue();
				long lZMax = Double.valueOf(Math.ceil(lNewShop.newShopPosition1.z)).longValue();

				long lCurrentValue = 0;
				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.x)).longValue();
				if (lXMin > lCurrentValue) {
					lXMin = lCurrentValue;
				}
				if (lXMax < lCurrentValue) {
					lXMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.y)).longValue();
				if (lYMin > lCurrentValue) {
					lYMin = lCurrentValue;
				}
				if (lYMax < lCurrentValue) {
					lYMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition2.z)).longValue();
				if (lZMin > lCurrentValue) {
					lZMin = lCurrentValue;
				}
				if (lZMax < lCurrentValue) {
					lZMax = lCurrentValue;
				}

				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.x)).longValue();
				if (lXMin > lCurrentValue) {
					lXMin = lCurrentValue;
				}
				if (lXMax < lCurrentValue) {
					lXMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.y)).longValue();
				if (lYMin > lCurrentValue) {
					lYMin = lCurrentValue;
				}
				if (lYMax < lCurrentValue) {
					lYMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lNewShop.newShopPosition3.z)).longValue();
				if (lZMin > lCurrentValue) {
					lZMin = lCurrentValue;
				}
				if (lZMax < lCurrentValue) {
					lZMax = lCurrentValue;
				}

				lShop.setX_min(lXMin);
				lShop.setX_max(lXMax);
				lShop.setY_min(lYMin);
				lShop.setY_max(lYMax);
				lShop.setZ_min(lZMin);
				lShop.setZ_max(lZMax);
				lShop.setOwner(lPlayerModel);
				lShop.save();
				ShopsHelper.newShops.remove(lUuid);
				sender.sendMessage("Shop id #" + lShop.getId() + " was created. You can manage it into the web interface.");
				return true;
			}
		} else if ("shopping.status".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.status");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				Player lPlayer = (Player) sender;
				NewShop lNewShop = ShopsHelper.newShops.get(lUuid);
				if (lNewShop == null) {
					sender.sendMessage("There is no shop in create or update state.");
					return true;
				}
				List<Integer> lPointsDefined = new ArrayList<>();
				if (lNewShop.newShopPosition1 != null) {
					lPointsDefined.add(1);
				}
				if (lNewShop.newShopPosition2 != null) {
					lPointsDefined.add(2);
				}
				if (lNewShop.newShopPosition3 != null) {
					lPointsDefined.add(3);
				}
				String lMessage = null;
				if (lNewShop.idShop != null) {
					lMessage = "The shop #" + lNewShop.idShop + " is in update state";
				} else {
					lMessage = "A shop is in creation state";
				}
				if (lPointsDefined.size() == 0) {
					lMessage += " but no points has been defined.";
				} else if (lPointsDefined.size() == 1) {
					lMessage += " and only the corner number " + lPointsDefined.get(0) + " is defined.";
				} else if (lPointsDefined.size() == 2) {
					lMessage += " and corners number " + lPointsDefined.get(0) + " and " + lPointsDefined.get(1) + " are defined.";
				} else {
					lMessage += " and all corners are defined.";
				}
				sender.sendMessage(lMessage);
				return true;
			}
		} else if ("shopping.delete".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.delete");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				if (args.length < 1) {
					sender.sendMessage("Usage : /shopping.delete [idShop to delete]");
					return false;
				}
				Player lPlayer = (Player) sender;
				fr.chklang.minecraft.shoping.model.Player lPlayerModel = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid.toString());
				long lIdShop = 0;
				try {
					lIdShop = Long.parseLong(args[0]);
				} catch (NumberFormatException e) {
					sender.sendMessage("The first argument (optional) must be a number!");
					return false;
				}
				Shop lShop = Shop.DAO.get(lIdShop);
				if (lShop == null) {
					sender.sendMessage("Shop " + lIdShop + " doesn't exists!");
					return false;
				}
				if (!lPlayer.isOp() && lShop.owner == null) {
					sender.sendMessage("Only an admin can delete general shops");
					return false;
				} else if (!lPlayer.isOp() && lShop.owner.getId() != lPlayerModel.getId()) {
					sender.sendMessage("It's not your shop!");
					return false;
				}
				lShop.delete();
				sender.sendMessage("Shop #"+lIdShop+" has been deleted!");
				return true;
			}
			return false;
		} else if ("shopping.cancel".equalsIgnoreCase(cmd.getName())) {
			System.out.println("Execute shopping.cancel");
			if (sender instanceof Player) {
				UUID lUuid = ((Player) sender).getUniqueId();
				ShopsHelper.newShops.remove(lUuid);
				return true;
			}
			return false;
		} else {
			System.out.println("No command detected!");
		}
		System.out.println("End of commands");
		return false;
	}

	private void createShop(Player pPlayer) {

	}
}
