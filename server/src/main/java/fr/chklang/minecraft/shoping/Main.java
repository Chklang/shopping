package fr.chklang.minecraft.shoping;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import fr.chklang.minecraft.shoping.db.DBManager;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	private Economy econ = null;
	
	private Thread server;
	
	public Main() {
		System.out.println("Main created");
	}

	@Override
	public void onEnable() {
		getLogger().info("onEnable has been invoked!");

		System.out.println("Load sql");
		DBManager.create(getDataFolder());

		System.out.println("Load sql OK");
        if (!setupEconomy() ) {
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
		try {
			DBManager.getInstance().start();
		} catch (Exception e) {
			getLogger().log(Level.SEVERE, "Error on db initialization", e);
		}
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
		getLogger().info("onDisable has been invoked!");/*
		this.server.interrupt();
		this.server = null;
		DBManager.getInstance().close();*/
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("testperso")) { // If the player typed /basic then do the following, note: If you only registered this executor for one command, you don't need this
			// doSomething
			getLogger().info("testperso has been invoked by " + sender.getName() + "!");
			if (sender instanceof Player) {
				Player lPlayer = (Player) sender;
				PlayerInventory lInventory = lPlayer.getInventory();
				
				ItemStack lItemStack = new ItemStack(Material.valueOf(args[0]), 10);
				lInventory.addItem(lItemStack);
				Material.values();
			}
			return true;
		} //If this has happened the function will return true. 
	        // If this hasn't happened the value of false will be returned.
		return false; 
	}
}
