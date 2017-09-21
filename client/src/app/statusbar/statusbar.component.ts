import { Component, OnInit } from '@angular/core';

import { PositionService } from '../services/position/position.service';
import {PlayersService} from '../services/players/players.service';
import {LogService} from '../services/log/log.service';
import * as model from '../models';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.css']
})
export class StatusbarComponent implements OnInit {

  private idPlayerConnected: number = null;
  public player: model.IPlayer = null;

  constructor(
    private positionService: PositionService,
    private playerService: PlayersService,
    private logService: LogService
  ) {

  }

  ngOnInit() {
    this.init();
  }

  public init(): void {
    this.logService.getCurrentIdPlayer().then((pIdPlayer: number) => {
      this.idPlayerConnected = pIdPlayer;
      this.positionService.addListener((pPosition: model.ICoordinates) => {
        if (this.player) {
          this.player.coordinates.x = Math.trunc(pPosition.x);
          this.player.coordinates.y = Math.trunc(pPosition.y);
          this.player.coordinates.z = Math.trunc(pPosition.z);
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

}
