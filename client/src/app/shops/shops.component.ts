import { Component, OnInit, OnDestroy, ChangeDetectorRef, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import * as model from '../models';
import { Helpers } from '../helpers';
import { ShopsService } from '../services/shops/shops.service';
import { PositionService } from '../services/position/position.service';
import { PlayersService } from '../services/players/players.service';
import { LogService } from '../services/log/log.service';
import { DistanceService } from '../services/distance/distance.service';
import { AlertsService } from '../services/alerts/alerts.service';

@Component({
  selector: 'app-shops',
  templateUrl: './shops.component.html',
  styleUrls: ['./shops.component.css']
})
export class ShopsComponent implements OnInit, OnDestroy {
  public columns = null;

  private shops: model.MapArray<IShopElement> = new model.MapArray();
  public currentPlayer: model.IPlayer = null;
  public onlyMyOwnShops: boolean = null;

  @ViewChild('positionTmpl')
  private positionTmpl: TemplateRef<any>;

  @ViewChild('ownerTmpl')
  private ownerTmpl: TemplateRef<any>;

  @ViewChild('datatable')
  private datatable: any;

  constructor(
    private activatedRoute: ActivatedRoute,
    private shopsService: ShopsService,
    private playersService: PlayersService,
    private logService: LogService,
    private positionService: PositionService,
    private distanceService: DistanceService,
    private alertsService: AlertsService
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

    this.activatedRoute.params.subscribe((pParams: IParamsOpenView) => {
      this.onlyMyOwnShops = pParams.mode === 'modify';
      Helpers.promiseSelfResolved.then(() => {
        if (this.onlyMyOwnShops) {
          return this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
            if (pIdPlayer === null) {
              throw new Error('Player not connected');
            } else {
              return this.playersService.getPlayer(pIdPlayer);
            }
          });
        } else {
          return null;
        }
      }).then((pPlayer: model.IPlayer) => {
        this.currentPlayer = pPlayer;
        return this.shopsService.getShops();
      }).then((pShops: model.MapArray<model.IShop>) => {
        pShops.forEach((pShop: model.IShop) => {
          if (this.onlyMyOwnShops && pShop.owner === null && !this.currentPlayer.isOp) {
            //Can't modify global shops
            return;
          }
          if (this.onlyMyOwnShops && pShop.owner !== this.currentPlayer && !this.currentPlayer.isOp) {
            //It's not your shop
            return;
          }
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
      }).catch(() => {
      });
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
interface IParamsOpenView {
  mode: string;
}