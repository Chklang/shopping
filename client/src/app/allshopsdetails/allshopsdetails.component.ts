import { Component, OnInit, OnDestroy, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { DistanceService } from '../services/distance/distance.service';
import { LoadingService } from '../services/loading/loading.service';
import { CommunicationService } from '../services/communication/communication.service';
import { ShopsService } from '../services/shops/shops.service';
import { PositionService } from '../services/position/position.service';
import { TrService } from '../services/tr/tr.service';

import * as model from '../models';
import { Helpers } from '../helpers';

@Component({
  selector: 'app-allshopsdetails',
  templateUrl: './allshopsdetails.component.html',
  styleUrls: ['./allshopsdetails.component.css']
})
export class AllshopsdetailsComponent implements OnInit, OnDestroy {

  public modalRef: BsModalRef;
  public shop: model.IShop = null;
  public items: model.MapArray<IShopItemNamed> = new model.MapArray();

  public itemToBuyOrSell: model.IShopItem = null;
  public distance: number = null;

  //Pagination
  private itemsFiltered: IShopItemNamed[] = [];
  public itemsPaging: IShopItemNamed[] = [];
  public totalItems: number = null;
  public currentPage: number = 1;
  public filter_name: string = null;

  constructor(
    private distanceService: DistanceService,
    private activatedRoute: ActivatedRoute,
    private modalService: BsModalService,
    private loadingService: LoadingService,
    private communicationService: CommunicationService,
    private shopsService: ShopsService,
    private positionService: PositionService,
    private trService: TrService
  ) {

  }

  ngOnInit() {
    this.loadingService.show();
    this.activatedRoute.params.subscribe((pParams) => {
      this.shopsService.getShop(pParams['id']).then((pShop: model.IShop) => {
        this.shop = pShop;
        this.positionService.addListener(this.listenerPosition);
        this.communicationService.sendWithResponse('SHOPS_GET_ITEMS', <IShopItemRequest>{
          idShop: pShop.idShop
        }).then((pResponse: IShopItemResponse) => {
          let lPromises: Promise<void>[] = [];
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
              nbIntoShop: pItem.quantity,
              nbToBuy: pItem.sell,
              nbToSell: pItem.buy,
              priceBuy: Math.round(pItem.price * (1 + lMargin) * 100) / 100,
              priceSell: Math.round(pItem.price * (1 - lMargin) * 100) / 100,
              basePrice: pItem.price,
              isDefaultPrice: pItem.isDefaultPrice,
              margin: pItem.margin,
              isDefaultMargin: pItem.margin === null,
              name: '',
              nameSimplified: ''
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
          this.loadingService.hide();
        });
      });
    });
  }

  ngOnDestroy() {
    this.positionService.removeListener(this.listenerPosition);
  }

  private listenerPosition = (pPosition: model.ICoordinates) => {
    this.distance = this.distanceService.calculateShop(this.shop, pPosition);
    console.log(pPosition);
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
    this.items.forEach((pItem: IShopItemNamed) => {
      if (!lRegexp.test(pItem.nameSimplified)) {
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
    this.itemToBuyOrSell = pItem;
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
    this.itemToBuyOrSell = pItem;
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
  name: string;
  nameDetails: string;
}
interface IShopItemNamed extends model.IShopItem {
  name: string;
  nameSimplified: string;
}