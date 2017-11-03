package fr.chklang.minecraft.shoping.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.Wrapper;
import fr.chklang.minecraft.shoping.json.events.ShopItemUpdateEvent;
import fr.chklang.minecraft.shoping.json.events.ShopUpdateEvent;
import fr.chklang.minecraft.shoping.model.Shop;

public class ShopsHelper {

	public static final Map<UUID, NewShop> newShops = new HashMap<>();

	public static void broadcastShopItemUpdateEvent(ShopItemUpdateEvent pEvent) {
		long lIdShop = pEvent.content.idShop;
		LoginHelper.connectedPlayers.values().forEach((pPlayer) -> {
			pPlayer.connexions.forEach((pConnection) -> {
				if (pConnection.getIdShopSubscripted() != null
						&& pConnection.getIdShopSubscripted().longValue() == lIdShop) {
					pConnection.send(pEvent);
				}
			});
		});
	}

	public static void broadcastShopUpdateEvent(ShopUpdateEvent pEvent) {
		long lIdShop = pEvent.content.idShop;
		LoginHelper.connectedPlayers.values().forEach((pPlayer) -> {
			pPlayer.connexions.forEach((pConnection) -> {
				if (pConnection.getIdShopSubscripted() != null
						&& pConnection.getIdShopSubscripted().longValue() == lIdShop) {
					pConnection.send(pEvent);
				}
			});
		});
	}

	public static void showShop(Plugin pPlugin, Shop pShop, World pWorld) {
		double xmin = pShop.getX_min();
		double xmax = pShop.getX_max();
		double ymin = pShop.getY_min();
		double ymax = pShop.getY_max();
		double zmin = pShop.getZ_min();
		double zmax = pShop.getZ_max();
		Wrapper<Integer> lIdTask = new Wrapper<Integer>(null);
		final long lStartTime = System.currentTimeMillis();
		Runnable lRunnable = () -> {
			for (double x = xmin; x <= xmax; x++) {
				for (double y = ymin; y <= ymax; y++) {
					for (double z = zmin; z <= zmax; z++) {
						pWorld.spawnParticle(Particle.FLAME, x - 0.5, y + 0.5, z - 0.5, 1, 0, 0, 0, 0);
					}
				}
			}
			if (System.currentTimeMillis() > (lStartTime + 5_000)) {
				if (lIdTask.e != null) {
					Bukkit.getServer().getScheduler().cancelTask(lIdTask.e);
				}
			}
		};
		lIdTask.e = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(pPlugin, lRunnable, 1, 1);
	}

}
