package fr.chklang.minecraft.shoping.model;

import java.util.Set;
import java.util.UUID;

public class Shop {
	
	public int id;

	public long x1;
	public long y1;
	public long z1;
	
	public long x2;
	public long y2;
	public long z2;
	
	public long x3;
	public long y3;
	public long z3;
	
	public UUID owner;
	
	public Set<ShopItem> items;
}
