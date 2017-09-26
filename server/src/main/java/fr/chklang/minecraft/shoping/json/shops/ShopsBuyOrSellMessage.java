package fr.chklang.minecraft.shoping.json.shops;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import fr.chklang.minecraft.shoping.helpers.BlocksHelper;
import fr.chklang.minecraft.shoping.helpers.BlocksHelper.Element;
import fr.chklang.minecraft.shoping.helpers.LoginHelper.PlayerConnected;
import fr.chklang.minecraft.shoping.helpers.ShopsHelper;
import fr.chklang.minecraft.shoping.json.AbstractMessage;
import fr.chklang.minecraft.shoping.json.AbstractResponse;
import fr.chklang.minecraft.shoping.json.events.ShopItemUpdateEvent;
import fr.chklang.minecraft.shoping.model.Shop;
import fr.chklang.minecraft.shoping.model.ShopItem;
import fr.chklang.minecraft.shoping.model.ShopItemPk;
import fr.chklang.minecraft.shoping.servlets.IConnexion;
import net.milkbowl.vault.economy.Economy;

public class ShopsBuyOrSellMessage extends AbstractMessage<ShopsBuyOrSellContent> {

	@Override
	public void execute(IConnexion pConnexion) {
		PlayerConnected lPlayer = pConnexion.getPlayer();
		if (lPlayer == null) {
			System.err.println("Player not connected");
			pConnexion.send(new Response(this, false));
			return;
		}
		Shop lShop = Shop.DAO.get(this.content.idShop);
		if (lShop == null) {
			System.err.println("Shop not found");
			pConnexion.send(new Response(this, false));
			return;
		}
		double lPlayerX = lPlayer.player.getLocation().getX();
		double lPlayerY = lPlayer.player.getLocation().getY();
		double lPlayerZ = lPlayer.player.getLocation().getZ();
		if (lShop.getX_min() > lPlayerX || lShop.getX_max() < lPlayerX || lShop.getY_min() > lPlayerY || lShop.getY_max() < lPlayerY || lShop.getZ_min() > lPlayerZ || lShop.getZ_max() < lPlayerZ) {
			System.err.println("You are not into the shop");
			pConnexion.send(new Response(this, false));
			return;
		}
		ItemStack lItemStack = null;
		if (this.content.subIdItem == 0) {
			lItemStack = new ItemStack(Material.getMaterial(this.content.idItem), this.content.quantity, this.content.subIdItem);
		} else {
			lItemStack = new ItemStack(Material.getMaterial(this.content.idItem), this.content.quantity, this.content.subIdItem);
		}
		ShopItemPk lShopItemPk = new ShopItemPk(this.content.idShop, this.content.idItem, this.content.subIdItem);
		ShopItem lShopItem = ShopItem.DAO.get(lShopItemPk);
		if (lShop.owner != null) {
			if (lShopItem == null) {
				if (lShop.owner.getId() == lPlayer.idUser) {
					lShopItem = new ShopItem();
					lShopItem.setShop(lShop);
					lShopItem.setIdItem(this.content.idItem);
					lShopItem.setSubIdItem(this.content.subIdItem);
					lShopItem.setMargin(null);
					lShopItem.setPrice(null);
					lShopItem.setSell(0);
					lShopItem.setBuy(0);
					lShopItem.setQuantity(0);
				} else {
					System.err.println("Item not found into shop");
					pConnexion.send(new Response(this, false));
					return;
				}
			}
		}
		if (this.content.actionType == ShopsBuyOrSellContent.ActionType.BUY.value) {
			// Player buy
			OfflinePlayer lOwner = null;
			if (lShop.owner != null) {
				if (lShopItem.getQuantity() < this.content.quantity) {
					System.err.println("Shop hasn't the good quantity to sell");
					pConnexion.send(new Response(this, false));
					return;
				}
				if (lShopItem.getSell() > 0 && lShopItem.getSell() < this.content.quantity) {
					System.err.println("Shop don't sell this quantity");
					pConnexion.send(new Response(this, false));
					return;
				}

				lOwner = Bukkit.getOfflinePlayer(UUID.fromString(lShop.getOwner().getUuid()));
			}
			double lPrice = 0;
			Element lElement = BlocksHelper.getElement(this.content.idItem, Short.valueOf(this.content.subIdItem));
			System.out.println("Element found : " + lElement);
			if (lShop.owner != null && lShop.owner.getId() == lPlayer.idUser) {
				// It's the owner
				lPrice = 0;
			} else if (lShop.owner == null || lShopItem == null || lShopItem.getPrice() == null) {
				lPrice = lElement.price * this.content.quantity;
			} else {
				lPrice = lShopItem.getPrice() * this.content.quantity;
			}
			if (lShopItem == null || lShopItem.getMargin() == null) {
				lPrice *= (1 + lShop.getBaseMargin());
			} else {
				lPrice *= (1 + lShopItem.getMargin());
			}
			Economy lEconomy = this.getEconomy();
			double lBalance = lEconomy.getBalance(lPlayer.player);
			if (lBalance < lPrice) {
				System.err.println("You don't have sufficient money");
				pConnexion.send(new Response(this, false));
				return;
			}
			lPlayer.player.getInventory().addItem(lItemStack);
			lEconomy.withdrawPlayer(lPlayer.player, lPrice);

			if (lOwner != null) {
				lEconomy.depositPlayer(lOwner, lPrice);
				lShopItem.setQuantity(lShopItem.getQuantity() - this.content.quantity);
				lShopItem.save();
				ShopsHelper.broadcastShopItemUpdateEvent(new ShopItemUpdateEvent(lShopItem));
			}
			pConnexion.send(new Response(this, true));
			return;
		} else {
			// Player sell
			OfflinePlayer lOwner = null;
			boolean lMaterialFound = lPlayer.player.getInventory().containsAtLeast(lItemStack, this.content.quantity);
			if (!lMaterialFound) {
				System.err.println("Player hasn't this material");
				pConnexion.send(new Response(this, false));
				return;
			}
			if (lShop.owner != null) {
				if (lShopItem.getBuy() > 0 && lShopItem.getBuy() < this.content.quantity) {
					System.err.println("Shop don't buy this quantity");
					pConnexion.send(new Response(this, false));
					return;
				}

				lOwner = Bukkit.getOfflinePlayer(UUID.fromString(lShop.getOwner().getUuid()));
			}

			double lPrice = 0;
			Element lElement = BlocksHelper.getElement(this.content.idItem, Short.valueOf(this.content.subIdItem));
			if (lShop.owner != null && lShop.owner.getId() == lPlayer.idUser) {
				// It's the owner
				lPrice = 0;
			} else if (lShop.owner == null || lShopItem == null || lShopItem.getPrice() == null) {
				lPrice = lElement.price * this.content.quantity;
			} else {
				lPrice = lShopItem.getPrice() * this.content.quantity;
			}
			if (lShopItem == null || lShopItem.getMargin() == null) {
				lPrice *= (1 + lShop.getBaseMargin());
			} else {
				lPrice *= (1 + lShopItem.getMargin());
			}

			Economy lEconomy = this.getEconomy();
			if (lOwner != null) {
				double lBalancerOwner = lEconomy.getBalance(lOwner);
				if (lBalancerOwner < lPrice) {
					System.err.println("Shop owner hasn't sufficient money");
					pConnexion.send(new Response(this, false));
					return;
				}
				lEconomy.withdrawPlayer(lOwner, lPrice);
			}
			lEconomy.depositPlayer(lPlayer.player, lPrice);
			lPlayer.player.getInventory().removeItem(lItemStack);

			if (lOwner != null) {
				lEconomy.depositPlayer(lOwner, lPrice);
				lShopItem.setQuantity(lShopItem.getQuantity() + this.content.quantity);
				lShopItem.save();
				ShopsHelper.broadcastShopItemUpdateEvent(new ShopItemUpdateEvent(lShopItem));
			}
			pConnexion.send(new Response(this, true));
			return;
		}
	}

	public static class Response extends AbstractResponse<ResponseContent> {

		public Response(AbstractMessage<?> pOrigin, boolean pIsOk) {
			super(pOrigin);
			this.content = new ResponseContent(pIsOk);
		}
	}

	public static class ResponseContent {
		public final boolean isOk;

		public ResponseContent(boolean pIsOk) {
			super();
			this.isOk = pIsOk;
		}
	}

}
