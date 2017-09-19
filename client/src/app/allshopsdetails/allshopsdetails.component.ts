import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { LoadingService } from '../services/loading/loading.service';
import { CommunicationService } from '../services/communication/communication.service';
import { ShopsService } from '../services/shops/shops.service';

import * as model from '../models';

@Component({
  selector: 'app-allshopsdetails',
  templateUrl: './allshopsdetails.component.html',
  styleUrls: ['./allshopsdetails.component.css']
})
export class AllshopsdetailsComponent implements OnInit {

  public shop: model.IShop = null;
  public items: model.MapArray<model.IShopItem> = new model.MapArray();

  constructor(
    private activatedRoute: ActivatedRoute,
    private loadingService: LoadingService,
    private communicationService: CommunicationService,
    private shopsService: ShopsService
  ) {

  }

  ngOnInit() {
    this.loadingService.show();
    this.activatedRoute.params.subscribe((pParams) => {
      this.shopsService.getShop(pParams['id']).then((pShop: model.IShop) => {
        this.shop = pShop;
        this.communicationService.sendWithResponse('SHOPS_GET_ITEMS', <IShopItemRequest>{
          idShop: pShop.idShop
        }).then((pResponse: IShopItemResponse) => {
          pResponse.items.forEach((pItem: IShopItemElementResponse) => {
            let lMargin: number = null;
            if (pItem.margin === null) {
              lMargin = pShop.baseMargin;
            } else {
              lMargin = pItem.margin;
            }
            let lShopItem: model.IShopItem = {
              idItem: pItem.idItem,
              subIdItem: pItem.subIdItem,
              item: {
                'EN': 'test'
              },
              nbIntoShop: pItem.quantity,
              nbToBuy: pItem.sell,
              nbToSell: pItem.buy,
              priceBuy: pItem.price * (1 + lMargin),
              priceSell: pItem.price * (1 - lMargin),
              basePrice: pItem.price,
              margin: pItem.margin
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
  margin?: number;
  quantity: number;
}