package fr.chklang.minecraft.shoping.helpers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import fr.chklang.minecraft.shoping.Position;

public class NewShop {
	public final Long idShop;
	private boolean isShown;
	private int idScheduler;
	private final World world;
	private final Plugin plugin;
	private final List<Position> positions = new ArrayList<>();

	private Long xMin;
	private Long xMax;
	private Long yMin;
	private Long yMax;
	private Long zMin;
	private Long zMax;

	public NewShop(Long pIdShop, World pWorld, Plugin pPlugin) {
		super();
		this.idShop = pIdShop;
		this.world = pWorld;
		this.plugin = pPlugin;
		this.isShown = false;
	}

	public void addPosition(Position pPosition) {
		this.positions.add(pPosition);
		this.recalculate();
	}
	
	public boolean setPosition(int pIndex, Position pPosition) {
		if (this.positions.size() < pIndex) {
			return false;
		}
		this.positions.add(pIndex, pPosition);
		return true;
	}
	
	private void recalculate() {
		if (this.positions.isEmpty()) {
			this.xMin = null;
			this.xMax = null;
			this.yMin = null;
			this.yMax = null;
			this.zMin = null;
			this.zMax = null;
		} else {
			this.xMin = Double.valueOf(Math.ceil(this.positions.get(0).x)).longValue();
			this.xMax = Double.valueOf(Math.ceil(this.positions.get(0).x)).longValue();
			this.yMin = Double.valueOf(Math.ceil(this.positions.get(0).y)).longValue();
			this.yMax = Double.valueOf(Math.ceil(this.positions.get(0).y)).longValue();
			this.zMin = Double.valueOf(Math.ceil(this.positions.get(0).z)).longValue();
			this.zMax = Double.valueOf(Math.ceil(this.positions.get(0).z)).longValue();

			for (int i = 1; i < this.positions.size(); i++) {
				long lCurrentValue = 0;
				Position lPosition = this.positions.get(i);
				lCurrentValue = Double.valueOf(Math.ceil(lPosition.x)).longValue();
				if (this.xMin > lCurrentValue) {
					this.xMin = lCurrentValue;
				}
				if (this.xMax < lCurrentValue) {
					this.xMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lPosition.y)).longValue();
				if (this.yMin > lCurrentValue) {
					this.yMin = lCurrentValue;
				}
				if (this.yMax < lCurrentValue) {
					this.yMax = lCurrentValue;
				}
				lCurrentValue = Double.valueOf(Math.ceil(lPosition.z)).longValue();
				if (this.zMin > lCurrentValue) {
					this.zMin = lCurrentValue;
				}
				if (this.zMax < lCurrentValue) {
					this.zMax = lCurrentValue;
				}
			}
		}
	}
	
	public boolean positionIsOk() {
		return !this.positions.isEmpty();
	}
	
	public int getNbPositions() {
		return this.positions.size();
	}

	public int getIdScheduler() {
		return this.idScheduler;
	}

	public Long getxMax() {
		return this.xMax;
	}

	public Long getxMin() {
		return this.xMin;
	}

	public Long getyMax() {
		return this.yMax;
	}

	public Long getyMin() {
		return this.yMin;
	}

	public Long getzMax() {
		return this.zMax;
	}

	public Long getzMin() {
		return this.zMin;
	}

	public void hide() {
		if (this.isShown) {
			Bukkit.getServer().getScheduler().cancelTask(this.idScheduler);
			this.isShown = false;
		}
	}

	public void show() {
		Runnable lRunnable = () -> {
			if (this.xMin == null || this.xMax == null || this.yMin == null || this.yMax == null
					|| this.zMin == null || this.zMax == null) {
				return;
			}
			for (double x = this.xMin.doubleValue(); x <= this.xMax.doubleValue(); x++) {
				for (double y = this.yMin.doubleValue(); y <= this.yMax.doubleValue(); y++) {
					for (double z = this.zMin.doubleValue(); z <= this.zMax.doubleValue(); z++) {
						world.spawnParticle(Particle.FLAME, x - 0.5, y + 0.5, z - 0.5, 1, 0, 0, 0, 0);
					}
				}
			}
		};
		this.idScheduler = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, lRunnable, 0, 5);
		this.isShown = true;
	}
}