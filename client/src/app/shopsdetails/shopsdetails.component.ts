import { Component, OnInit, OnDestroy, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { LoadingService } from '../services/loading/loading.service';
import { CommunicationService } from '../services/communication/communication.service';
import { ShopsService, IShopItemUpdateEventListener } from '../services/shops/shops.service';
import { LogService } from '../services/log/log.service';
import { PlayersService } from '../services/players/players.service';
import { TrService } from '../services/tr/tr.service';

import * as model from '../models';
import { Helpers, IDeferred } from '../helpers';

@Component({
  selector: 'app-shopsdetails',
  templateUrl: './shopsdetails.component.html',
  styleUrls: ['./shopsdetails.component.css']
})
export class ShopsdetailsComponent implements OnInit, OnDestroy {

  public idPlayerConnected: number = null;
  private items: model.MapArray<IShopItemUpdatable> = new model.MapArray();

  public shop: model.IShop = null;
  public itsYourShop: boolean = null;
  public mode: string = 'view';

  //Pagination
  private itemsFiltered: IShopItemUpdatable[] = [];
  public itemsPaging: IShopItemUpdatable[] = [];
  public totalItems: number = null;
  public currentPage: number = 1;
  public filter_name: string = null;

  public modalRef: BsModalRef;
  public itemToBuyOrSell: model.IShopItem = null;

  public spaceOccuped: number = 0;
  public percentOccuped: number = 0;
  public addspace_newspace: number = 0;
  public addspace_newpercent: number = 0;
  public addspace_price: number = 0;
  public addspace_value: string = '0';

  constructor(
    private activatedRoute: ActivatedRoute,
    private modalService: BsModalService,
    private loadingService: LoadingService,
    private communicationService: CommunicationService,
    private shopsService: ShopsService,
    private logService: LogService,
    private playersService: PlayersService,
    private trService: TrService
  ) {

  }

  ngOnInit() {
    this.loadingService.show();
    this.activatedRoute.params.subscribe((pParams: IParamsOpenView) => {
      switch (pParams.mode) {
        case 'view':
          this.mode = 'view';
          break;
        case 'modify':
          this.mode = 'update';
          break;
        default:
          this.mode = null;
          break;
      }
      let lPromises: Promise<any>[] = [];
      lPromises.push(this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
        this.idPlayerConnected = pIdPlayer;
        if (this.mode === 'update' && this.idPlayerConnected === null) {
          throw new Error('Player not connected');
        }
      }));
      lPromises.push(this.shopsService.getShop(pParams.idShop).then((pShop: model.IShop) => {
        this.shop = pShop;
      }));
      Helpers.promisesAll(lPromises).then(() => {
        this.shopsService.subscribeShopEvent(this.shop);
        this.shopsService.addListenerShopItemUpdate(this.listenerShopItemUpdateEvent);
        return this.playersService.getPlayer(this.idPlayerConnected);
      }).then((pPlayerConnected: model.IPlayer) => {
        if (this.shop.owner === null && pPlayerConnected && pPlayerConnected.isOp) {
          this.itsYourShop = true;
        } else if (this.shop.owner !== null && this.shop.owner.idPlayer === this.idPlayerConnected) {
          this.itsYourShop = true;
        } else {
          this.itsYourShop = false;
        }
        return this.shopsService.getItems(this.shop);
      }).then((pItems: model.IShopItem[]) => {
        let lMargin: number = null;
        pItems.forEach((pItem: model.IShopItem) => {
          if (pItem.margin === null) {
            lMargin = this.shop.baseMargin;
          } else {
            lMargin = pItem.margin;
          }
          let lShopItem: IShopItemUpdatable = {
            baseItem: pItem,

            realmargin: pItem.isDefaultMargin ? null : pItem.margin,
            name: pItem.name,
            nameSimplified: '',
            originalNbToBuy: pItem.nbToBuy,
            originalNbToSell: pItem.nbToSell,
            originalBasePrice: pItem.basePrice,
            originalMargin: null,
            originalIsDefaultPrice: pItem.isDefaultPrice,
            isModified: false
          };
          lShopItem.originalMargin = lShopItem.realmargin;
          lShopItem.nameSimplified = this.simplifyText('' + pItem.name);
          this.items.addElement(pItem.idItem + '_' + pItem.subIdItem, lShopItem);
        });
        this.calculateShopSpace();
        this.items.sort((a: IShopItemUpdatable, b: IShopItemUpdatable): number => {
          if (a.baseItem.idItem === b.baseItem.idItem) {
            return a.baseItem.subIdItem - b.baseItem.subIdItem;
          } else {
            return a.baseItem.idItem - b.baseItem.idItem;
          }
        });
        this.totalItems = this.items.length;
        this.currentPage = 1;
        this.itemsPaging = this.items.slice(0, 10);
      }).then(() => {
        this.loadingService.hide();
      }).catch((e) => {
        console.error(e);
        this.loadingService.hide();
      });
    });
  }

  ngOnDestroy() {
    this.shopsService.removeListenerShopItemUpdate(this.listenerShopItemUpdateEvent);
  }

  private listenerShopItemUpdateEvent: IShopItemUpdateEventListener = (pShopItem: model.IShopItem) => {
    let lShopItemStored: IShopItemUpdatable = this.items.getElement(pShopItem.idItem + '_' + pShopItem.subIdItem);
    if (!lShopItemStored) {
      //Items not loaded, ignore event
      return;
    }
    let lMargin: number = null;
    if (pShopItem.margin === null) {
      lMargin = this.shop.baseMargin;
    } else {
      lMargin = pShopItem.margin;
    }
    if (lShopItemStored.originalBasePrice === lShopItemStored.baseItem.basePrice) {
      lShopItemStored.baseItem.basePrice = pShopItem.basePrice;
    }
    if (lShopItemStored.originalIsDefaultPrice === lShopItemStored.baseItem.isDefaultPrice) {
      lShopItemStored.baseItem.isDefaultPrice = pShopItem.isDefaultPrice;
    }
    if (lShopItemStored.originalMargin === lShopItemStored.realmargin) {
      lShopItemStored.baseItem.margin = pShopItem.margin;
      lShopItemStored.realmargin = pShopItem.margin;
    }
    if (lShopItemStored.baseItem.nbIntoShop !== pShopItem.nbIntoShop) {
      lShopItemStored.baseItem.nbIntoShop = pShopItem.nbIntoShop;
      this.calculateShopSpace();
    }
    if (lShopItemStored.originalNbToBuy === lShopItemStored.baseItem.nbToBuy) {
      lShopItemStored.baseItem.nbToBuy = pShopItem.nbToBuy;
    }
    if (lShopItemStored.originalNbToSell === lShopItemStored.baseItem.nbToSell) {
      lShopItemStored.baseItem.nbToSell = pShopItem.nbToSell;
    }
    lShopItemStored.baseItem.priceBuy = pShopItem.basePrice * (1 + lMargin);
    lShopItemStored.baseItem.priceSell = pShopItem.basePrice * (1 - lMargin);

  }

  private calculateShopSpace(): void {
    if (!this.shop) {
      return;
    }
    let lSpaceOccuped: number = 0;
    this.items.forEach((pItem: IShopItemUpdatable) => {
      lSpaceOccuped += pItem.baseItem.nbIntoShop;
    });
    this.spaceOccuped = lSpaceOccuped;
    this.percentOccuped = Math.round((100 / this.shop.space) * this.spaceOccuped * 100) / 100;
  }

  private simplifyText(pText: string): string {
    return pText.replace(/[éèê]/gi, 'e');
  }

  public filterRefresh(): void {
    if (!this.filter_name) {
      this.itemsFiltered = this.items;
      this.itemsPaging = this.items.slice((this.currentPage - 1) * 10, this.currentPage * 10);
      return;
    }
    this.itemsFiltered = [];
    const lRegexp = new RegExp(this.simplifyText(this.filter_name), "i");
    this.items.forEach((pItem: IShopItemUpdatable) => {
      if (!lRegexp.test(pItem.nameSimplified)) {
        return;
      }
      this.itemsFiltered.push(pItem);
    });
    this.totalItems = this.itemsFiltered.length;
    this.currentPage = 1;
    this.itemsPaging = this.itemsFiltered.slice((this.currentPage - 1) * 10, this.currentPage * 10);
  }

  public checkItemModifications(pItem: IShopItemUpdatable): void {
    if (pItem.baseItem.margin !== null && (<any>pItem.baseItem.margin) === "") {
      pItem.baseItem.margin = null;
    }
    pItem.baseItem.nbToBuy = this.testNaN(pItem.baseItem.nbToBuy);
    pItem.baseItem.nbToSell = this.testNaN(pItem.baseItem.nbToSell);
    pItem.baseItem.basePrice = this.testNaN(pItem.baseItem.basePrice);
    pItem.isModified = false;
    pItem.isModified = pItem.isModified || Number(pItem.baseItem.basePrice) !== pItem.originalBasePrice;
    pItem.isModified = pItem.isModified || pItem.baseItem.nbToSell !== pItem.originalNbToSell;
    pItem.isModified = pItem.isModified || pItem.baseItem.nbToBuy !== pItem.originalNbToBuy;
    pItem.isModified = pItem.isModified || pItem.baseItem.margin !== pItem.originalMargin;
    pItem.isModified = pItem.isModified || pItem.baseItem.isDefaultPrice !== pItem.originalIsDefaultPrice;
  }

  public cancel(pItem: IShopItemUpdatable): void {
    pItem.baseItem.nbToBuy = pItem.originalNbToBuy;
    pItem.baseItem.nbToSell = pItem.originalNbToSell;
    pItem.baseItem.margin = pItem.originalMargin;
    pItem.baseItem.basePrice = pItem.originalBasePrice;
    pItem.baseItem.isDefaultPrice = pItem.originalIsDefaultPrice;
    pItem.isModified = false;
  }

  public save(pItem: IShopItemUpdatable): void {
    this.shopsService.setItem(this.shop, pItem.baseItem).then((pIsOK: boolean) => {
      if (pIsOK) {
        pItem.originalNbToBuy = pItem.baseItem.nbToBuy;
        pItem.originalNbToSell = pItem.baseItem.nbToSell;
        pItem.originalMargin = pItem.baseItem.margin;
        pItem.originalBasePrice = pItem.baseItem.basePrice;
        pItem.originalIsDefaultPrice = pItem.baseItem.isDefaultPrice;
        pItem.isModified = false;
        console.log('Update OK');
      } else {
        console.log('Update NOK');
      }
    }, console.error);
  }

  private testNaN(pValue: any): any {
    let lValue = Number(pValue);
    if (isNaN(lValue)) {
      return pValue;
    } else {
      return lValue;
    }
  }

  public validateSetProperties(): void {
    this.loadingService.show();
    this.shopsService.setProperties(this.shop).then(() => {
      //Update datas
      this.items.forEach((pItem) => {
        let lMargin: number = null;
        if (pItem.baseItem.margin === null) {
          lMargin = Number(this.shop.baseMargin);
        } else {
          lMargin = pItem.baseItem.margin;
        }
        pItem.baseItem.priceBuy = pItem.baseItem.basePrice * (1 + lMargin);
        pItem.baseItem.priceSell = pItem.baseItem.basePrice * (1 - lMargin);
      });
    }).then(() => {
      this.loadingService.hide();
    });
  }

  public pageChanged(event: any): void {
    this.itemsPaging = this.itemsFiltered.slice((event.page - 1) * event.itemsPerPage, event.page * event.itemsPerPage);
  }

  public take(pItem: model.IShopItem, pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
    this.itemToBuyOrSell = pItem;
  }

  public takeAction(pQuantity: number): void {
    this.shopsService.buy(this.shop.idShop, this.itemToBuyOrSell, pQuantity).then(() => {
      this.modalRef.hide();
    }, () => {
      console.error('Take error!');
    });
  }

  public give(pItem: model.IShopItem, pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
    this.itemToBuyOrSell = pItem;
  }

  public giveAction(pQuantity: number): void {
    this.shopsService.sell(this.shop.idShop, this.itemToBuyOrSell, pQuantity).then(() => {
      this.modalRef.hide();
    }, () => {
      console.error('Give error!');
    });
  }

  public buySpace(pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
    this.addspace_value = "0";
    this.addSpaceUpdate();
  }

  public addSpaceUpdate(): void {
    let lValue: number = Number(this.addspace_value);
    if (isNaN(lValue)) {
      lValue = 0;
    }
    lValue = Math.ceil(lValue);
    if (lValue < 0) {
      lValue = 0;
    }
    this.addspace_newspace = this.shop.space + lValue;
    this.addspace_newpercent = Math.round((100 / this.addspace_newspace) * this.spaceOccuped * 100) / 100;
    this.addspace_price = lValue * 0.1;
  }

  public buySpaceAction(pValue: string): void {
    let lValue: number = Number(pValue);
    if (isNaN(lValue)) {
      console.error("Must be an integer");
      return;
    }
    lValue = Math.ceil(lValue);
    if (lValue < 0) {
      lValue = 0;
    }
    this.shopsService.buySpace(this.shop, lValue).then((pIsOk: boolean) => {
      if (!pIsOk) {
        throw new Error('Can\'t add this space');
      }
      this.modalRef.hide();
    }).catch((pError: Error) => {
      console.error(pError);
    });
  }

  public addSpace_less(): void {
    let lCurrentValue: number = Number(this.addspace_value);
    if (isNaN(lCurrentValue)) {
      lCurrentValue = 0;
    }
    lCurrentValue = Math.ceil(lCurrentValue);
    lCurrentValue -= 50;
    if (lCurrentValue < 0) {
      lCurrentValue = 0;
    }
    this.addspace_value = lCurrentValue.toString();
    this.addSpaceUpdate();
  }

  public addSpace_more(): void {
    let lCurrentValue: number = Number(this.addspace_value);
    if (isNaN(lCurrentValue)) {
      lCurrentValue = 0;
    }
    if (lCurrentValue < 0) {
      lCurrentValue = 0;
    }
    lCurrentValue = Math.ceil(lCurrentValue);
    lCurrentValue += 50;
    this.addspace_value = lCurrentValue.toString();
    this.addSpaceUpdate();
  }
}
interface IShopItemUpdatable {
  baseItem: model.IShopItem;
  realmargin: number;
  name: string;
  nameSimplified: string;
  originalNbToSell: number;
  originalNbToBuy: number;
  originalBasePrice: number;
  originalMargin: number;
  originalIsDefaultPrice: boolean;
  isModified: boolean;
}
interface IParamsOpenView {
  idShop: number;
  mode: string;
}