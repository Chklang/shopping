import { Component, OnInit, OnDestroy, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { LoadingService } from '../services/loading/loading.service';
import { CommunicationService } from '../services/communication/communication.service';
import { ShopsService } from '../services/shops/shops.service';
import { LogService } from '../services/log/log.service';
import { PlayersService } from '../services/players/players.service';
import { TrService } from '../services/tr/tr.service';

import * as model from '../models';
import { Helpers, IDeferred } from '../helpers';

@Component({
  selector: 'app-yoursshopsdetail',
  templateUrl: './yoursshopsdetail.component.html',
  styleUrls: ['./yoursshopsdetail.component.css']
})
export class YoursshopsdetailComponent implements OnInit {

  private idPlayerConnected: number = null;
  private items: model.MapArray<IShopItemUpdatable> = new model.MapArray();

  public shop: model.IShop = null;
  public itsYourShop: boolean = null;

  //Pagination
  private itemsFiltered: IShopItemUpdatable[] = [];
  public itemsPaging: IShopItemUpdatable[] = [];
  public totalItems: number = null;
  public currentPage: number = 1;
  public filter_name: string = null;

  public modalRef: BsModalRef;
  public itemToBuyOrSell: model.IShopItem = null;

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
    this.activatedRoute.params.subscribe((pParams) => {
      let lPromises: Promise<any>[] = [];
      lPromises.push(this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
        this.idPlayerConnected = pIdPlayer;
      }));
      lPromises.push(this.shopsService.getShop(pParams['id']).then((pShop: model.IShop) => {
        this.shop = pShop;
      }));
      Helpers.promisesAll(lPromises).then(() => {
        this.playersService.getPlayer(this.idPlayerConnected).then((pPlayerConnected: model.IPlayer) => {
          if (this.shop.owner === null && !pPlayerConnected.isOp) {
            console.error('You can\'t modify global shops!');
            this.itsYourShop = false;
            return;
          }
          if (this.shop.owner !== null && this.shop.owner.idPlayer !== this.idPlayerConnected && !pPlayerConnected.isOp) {
            console.error('It\'s not your shop!');
            this.itsYourShop = false;
            return;
          }
          this.itsYourShop = true;
          this.communicationService.sendWithResponse('SHOPS_GET_ITEMS', <IShopItemRequest>{
            idShop: this.shop.idShop
          }).then((pResponse: IShopItemResponse) => {
            let lPromises: Promise<void>[] = [];
            pResponse.items.forEach((pItem: IShopItemElementResponse) => {
              let lMargin: number = null;
              if (pItem.margin === null) {
                lMargin = this.shop.baseMargin;
              } else {
                lMargin = pItem.margin;
              }
              let lShopItem: IShopItemUpdatable = {
                idItem: pItem.idItem,
                subIdItem: pItem.subIdItem,
                item: {
                  'EN': 'test'
                },
                nbIntoShop: pItem.quantity,
                nbToBuy: pItem.buy,
                nbToSell: pItem.sell,
                priceBuy: pItem.price * (1 + lMargin),
                priceSell: pItem.price * (1 - lMargin),
                basePrice: pItem.price,
                margin: pItem.margin,
                isDefaultPrice: pItem.isDefaultPrice,

                name: '',
                nameSimplified: '',
                originalNbToBuy: pItem.buy,
                originalNbToSell: pItem.sell,
                originalBasePrice: pItem.price,
                originalMargin: pItem.margin,
                originalIsDefaultPrice: pItem.isDefaultPrice,
                isModified: false
              };
              lPromises.push(this.trService.getText(pItem.name).then((pNameValue: string) => {
                if (pItem.nameDetails) {
                  pNameValue += ' (' + pItem.nameDetails + ')';
                }
                lShopItem.name = pNameValue;
                lShopItem.nameSimplified = this.simplifyText('' + pNameValue);
                this.items.addElement(lShopItem.idItem + '_' + lShopItem.subIdItem, lShopItem);
              }));
            });
            return Helpers.promisesAll(lPromises);
          }).then(() => {
            this.items.sort((a: model.IShopItem, b: model.IShopItem): number => {
              if (a.idItem === b.idItem) {
                return a.subIdItem - b.subIdItem;
              } else {
                return a.idItem - b.idItem;
              }
            });
            this.totalItems = this.items.length;
            this.currentPage = 1;
            this.itemsPaging = this.items.slice(0, 10);
          }).then(() => {
            this.loadingService.hide();
          });
        });
      });
    });
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
    if (pItem.margin !== null && (<any>pItem.margin) === "") {
      pItem.margin = null;
    }
    pItem.nbToBuy = this.testNaN(pItem.nbToBuy);
    pItem.nbToSell = this.testNaN(pItem.nbToSell);
    pItem.basePrice = this.testNaN(pItem.basePrice);
    pItem.isModified = false;
    pItem.isModified = pItem.isModified || Number(pItem.basePrice) !== pItem.originalBasePrice;
    pItem.isModified = pItem.isModified || pItem.nbToSell !== pItem.originalNbToSell;
    pItem.isModified = pItem.isModified || pItem.nbToBuy !== pItem.originalNbToBuy;
    pItem.isModified = pItem.isModified || pItem.margin !== pItem.originalMargin;
    pItem.isModified = pItem.isModified || pItem.isDefaultPrice !== pItem.originalIsDefaultPrice;
  }

  public cancel(pItem: IShopItemUpdatable): void {
    pItem.nbToBuy = pItem.originalNbToBuy;
    pItem.nbToSell = pItem.originalNbToSell;
    pItem.margin = pItem.originalMargin;
    pItem.basePrice = pItem.originalBasePrice;
    pItem.isDefaultPrice = pItem.originalIsDefaultPrice;
    pItem.isModified = false;
  }

  public save(pItem: IShopItemUpdatable): void {
    console.log('Item to save :', pItem);
    this.shopsService.setItem(this.shop, pItem).then((pIsOK: boolean) => {
      if (pIsOK) {
        pItem.originalNbToBuy = pItem.nbToBuy;
        pItem.originalNbToSell = pItem.nbToSell;
        pItem.originalMargin = pItem.margin;
        pItem.originalBasePrice = pItem.basePrice;
        pItem.originalIsDefaultPrice = pItem.isDefaultPrice;
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
        if (pItem.margin === null) {
          lMargin = Number(this.shop.baseMargin);
        } else {
          lMargin = pItem.margin;
        }
        pItem.priceBuy = pItem.basePrice * (1 + lMargin);
        pItem.priceSell = pItem.basePrice * (1 - lMargin);
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
}

interface IShopItemRequest {
  idShop: number;
}
interface IShopItemResponse {
  items: IShopItemElementResponse[];
}
interface IShopItemElementResponse {
  idItem: number;
  subIdItem: number;
  sell: number;
  buy: number;
  price?: number;
  isDefaultPrice?: boolean;
  margin?: number;
  quantity: number;
  name: string;
  nameDetails: string;
}
interface IShopItemUpdatable extends model.IShopItem {
  name: string;
  nameSimplified: string;
  originalNbToSell: number;
  originalNbToBuy: number;
  originalBasePrice: number;
  originalMargin: number;
  originalIsDefaultPrice: boolean;
  isModified: boolean;
}