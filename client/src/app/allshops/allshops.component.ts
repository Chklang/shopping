import { Component, OnInit, OnDestroy, ChangeDetectorRef, TemplateRef, ViewChild } from '@angular/core';

import * as model from '../models';
import { ShopsService } from '../services/shops/shops.service';
import { PositionService } from '../services/position/position.service';
import { DistanceService } from '../services/distance/distance.service';

@Component({
  selector: 'app-allshops',
  templateUrl: './allshops.component.html',
  styleUrls: ['./allshops.component.css']
})
export class AllshopsComponent implements OnInit, OnDestroy {
  public columns = null;

  private shops: model.MapArray<IShopElement> = new model.MapArray();

  @ViewChild('positionTmpl')
  private positionTmpl: TemplateRef<any>;

  @ViewChild('ownerTmpl')
  private ownerTmpl: TemplateRef<any>;

  @ViewChild('datatable')
  private datatable: any;

  constructor(
    private shopsService: ShopsService,
    private positionService: PositionService,
    private distanceService: DistanceService
  ) {

  }

  ngOnInit() {
    this.columns = [
      { name: 'Name', property: 'shop.name' },
      { name: 'Owner', cellTemplate: this.ownerTmpl },
      { name: 'Position', cellTemplate: this.positionTmpl },
      { name: 'Distance', property: 'distance' },
      { name: 'Action' }
    ];
    this.shopsService.getShops().then((pShops: model.MapArray<model.IShop>) => {
      console.log("Add shops");
      pShops.forEach((pShop: model.IShop) => {
        const lIdShop: number = pShop.idShop;
        let lShop: IShopElement = this.shops.getElement(lIdShop);
        if (!lShop) {
          lShop = {
            shop: pShop,
            distance: null
          }
          this.shops.addElement(lIdShop, lShop);
        }
      });
      this.listenerMoveEvents(this.positionService.getPosition());
    });
    this.positionService.addListener(this.listenerMoveEvents);
  }

  ngOnDestroy() {
    this.positionService.removeListener(this.listenerMoveEvents);
  }

  private listenerMoveEvents = (pPosition: model.ICoordinates) => {
    console.log("Listener move");
    if (!pPosition) {
      //Ignore event
      return;
    }
    this.shops.forEach((pShopElement: IShopElement) => {
      pShopElement.distance = Math.round(this.distanceService.calculateShop(pShopElement.shop, pPosition) * 100) / 100;
    });
  };

}

interface IShopElement {
  shop: model.IShop;
  distance: number;
}