import { Component, OnInit, OnDestroy, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { LoadingService } from '../services/loading/loading.service';
import { ShopsService, IShopItemUpdateEventListener } from '../services/shops/shops.service';
import { LogService } from '../services/log/log.service';
import { PlayersService } from '../services/players/players.service';
import { TrService } from '../services/tr/tr.service';
import { InventoryService } from '../services/inventory/inventory.service';

import { AbstractItemsComponent } from '../abstract-items.component';

import * as model from '../models';
import { Helpers, IDeferred } from '../helpers';

@Component({
  templateUrl: './shopsdetailsview.component.html',
  styleUrls: ['./shopsdetailsview.component.css']
})
export class ShopsdetailsviewComponent extends AbstractItemsComponent implements OnInit, OnDestroy {

  constructor(
    activatedRoute: ActivatedRoute,
    modalService: BsModalService,
    loadingService: LoadingService,
    shopsService: ShopsService,
    logService: LogService,
    playersService: PlayersService,
    trService: TrService,
    inventoryService: InventoryService
  ) {
    super(activatedRoute, modalService, loadingService, shopsService, logService, playersService, trService, inventoryService);
  }

  ngOnInit() {
    this.loadingService.show();
    this.activatedRoute.params.subscribe((pParams: IParamsOpenView) => {
      this.init(pParams.idShop, 'view').then(() => {
        this.loadingService.hide();
      }).catch((e) => {
        console.error(e);
        this.loadingService.hide();
      });
    });
  }

  ngOnDestroy() {
    this.destroy();
  }
}
interface IParamsOpenView {
    idShop: number;
}