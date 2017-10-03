import { Component, OnInit, OnDestroy, TemplateRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { LoadingService } from '../services/loading/loading.service';
import { ShopsService, IShopItemUpdateEventListener, IShopUpdateEventListener, IListenerNearShop } from '../services/shops/shops.service';
import { LogService } from '../services/log/log.service';
import { PlayersService } from '../services/players/players.service';
import { TrService } from '../services/tr/tr.service';
import { InventoryService, IPlayerItemListener } from '../services/inventory/inventory.service';

import * as model from '../models';
import { Helpers, IDeferred } from '../helpers';

@Component({
  selector: 'app-inventory',
  templateUrl: './inventory.component.html',
  styleUrls: ['./inventory.component.css']
})
export class InventoryComponent implements OnInit, OnDestroy {

  public idPlayerConnected: number = null;
  protected items: model.MapArray<IPlayerItemSellable> = new model.MapArray();
  private shopItems: model.IShopItem[] = null;
  private currentNearShop: model.IShop = null;
  private currentIsIntoShop: boolean = false;

  public modalRef: BsModalRef;
  public itemToBuyOrSell: model.IPlayerItem = null;

  //Pagination
  private itemsFiltered: IPlayerItemSellable[] = [];
  public itemsPaging: IPlayerItemSellable[] = [];
  public totalItems: number = null;
  public currentPage: number = 1;
  public filter_name: string = null;

  constructor(
    protected modalService: BsModalService,
    protected loadingService: LoadingService,
    protected shopsService: ShopsService,
    protected logService: LogService,
    protected playersService: PlayersService,
    protected trService: TrService,
    protected inventoryService: InventoryService
  ) {

  }

  public ngOnInit() {
    return this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
      this.idPlayerConnected = pIdPlayer;
      if (this.idPlayerConnected === null) {
        throw new Error('Player not connected');
      }
      this.inventoryService.addListenerInventory(this.listenerInventoryUpdated);
      this.shopsService.addListenerNearShop(this.listenerNearShop);
      this.shopsService.addListenerShopItemUpdate(this.listenerShopItemUpdate);
    });
  }

  public ngOnDestroy() {
    this.inventoryService.removeListenerInventory(this.listenerInventoryUpdated);
    this.shopsService.removeListenerNearShop(this.listenerNearShop);
    this.shopsService.removeListenerShopItemUpdate(this.listenerShopItemUpdate);
  }

  private listenerInventoryUpdated: IPlayerItemListener = (pItems: model.MapArray<model.IPlayerItem>, pItemsUpdated: model.IPlayerItem[]) => {
    pItemsUpdated.forEach((pItemUpdated: model.IPlayerItem) => {
      const lIdItem: string = pItemUpdated.idItem + '_' + pItemUpdated.subIdItem;
      let lItem: IPlayerItemSellable = this.items.getElement(lIdItem);
      if (!lItem && pItemUpdated.quantity > 0) {
        lItem = {
          baseItem: pItemUpdated,
          nbToSell: 0,
          price: null,
          canSell: false,
          nameSimplified: this.simplifyText(pItemUpdated.name)
        }
        this.items.addElement(lIdItem, lItem);
      } else {
        if (pItemUpdated.quantity > 0) {
          lItem.baseItem = pItemUpdated;
        } else {
          this.items.removeElement(lIdItem);
        }
      }
    });
    this.filterRefresh();
  }

  private listenerNearShop: IListenerNearShop = (pNearShop: model.IShopDistance) => {
    if (!this.currentNearShop || this.currentNearShop === pNearShop.shop) {
      this.currentNearShop = pNearShop.shop;
    }
    if (pNearShop.distance === 0 && !this.currentIsIntoShop) {
      this.currentIsIntoShop = true;
      this.updatePrices();
    } else if (pNearShop.distance > 0 && this.currentIsIntoShop) {
      this.currentIsIntoShop = false;
      this.updatePrices();
    }
  }

  private listenerShopItemUpdate: IShopItemUpdateEventListener = (pIdShop: number, pShopItem: model.IShopItem) => {
    if (!this.currentIsIntoShop || !this.currentNearShop || this.currentNearShop.idShop !== pIdShop) {
      // Not current shop
      return;
    }
    const lIdItem: string = pShopItem.idItem + '_' + pShopItem.subIdItem;
    const lItemShop: IPlayerItemSellable = this.items.getElement(lIdItem);
    if (!lItemShop) {
      // Not in inventory
      return;
    }
    if (!this.currentNearShop.owner) {
      // Ignore because it's general shop
      return;
    }
    if (this.currentNearShop.owner.idPlayer === this.idPlayerConnected) {
      // Ignore, because player is the owner
      return;
    }
    if (pShopItem.nbToSell === 0) {
      lItemShop.nbToSell = 0;
      lItemShop.price = null;
      lItemShop.canSell = false;
    } else {
      lItemShop.nbToSell = pShopItem.nbToSell;
      lItemShop.price = pShopItem.priceSell;
      lItemShop.canSell = true;
    }
  }

  private listenerShopUpdate: IShopUpdateEventListener = (pShop: model.IShop): void => {
    this.updatePrices();
  }

  private updatePrices(): void {
    if (!this.items) {
      return;
    }
    Helpers.promiseSelfResolved.then(() => {
      if (this.currentNearShop && !this.shopItems) {
        return this.shopsService.getItems(this.currentNearShop).then((pItems: model.IShopItem[]) => {
          pItems.forEach((pItem: model.IShopItem) => {
            const lIdItem: string = pItem.idItem + '_' + pItem.subIdItem;
            this.currentNearShop.items.addElement(lIdItem, pItem);
          });
        });
      }
    }).then(() => {
      if (this.currentIsIntoShop === false) {
        this.items.forEach((pItem: IPlayerItemSellable) => {
          pItem.nbToSell = null;
          pItem.price = null;
          pItem.canSell = false;
        });
      } else {
        if (!this.currentNearShop.owner) {
          // General shop
          this.items.forEach((pItem: IPlayerItemSellable) => {
            const lIdItem: string = pItem.baseItem.idItem + '_' + pItem.baseItem.subIdItem;
            const lItemShop: model.IShopItem = this.currentNearShop.items.getElement(lIdItem);
            if (!lItemShop) {
              pItem.nbToSell = 0;
              pItem.price = null;
              pItem.canSell = false;
            } else {
              pItem.nbToSell = -1;
              pItem.price = lItemShop.priceSell;
              pItem.canSell = true;
            }
          });
        } else if (this.currentNearShop.owner.idPlayer === this.idPlayerConnected) {
          // Owner is player
          this.items.forEach((pItem: IPlayerItemSellable) => {
            pItem.nbToSell = -1;
            pItem.price = null;
            pItem.canSell = true;
          });
        } else {
          this.items.forEach((pItem: IPlayerItemSellable) => {
            const lIdItem: string = pItem.baseItem.idItem + '_' + pItem.baseItem.subIdItem;
            const lItemShop: model.IShopItem = this.currentNearShop.items.getElement(lIdItem);
            if (!lItemShop) {
              pItem.nbToSell = 0;
              pItem.price = null;
              pItem.canSell = false;
            } else {
              pItem.nbToSell = lItemShop.nbToSell;
              pItem.price = lItemShop.priceSell;
              pItem.canSell = true;
            }
          });
        }
      }
    });
  }

  private simplifyText(pText: string): string {
    return pText.replace(/[éèê]/gi, 'e');
  }

  public filterRefresh(): void {
    if (!this.filter_name) {
      this.itemsFiltered = this.items;
    } else {
      this.itemsFiltered = [];
      const lRegexp = new RegExp(this.simplifyText(this.filter_name), "i");
      this.items.forEach((pItem: IPlayerItemSellable) => {
        if (!lRegexp.test(pItem.nameSimplified)) {
          return;
        }
        this.itemsFiltered.push(pItem);
      });
      this.totalItems = this.itemsFiltered.length;
      this.currentPage = 1;
    }
    this.itemsPaging = this.itemsFiltered.slice((this.currentPage - 1) * 10, this.currentPage * 10);
  }

  public pageChanged(event: any): void {
    this.itemsPaging = this.itemsFiltered.slice((event.page - 1) * event.itemsPerPage, event.page * event.itemsPerPage);
  }

  public give(pItem: model.IPlayerItem, pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
    this.itemToBuyOrSell = pItem;
  }

  public giveAction(pQuantity: number): void {
    this.shopsService.sell(this.currentNearShop.idShop, this.itemToBuyOrSell, pQuantity).then(() => {
      this.modalRef.hide();
    }, () => {
      console.error('Give error!');
    });
  }
}

export interface IPlayerItemSellable {
  baseItem: model.IPlayerItem;
  nameSimplified: string;
  nbToSell: number;
  price: number;
  canSell: boolean;
}