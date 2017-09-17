import {Injectable} from '@angular/core';
import {CommunicationService} from '../communication/communication.service';
import {PlayersService} from '../players/players.service';
import {Helpers} from '../../helpers';

import * as model from '../../models';

@Injectable()
export class ShopsService {

    private shops: model.MapArray<model.IShop> = new model.MapArray();

    private listeners: IShopEventListener[] = [];
    private promiseGetShops: Promise<model.MapArray<model.IShop>> = null;

    constructor(
        private communicationService: CommunicationService,
        private playersService: PlayersService
    ) {
        this.communicationService.addListener("SHOP_EVENT", (pEvent: IShopEvent) => {
            let lShop: model.IShop = this.shops.getElement(pEvent.idShop);
            if (!lShop) {
                lShop = {
                    idShop: pEvent.idShop,
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
                this.shops.addElement(lShop.idShop, lShop);
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
        let lResolve: (e: model.MapArray<model.IShop>) => void = null;
        let lReject: (e: Error) => void = null;
        const lPromise: Promise<model.MapArray<model.IShop>> = new Promise((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject;
        });
        this.promiseGetShops = lPromise;
        this.communicationService.sendWithResponse('PLAYERS_GETALL').then((pResponse: IGetShops) => {
            pResponse.shops.forEach((pShop: model.IShop) => {
                this.shops.addElement(pShop.idShop, pShop);
            });
            lResolve(this.shops);
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
    
    public getShop(pIdShop: number): model.IShop {
        return this.shops.getElement(pIdShop);
    }

    public getPShops(): Promise<model.MapArray<model.IShop>> {
        return this.promiseGetShops;
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
export interface IGetShops {
    shops: model.IShop[];
}