import {Injectable} from '@angular/core';
import {CommunicationService} from '../communication/communication.service';
import {PlayersService} from '../players/players.service';
import {Helpers} from '../../helpers';

import * as model from '../../models';

@Injectable()
export class ShopsService {

    private shops: model.IShop[] = [];
    private shopsById: {[key: number]: model.IShop} = {};

    private listeners: IShopEventListener[] = [];

    constructor(
        private communicationService: CommunicationService,
        private playersService: PlayersService
    ) {
        this.communicationService.addListener("PLAYER_EVENT", (pEvent: IShopEvent) => {
            let lShop: model.IShop = this.shopsById[pEvent.idShop];
            if (!lShop) {
                lShop = {
                    items: new model.MapArray<model.IItemShop>(),
                    name: null,
                    owner: null,
                    xmin: null,
                    xmax: null,
                    ymin: null,
                    ymax: null,
                    zmin: null,
                    zmax: null
                };
                this.shops.push(lShop);
            }
            lShop.owner = this.playersService.getPlayer(pEvent.idOwner);
            lShop.xmin = pEvent.xMin;
            lShop.xmax = pEvent.xMax;
            lShop.ymin = pEvent.yMin;
            lShop.ymax = pEvent.yMax;
            lShop.zmin = pEvent.zMin;
            lShop.zmax = pEvent.zMax;

            this.listeners.forEach((pListener: IShopEventListener) => {
                pListener(lShop);
            });
        });
    }

    public addListener(pListener: IShopEventListener): void {
        this.listeners.push(pListener);
    }

    public removeListener(pListener: IShopEventListener): void {
        Helpers.remove(this.listeners, (pListenerCurrent: IShopEventListener) => {
            return pListenerCurrent === pListener;
        });
    }
}

interface IShopEvent {
    idShop: number;
    idOwner?: number;
    xMin: number;
    xMax: number;
    yMin: number;
    yMax: number;
    zMin: number;
    zMax: number;
}

export interface IShopEventListener {
    (pShop: model.IShop): void;
}