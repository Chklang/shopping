import { Component, OnInit } from '@angular/core';

import { DistanceService } from '../services/distance/distance.service';
import { PositionService } from '../services/position/position.service';
import { PlayersService } from '../services/players/players.service';
import { LogService } from '../services/log/log.service';
import { ShopsService } from '../services/shops/shops.service';

import * as model from '../models';
import { Helpers } from '../helpers';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.css']
})
export class StatusbarComponent implements OnInit {

  private idPlayerConnected: number = null;
  private shops: model.IShop[] = [];
  public player: model.IPlayer = null;
  public shopDistance: string = '-';
  public nearShop: model.IShop = null;

  constructor(
    private distanceService: DistanceService,
    private positionService: PositionService,
    private playerService: PlayersService,
    private logService: LogService,
    private shopsService: ShopsService
  ) {

  }

  ngOnInit() {
    this.init();
  }

  public init(): void {
    this.shopsService.getShops().then((pShops: model.MapArray<model.IShop>) => {
      this.shops = pShops;
    })
    this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
      this.idPlayerConnected = pIdPlayer;
      if (pIdPlayer === null) {
        return;
      }
      this.positionService.addListener((pPosition: model.ICoordinates) => {
        if (this.player) {
          this.player.coordinates.x = Math.trunc(pPosition.x);
          this.player.coordinates.y = Math.trunc(pPosition.y);
          this.player.coordinates.z = Math.trunc(pPosition.z);
        }
        let lMinDistance = null;
        let lNearShop = null;
        if (this.shops && this.shops.length > 0) {
          this.shops.forEach((pShop: model.IShop) => {
            let lDistance: number = this.distanceService.calculateShop(pShop, pPosition);
            if (lMinDistance === null || lMinDistance > lDistance) {
              lMinDistance = lDistance;
              lNearShop = pShop;
            }
          });
          if (lMinDistance === null) {
            this.shopDistance = '-';
            this.nearShop = null;
          } else {
            this.shopDistance = '' + Math.round(lMinDistance * 100) / 100;
            this.nearShop = lNearShop;
          }
        }
      });
      this.playerService.getPlayer(this.idPlayerConnected).then((pPlayer: model.IPlayer) => {
        this.listenerPlayer(pPlayer);
      })
      this.playerService.addListener(this.listenerPlayer);
    })
  }

  private listenerPlayer = (pPlayerEvent: model.IPlayer) => {
    if (this.idPlayerConnected === pPlayerEvent.idPlayer) {
      let lOldCoordinates: model.ICoordinates = null;
      if (this.player !== null) {
        lOldCoordinates = this.player.coordinates;
      }
      this.player = pPlayerEvent;
      if (!this.player.coordinates) {
        this.player.coordinates = lOldCoordinates;
      }
      if (!this.player.coordinates) {
        this.player.coordinates = {
          x: 0,
          y: 0,
          z: 0
        }
      }
    }
  }

  private listenerShops = () => {

  }

}

interface IShopDistance extends model.IShop {

}
