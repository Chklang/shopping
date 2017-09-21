import { Component, OnInit, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

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

  public modalRef: BsModalRef;
  public shop: model.IShop = null;
  public items: model.MapArray<model.IShopItem> = new model.MapArray();

  public itemToBuyOrSell: model.IShopItem = null;

  constructor(
    private activatedRoute: ActivatedRoute,
    private modalService: BsModalService,
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
          console.log('Fin de chargement de la liste des items');
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
              isDefaultPrice: pItem.isDefaultPrice,
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
          console.log('Fin de chargement');
        }).then(() => {
          this.loadingService.hide();
        });
      });
    });
  }

  public buy(pItem: model.IShopItem, pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
  }

  public buyAction(pQuantity: number): void {
    this.shopsService.buy(this.shop.idShop, this.itemToBuyOrSell, pQuantity).then(() => {
      this.modalRef.hide();
    }, () => {
      console.error('Buy error!');
    });
  }

  public sell(pItem: model.IShopItem, pTemplate: TemplateRef<any>): void {
    this.modalRef = this.modalService.show(pTemplate);
  }

  public sellAction(pQuantity: number): void {
    this.shopsService.sell(this.shop.idShop, this.itemToBuyOrSell, pQuantity).then(() => {
      this.modalRef.hide();
    }, () => {
      console.error('Buy error!');
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
}