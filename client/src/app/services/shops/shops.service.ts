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
            let lShop: model.IShop = this.saveShop(pEvent);

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
        //Wait to get all players
        this.playersService.getPlayers().then(() => {
            this.communicationService.sendWithResponse('SHOPS_GETALL').then((pResponse: IGetShops) => {
                pResponse.shops.forEach((pShop: IShopEvent) => {
                    let lShop: model.IShop = this.saveShop(pShop);
                    this.shops.addElement(lShop.idShop, lShop);
                });
                lResolve(this.shops);
            });
        });
    }

    private saveShop(pShop: IShopEvent): model.IShop {
        let lShop: model.IShop = this.shops.getElement(pShop.idShop);
        if (!lShop) {
            lShop = {
                idShop: pShop.idShop,
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
        lShop.owner = this.playersService.getPlayer(pShop.idOwner);
        lShop.xmin = pShop.x_min;
        lShop.xmax = pShop.x_max;
        lShop.ymin = pShop.y_min;
        lShop.ymax = pShop.y_max;
        lShop.zmin = pShop.z_min;
        lShop.zmax = pShop.z_max;
        return lShop;
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

    public getShops(): Promise<model.MapArray<model.IShop>> {
        return this.promiseGetShops;
    }
}

interface IShopEvent {
    idShop: number;
    idOwner?: number;
    x_min: number;
    x_max: number;
    y_min: number;
    y_max: number;
    z_min: number;
    z_max: number;
}

export interface IShopEventListener {
    (pShop: model.IShop): void;
}
export interface IGetShops {
    shops: IShopEvent[];
}