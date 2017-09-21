package fr.chklang.minecraft.shoping.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.helpers.MessagesHelper;
import fr.chklang.minecraft.shoping.json.events.MoneyEvent;
import net.milkbowl.vault.economy.Economy;

public class EconomyEvent {

	public Map<UUID, Double> balances;

	private Plugin plugin;
	private Economy economy;
	private Integer schedulerTaskId;

	public EconomyEvent(Plugin pPlugin, Economy pEconomy) {
		this.plugin = pPlugin;
		this.economy = pEconomy;
	}

	public void start() {
		this.balances = new HashMap<>();
		this.setupOnEconomyChange();
	}

	private void setupOnEconomyChange() {
		this.schedulerTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				// Checking if the players balance is the same as we have, if
				// not continue
				Double lBalance = this.economy.getBalance(p);
				if (lBalance != null) {
					boolean lSendEvent = !this.balances.containsKey(p.getUniqueId());
					if (!lSendEvent) {
						double lOldValue = this.balances.get(p.getUniqueId()).doubleValue();
						double lDiff = lBalance.doubleValue() - lOldValue;
						lSendEvent = Math.abs(lDiff) > 0.001;
					}
					if (lSendEvent) {
						// Do stuff here
						fr.chklang.minecraft.shoping.model.Player lPlayer = fr.chklang.minecraft.shoping.model.Player.DAO.getByUuid(p.getUniqueId().toString());
						MessagesHelper.broadcastEventToAllPlayers(new MoneyEvent(lPlayer.getId(), lBalance), false);
	
						// Once we're done, we have to update the players money
						this.balances.put(p.getUniqueId(), this.economy.getBalance(p));
					}
				}
			}
		}, 20, 20);
	}

	public void stop() {
		if (this.schedulerTaskId != null) {
			Bukkit.getServer().getScheduler().cancelTask(this.schedulerTaskId.intValue());
		}
	}
}
