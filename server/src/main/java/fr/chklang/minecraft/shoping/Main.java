package fr.chklang.minecraft.shoping;

import java.util.logging.Level;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import fr.chklang.minecraft.shoping.commands.CancelCommand;
import fr.chklang.minecraft.shoping.commands.CreateCommand;
import fr.chklang.minecraft.shoping.commands.DeleteCommand;
import fr.chklang.minecraft.shoping.commands.SetCornerCommand;
import fr.chklang.minecraft.shoping.commands.ShowCommand;
import fr.chklang.minecraft.shoping.commands.StatusCommand;
import fr.chklang.minecraft.shoping.commands.UpdateCommand;
import fr.chklang.minecraft.shoping.commands.ValidateCommand;
import fr.chklang.minecraft.shoping.db.DBManager;
import fr.chklang.minecraft.shoping.events.EconomyEvent;
import fr.chklang.minecraft.shoping.events.InventoryEvent;
import fr.chklang.minecraft.shoping.events.PlayerEvent;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {

	private Economy economy;
	private EconomyEvent economyEvent = null;
	private InventoryEvent inventoryEvent = null;

	private WebServer server;

	public Main() {
	}

	@Override
	public boolean onCommand(CommandSender pSender, Command pCommand, String pLabel, String[] pArgs) {
		System.out.println("Execute command " + pCommand.getName());
		return false;
	}
	
	@Override
	public void onDisable() {
		getLogger().info("onDisable has been invoked!");
		try {
			this.server.stop();
		} catch (Exception e) {
			//Ignore exception
		}
		getServer().getServicesManager().unregisterAll(this);
		if (this.economyEvent != null) {
			this.economyEvent.stop();
		}
		if (this.inventoryEvent != null) {
			this.inventoryEvent.stop();
		}
	}
	
	@Override
	public void onEnable() {
		try {
			//Setup commands
			this.setupCommands();
			
			//Setup DB
			this.setupDB();
			
			//Save players into DB
			this.setupSavePlayers();
			
			//Setup economy
			this.setupEconomy();
			
			this.setupInventoryEvent();
			
			//Setup web server
			this.setupServer();
			
			//Setup events
			this.setupEvents();
			getLogger().info("Initialization OK!");
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error in Initialization plugin!", e);
			getServer().getPluginManager().disablePlugin(this);
		}
	}
	
	private void setupCommands() {
		getCommand("shopping.create").setExecutor(new CreateCommand(this));
		getCommand("shopping.cancel").setExecutor(new CancelCommand());
		getCommand("shopping.delete").setExecutor(new DeleteCommand());
		getCommand("shopping.setcorner").setExecutor(new SetCornerCommand());
		getCommand("shopping.status").setExecutor(new StatusCommand());
		getCommand("shopping.update").setExecutor(new UpdateCommand(this));
		getCommand("shopping.validate").setExecutor(new ValidateCommand());
		getCommand("shopping.show").setExecutor(new ShowCommand(this));
	}

	private void setupDB() {
		DBManager.create(getDataFolder());
		try {
			DBManager.getInstance().start();
		} catch (Exception e) {
			throw new RuntimeException("Error on db initialization", e);
		}
	}
	
	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			throw new RuntimeException("Plugin Vault not found");
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			throw new RuntimeException("Plugin Vault - Economy not found");
		}
		this.economy = rsp.getProvider();
		
		if (!this.economy.isEnabled()) {
			throw new RuntimeException("Economy plugin not enabled");
		}
		if (!this.economy.hasBankSupport()) {
			throw new RuntimeException("Economy plugin not enabled - No bank support");
		}
		try {
			this.economy.getBanks();
		} catch (NullPointerException e) {
			throw new RuntimeException("Economy plugin not enabled - Did you have setup the plugin?");
		}

		this.economyEvent = new EconomyEvent(this, this.economy);
		this.economyEvent.start();
	}
	
	private void setupInventoryEvent() {
		this.inventoryEvent = new InventoryEvent(this);
		this.inventoryEvent.start();
		getServer().getServicesManager().register(InventoryEvent.class, this.inventoryEvent, this, ServicePriority.Normal);
	}

	private void setupEvents() {
		getServer().getPluginManager().registerEvents(new PlayerEvent(this, this.economy), this);
	}

	private void setupSavePlayers() {
		// Get all Players
		for (OfflinePlayer lPlayer : this.getServer().getOfflinePlayers()) {
			String lUuid = lPlayer.getUniqueId().toString();
			fr.chklang.minecraft.shoping.model.Player lPlayerDB = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(lUuid);
			if (lPlayerDB == null) {
				lPlayerDB = new fr.chklang.minecraft.shoping.model.Player(lUuid);
				lPlayerDB.save();
			}
		}
	}

	private void setupServer() throws Exception {
		if (this.server != null) {
			this.server.stop();
		}
		new Thread(() -> {
			Main.this.server = new WebServer();
			try {
				Main.this.server.main(getLogger());
			} catch (Exception e) {
				getLogger().log(Level.SEVERE, "Error on web server start", e);
				e.printStackTrace();
			}
		}).start();
	}
}
