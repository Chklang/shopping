import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { LoadingService } from '../services/loading/loading.service';
import { CommunicationService } from '../services/communication/communication.service';
import { ShopsService } from '../services/shops/shops.service';
import { LogService } from '../services/log/log.service';
import { PlayersService } from '../services/players/players.service';

import * as model from '../models';
import { Helpers, IDeferred } from '../helpers';

@Component({
  selector: 'app-yoursshopsdetail',
  templateUrl: './yoursshopsdetail.component.html',
  styleUrls: ['./yoursshopsdetail.component.css']
})
export class YoursshopsdetailComponent implements OnInit {

  private idPlayerConnected: number = null;

  public shop: model.IShop = null;
  public items: model.MapArray<IShopItemUpdatable> = new model.MapArray();
  public itsYourShop: boolean = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private loadingService: LoadingService,
    private communicationService: CommunicationService,
    private shopsService: ShopsService,
    private logService: LogService,
    private playersService: PlayersService
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
          if (this.shop.owner.idPlayer !== this.idPlayerConnected && !pPlayerConnected.isOp) {
            console.error('It\'s not your shop!');
            this.itsYourShop = false;
            return;
          }
          this.itsYourShop = true;
          this.communicationService.sendWithResponse('SHOPS_GET_ITEMS', <IShopItemRequest>{
            idShop: this.shop.idShop
          }).then((pResponse: IShopItemResponse) => {
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


                originalNbToBuy: pItem.buy,
                originalNbToSell: pItem.sell,
                originalBasePrice: pItem.price,
                originalMargin: pItem.margin,
                originalIsDefaultPrice: pItem.isDefaultPrice,
                isModified: false
              };
              this.items.addElement(lShopItem.idItem + '_' + lShopItem.subIdItem, lShopItem);
            });
            this.items.sort((a: model.IShopItem, b: model.IShopItem): number => {
              if (a.idItem === b.idItem) {
                return a.subIdItem - b.subIdItem;
              } else {
                return a.idItem - b.idItem;
              }
            });
          }).then(() => {
            this.loadingService.hide();
          });
        });
      });
    });
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

  private listener = () => {

  };
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
}
interface IShopItemUpdatable extends model.IShopItem {
  originalNbToSell: number;
  originalNbToBuy: number;
  originalBasePrice: number;
  originalMargin: number;
  originalIsDefaultPrice: boolean;
  isModified: boolean;
}