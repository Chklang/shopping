import { Component, OnInit, OnDestroy, ChangeDetectorRef, TemplateRef, ViewChild } from '@angular/core';

import * as model from '../models';
import { ShopsService } from '../services/shops/shops.service';
import { PositionService } from '../services/position/position.service';
import { PlayersService } from '../services/players/players.service';
import { LogService } from '../services/log/log.service';
import { DistanceService } from '../services/distance/distance.service';

@Component({
  selector: 'app-allshops',
  templateUrl: './allshops.component.html',
  styleUrls: ['./allshops.component.css']
})
export class AllshopsComponent implements OnInit, OnDestroy {
  public columns = null;

  private shops: model.MapArray<IShopElement> = new model.MapArray();
  private currentPlayer: model.IPlayer = null;

  @ViewChild('positionTmpl')
  private positionTmpl: TemplateRef<any>;

  @ViewChild('ownerTmpl')
  private ownerTmpl: TemplateRef<any>;

  @ViewChild('datatable')
  private datatable: any;

  constructor(
    private shopsService: ShopsService,
    private playersService: PlayersService,
    private logService: LogService,
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

    this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
      return this.playersService.getPlayer(pIdPlayer);
    }).then((pPlayer: model.IPlayer) => {
      this.currentPlayer = pPlayer;
      return this.shopsService.getShops();
    }).then((pShops: model.MapArray<model.IShop>) => {
      console.log('Add shops');
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
      console.log(this);
      this.listenerMoveEvents(this.positionService.getPosition());
    });
    this.positionService.addListener(this.listenerMoveEvents);
  }

  ngOnDestroy() {
    this.positionService.removeListener(this.listenerMoveEvents);
  }

  private listenerMoveEvents = (pPosition: model.ICoordinates) => {
    if (!pPosition) {
      //Ignore event
      return;
    }
    this.shops.forEach((pShopElement: IShopElement) => {
      pShopElement.distance = Math.round(this.distanceService.calculateShop(pShopElement.shop, pPosition) * 100) / 100;
    });
  };

  public setToGlobalShop(pShop: model.IShop): void {
    this.shopsService.changeOwner(pShop, null).then((pIsOk: boolean) => {
      if (pIsOk) {
        console.log('Set properties OK');
        pShop.owner = null;
      } else {
        console.warn('Set properties NOK');
      }
    }, () => {
      console.error('Set properties NOK');
    });
  }

  public setShopToYou(pShop: model.IShop): void {
    this.shopsService.changeOwner(pShop, this.currentPlayer).then((pIsOk: boolean) => {
      if (pIsOk) {
        console.log('Set properties OK');
        pShop.owner = this.currentPlayer;
      } else {
        console.warn('Set properties NOK');
      }
    }, () => {
      console.error('Set properties NOK');
    });
  }

}

interface IShopElement {
  shop: model.IShop;
  distance: number;
}