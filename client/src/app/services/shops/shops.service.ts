import { Injectable } from '@angular/core';
import { CommunicationService } from '../communication/communication.service';
import { PlayersService } from '../players/players.service';
import { Helpers, IDeferred } from '../../helpers';

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
        this.communicationService.addListener('SHOP_EVENT', (pEvent: IShopEvent) => {
            this.saveShop(pEvent).then((pShop: model.IShop) => {
                this.listeners.forEach((pListener: IShopEventListener) => {
                    pListener(pShop);
                });
            });
        });
        //Wait to get all players
        this.promiseGetShops = Helpers.createPromise((pDeferred: IDeferred<model.MapArray<model.IShop>>) => {
            this.playersService.getPlayers().then(() => {
                this.communicationService.sendWithResponse('SHOPS_GETALL').then((pResponse: IGetShops) => {
                    const lPromisesShops: Promise<model.IShop>[] = [];
                    pResponse.shops.forEach((pShop: IShopEvent) => {
                        lPromisesShops.push(this.saveShop(pShop));
                    });
                    Helpers.promisesAll(lPromisesShops).then((pShops: model.IShop[]) => {
                        pShops.forEach((pShop: model.IShop) => {
                            this.shops.addElement(pShop.idShop, pShop);
                        });
                        pDeferred.resolve(this.shops);
                    });
                });
            });
        });
    }

    public changeOwner(pShop: model.IShop, pOwner: model.IPlayer): Promise<boolean> {
        return this.communicationService.sendWithResponse('SHOPS_CHANGE_OWNER', <IShopChangeOwnerRequest>{
            idShop: pShop.idShop,
            idOwner: pOwner ? pOwner.idPlayer : null
        }).then((pResponse: IShopChangeOwnerResponse) => {
            return pResponse.isOk;
        });
    }

    private saveShop(pShop: IShopEvent): Promise<model.IShop> {
        let lShop: model.IShop = this.shops.getElement(pShop.idShop);
        if (!lShop) {
            lShop = {
                idShop: pShop.idShop,
                items: new model.MapArray<model.IShopItem>(),
                name: null,
                owner: null,
                xmin: null,
                xmax: null,
                ymin: null,
                ymax: null,
                zmin: null,
                zmax: null,
                baseMargin: null,
            };
            this.shops.addElement(lShop.idShop, lShop);
        }
        if (pShop.idOwner !== null) {
            return this.playersService.getPlayer(pShop.idOwner).then((pPlayer: model.IPlayer) => {
                lShop.owner = pPlayer;
                lShop.xmin = pShop.x_min;
                lShop.xmax = pShop.x_max;
                lShop.ymin = pShop.y_min;
                lShop.ymax = pShop.y_max;
                lShop.zmin = pShop.z_min;
                lShop.zmax = pShop.z_max;
                lShop.baseMargin = pShop.baseMargin;
                lShop.name = pShop.name;
                return lShop;
            });
        } else {
            return Helpers.createPromise((pDefer: IDeferred<model.IShop>) => {
                lShop.owner = null;
                lShop.xmin = pShop.x_min;
                lShop.xmax = pShop.x_max;
                lShop.ymin = pShop.y_min;
                lShop.ymax = pShop.y_max;
                lShop.zmin = pShop.z_min;
                lShop.zmax = pShop.z_max;
                lShop.baseMargin = pShop.baseMargin;
                lShop.name = pShop.name;
                pDefer.resolve(lShop);
            });
        }
    }

    public buy(pShopId: number, pItem: model.IShopItem, pQuantity: number): Promise<boolean> {
        return this.communicationService.sendWithResponse('SHOPS_BUY_OR_SELL', <IShopBuyOrSellRequest>{
            actionType: ShopBuyOrSellActionType.BUY,
            idItem: pItem.idItem,
            idShop: pShopId,
            quantity: pQuantity,
            subIdItem: pItem.subIdItem
        }).then((pResponse: IShopBuyOrSellResponse) => {
            return pResponse.isOk;
        });
    }

    public sell(pShopId: number, pItem: model.IShopItem, pQuantity: number): Promise<boolean> {
        return this.communicationService.sendWithResponse('SHOPS_BUY_OR_SELL', <IShopBuyOrSellRequest>{
            actionType: ShopBuyOrSellActionType.SELL,
            idItem: pItem.idItem,
            idShop: pShopId,
            quantity: pQuantity,
            subIdItem: pItem.subIdItem
        }).then((pResponse: IShopBuyOrSellResponse) => {
            return pResponse.isOk;
        });
    }

    public setItem(pShop: model.IShop, pItem: model.IShopItem): Promise<boolean> {
        console.log("Save : ", pItem);
        return this.communicationService.sendWithResponse('SHOPS_SET_ITEM', <IShopSetItemRequest>{
            idShop: pShop.idShop,
            idItem: pItem.idItem,
            subIdItem: pItem.subIdItem,
            buy: pItem.nbToBuy,
            sell: pItem.nbToSell,
            price: pItem.isDefaultPrice ? null : pItem.basePrice,
            margin: pItem.margin
        }).then((pResponse: IShopSetItemResponse) => {
            return pResponse.isOk;
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

    public getShop(pIdShop: number): Promise<model.IShop> {
        return this.getShops().then((pShops: model.MapArray<model.IShop>) => {
            return pShops.getElement(pIdShop);
        });
    }

    public getShops(): Promise<model.MapArray<model.IShop>> {
        return this.promiseGetShops;
    }

    public setProperties(pShop: model.IShop): Promise<model.IShop> {
        return this.communicationService.sendWithResponse('SHOPS_SET_PROPERTIES', <IShopPropertiesRequest>{
            baseMargin: pShop.baseMargin,
            idShop: pShop.idShop,
            name: pShop.name
        }).then((pResponse: IShopPropertiesResponse) => {
            if (!pResponse.isOk) {
                throw 'Update error';
            }
            return pShop;
        });
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
    baseMargin: number;
    name: string;
}
interface IShopPropertiesRequest {
    idShop: number;
    baseMargin: number;
    name: string;
}
interface IShopPropertiesResponse {
    isOk: boolean;
}
interface IShopChangeOwnerRequest {
    idShop: number;
    idOwner?: number;
}
interface IShopChangeOwnerResponse {
    isOk: boolean;
}
interface IShopBuyOrSellRequest {
    idShop: number;
    idItem: number;
    subIdItem: number;
    actionType: number;
    quantity: number;
}
enum ShopBuyOrSellActionType {
    BUY = 1,
    SELL = 2
}
interface IShopBuyOrSellResponse {
    isOk: boolean;
}
interface IShopSetItemRequest {
    idShop: number;
    idItem: number;
    subIdItem: number;
    margin?: number;
    price?: number;
    buy: number;
    sell: number;
}
interface IShopSetItemResponse {
    isOk: boolean;
}
export interface IShopEventListener {
    (pShop: model.IShop): void;
}
export interface IGetShops {
    shops: IShopEvent[];
}