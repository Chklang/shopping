import { Component, OnInit } from '@angular/core';

import { PositionService, IPosition } from '../services/position/position.service';

@Component({
  selector: 'app-statusbar',
  templateUrl: './statusbar.component.html',
  styleUrls: ['./statusbar.component.css']
})
export class StatusbarComponent implements OnInit {

  public currentPosition: IPosition = {
    x: 0,
    y: 0,
    z: 0
  };

  constructor(
    private positionService: PositionService
  ) {

  }

  ngOnInit() {
    this.init();
  }

  public init(): void {
    this.positionService.addListener((pPosition: IPosition) => {
      this.currentPosition.x = Math.trunc(pPosition.x);
      this.currentPosition.y = Math.trunc(pPosition.y);
      this.currentPosition.z = Math.trunc(pPosition.z);
    });
  }

}
