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
  public items: model.MapArray<IShopItemNamed> = new model.MapArray();

  public itemToBuyOrSell: model.IShopItem = null;

  //Pagination
  private itemsFiltered: IShopItemNamed[] = [];
  public itemsPaging: IShopItemNamed[] = [];
  public totalItems: number = null;
  public currentPage: number = 1;
  public filter_name: string = null;

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
          pResponse.items.forEach((pItem: IShopItemElementResponse) => {
            let lMargin: number = null;
            if (pItem.margin === null) {
              lMargin = pShop.baseMargin;
            } else {
              lMargin = pItem.margin;
            }
            let lShopItem: IShopItemNamed = {
              idItem: pItem.idItem,
              subIdItem: pItem.subIdItem,
              item: {
                'EN': 'test'
              },
              nbIntoShop: pItem.quantity,
              nbToBuy: pItem.sell,
              nbToSell: pItem.buy,
              priceBuy: Math.round(pItem.price * (1 + lMargin) * 100) / 100,
              priceSell: Math.round(pItem.price * (1 - lMargin) * 100) / 100,
              basePrice: pItem.price,
              isDefaultPrice: pItem.isDefaultPrice,
              margin: pItem.margin,
              name: pItem.idItem + "_" + pItem.subIdItem
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
          this.totalItems = this.items.length;
          this.currentPage = 1;
          this.itemsPaging = this.items.slice(0, 10);
        }).then(() => {
          this.loadingService.hide();
        });
      });
    });
  }

  public filterRefresh(): void {
    if (!this.filter_name) {
      this.itemsFiltered = this.items;
      this.itemsPaging = this.items.slice((this.currentPage - 1) * 10, this.currentPage * 10);
      return;
    }
    this.itemsFiltered = [];
    const lRegexp = new RegExp(this.filter_name);
    this.items.forEach((pItem: IShopItemNamed) => {
      if (!lRegexp.test(pItem.name)) {
        return;
      }
      this.itemsFiltered.push(pItem);
    });
    this.totalItems = this.itemsFiltered.length;
    this.currentPage = 1;
    this.itemsPaging = this.itemsFiltered.slice((this.currentPage - 1) * 10, this.currentPage * 10);
  }

  public pageChanged(event: any): void {
    this.itemsPaging = this.itemsFiltered.slice((event.page - 1) * event.itemsPerPage, event.page * event.itemsPerPage);
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
interface IShopItemNamed extends model.IShopItem {
  name: string;
}