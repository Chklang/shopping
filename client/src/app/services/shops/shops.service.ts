import { Injectable } from '@angular/core';
import { CommunicationService } from '../communication/communication.service';
import { DistanceService } from '../distance/distance.service';
import { PositionService } from '../position/position.service';
import { PlayersService } from '../players/players.service';
import { TrService } from '../tr/tr.service';
import { Helpers, IDeferred } from '../../helpers';

import * as model from '../../models';

@Injectable()
export class ShopsService {

    private shops: model.MapArray<model.IShop> = new model.MapArray();

    private listenersShopUpdate: IShopUpdateEventListener[] = [];
    private listenersShopItemUpdate: IShopItemUpdateEventListener[] = [];
    private listenersNearShop: IListenerNearShop[] = []
    private promiseGetShops: Promise<model.MapArray<model.IShop>> = null;
    private nearShop: model.IShopDistance = null;

    constructor(
        private communicationService: CommunicationService,
        private distanceService: DistanceService,
        private playersService: PlayersService,
        private positionService: PositionService,
        private trService: TrService
    ) {
        this.communicationService.addListener('SHOP_ITEM_EVENT', (pEvent: IShopItemUpdateEvent) => {
            let lShopItem: model.IShopItem = {
                basePrice: pEvent.price,
                idItem: pEvent.idItem,
                subIdItem: pEvent.subIdItem,
                isDefaultPrice: pEvent.isDefaultPrice,
                name: null,
                margin: pEvent.margin,
                isDefaultMargin: pEvent.margin === null,
                nbIntoShop: pEvent.quantity,
                nbToBuy: pEvent.buy,
                nbToSell: pEvent.sell,
                priceBuy: null,
                priceSell: null
            };
            this.listenersShopItemUpdate.forEach((pListener: IShopItemUpdateEventListener) => {
                pListener(pEvent.idShop, lShopItem);
            });
        });
        this.communicationService.addListener('SHOP_EVENT', (pEvent: IShopUpdateEvent) => {
            this.saveShop(pEvent).then((pShop: model.IShop) => {
                this.listenersShopUpdate.forEach((pListener: IShopUpdateEventListener) => {
                    pListener(pShop);
                });
            });
        });
        //Wait to get all players
        this.promiseGetShops = Helpers.createPromise((pDeferred: IDeferred<model.MapArray<model.IShop>>) => {
            this.playersService.getPlayers().then(() => {
                this.communicationService.sendWithResponse('SHOPS_GETALL').then((pResponse: IGetShops) => {
                    const lPromisesShops: Promise<model.IShop>[] = [];
                    pResponse.shops.forEach((pShop: IShopUpdateEvent) => {
                        lPromisesShops.push(this.saveShop(pShop));
                    });
                    Helpers.promisesAll(lPromisesShops).then((pShops: model.IShop[]) => {
                        pShops.forEach((pShop: model.IShop) => {
                            this.shops.addElement(pShop.idShop, pShop);
                        });
                        pDeferred.resolve(this.shops);
                        this.listenerPositionChange(this.positionService.getPosition());
                    });
                });
            });
        });
        this.positionService.addListener(this.listenerPositionChange);
    }

    private listenerPositionChange = (pPosition: model.ICoordinates) => {
        if (!pPosition) {
            return;
        }
        if (this.shops && this.shops.length > 0) {
            let lNearShop = null;
            let lMinDistance = null;
            this.shops.forEach((pShop: model.IShop) => {
                let lDistance: number = this.distanceService.calculateShop(pShop, pPosition);
                if (lMinDistance === null || lMinDistance > lDistance) {
                    lMinDistance = lDistance;
                    lNearShop = pShop;
                }
            });
            if (lMinDistance === null) {
                this.nearShop = null;
            } else {
                if (this.nearShop === null) {
                    this.nearShop = {
                        distance: null,
                        shop: null,
                    };
                }
                this.nearShop.shop = lNearShop;
                this.nearShop.distance = Math.round(lMinDistance * 100) / 100;
            }
            this.listenersNearShop.forEach((pListener: IListenerNearShop) => {
                pListener(this.nearShop);
            });
        }
    }

    public changeOwner(pShop: model.IShop, pOwner: model.IPlayer): Promise<boolean> {
        return this.communicationService.sendWithResponse('SHOPS_CHANGE_OWNER', <IShopChangeOwnerRequest>{
            idShop: pShop.idShop,
            idOwner: pOwner ? pOwner.idPlayer : null
        }).then((pResponse: IShopChangeOwnerResponse) => {
            return pResponse.isOk;
        });
    }

    private saveShop(pShop: IShopUpdateEvent): Promise<model.IShop> {
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
                space: null
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
                lShop.space = pShop.space;
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

    public sell(pShopId: number, pItem: model.IShopItem | model.IPlayerItem, pQuantity: number): Promise<boolean> {
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

    public buySpace(pShop: model.IShop, pSpaceToAdd: number): Promise<boolean> {
        return this.communicationService.sendWithResponse('SHOPS_BUY_SPACE', <IShopBuySpaceRequest>{
            idShop: pShop.idShop,
            quantity: pSpaceToAdd
        }).then((pResponse: IShopBuySpaceResponse) => {
            return pResponse.isOk;
        });
    }

    public addListenerShopUpdate(pListener: IShopUpdateEventListener): void {
        this.listenersShopUpdate.push(pListener);
    }

    public removeListenerShopUpdate(pListener: IShopUpdateEventListener): void {
        Helpers.remove(this.listenersShopUpdate, (pListenerCurrent: IShopUpdateEventListener) => {
            return pListenerCurrent === pListener;
        });
    }

    public addListenerShopItemUpdate(pListener: IShopItemUpdateEventListener): void {
        this.listenersShopItemUpdate.push(pListener);
    }

    public removeListenerShopItemUpdate(pListener: IShopItemUpdateEventListener): void {
        Helpers.remove(this.listenersShopItemUpdate, (pListenerCurrent: IShopItemUpdateEventListener) => {
            return pListenerCurrent === pListener;
        });
    }

    public addListenerNearShop(pListener: IListenerNearShop): void {
        this.listenersNearShop.push(pListener);
    }

    public removeListenerNearShop(pListener: IListenerNearShop): void {
        Helpers.remove(this.listenersNearShop, (pListenerCurrent: IListenerNearShop) => {
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

    public subscribeShopEvent(pShop: model.IShop): void {
        this.communicationService.send('SHOPS_SUBSCRIBE', <IShopSubscribeRequest>{
            idShop: pShop.idShop
        });
    }
    public unsubscribeShopEven(): void {
        this.communicationService.send('SHOPS_UNSUBSCRIBE');
    }

    public getItems(pShop: model.IShop): Promise<model.IShopItem[]> {
        let lResults: model.IShopItem[] = [];
        return this.communicationService.sendWithResponse('SHOPS_GET_ITEMS', <IShopItemRequest>{
            idShop: pShop.idShop
        }).then((pResponse: IShopItemResponse) => {
            let lPromises: Promise<void>[] = [];
            pResponse.items.forEach((pItem: IShopItemElementResponse) => {
                let lMargin: number = null;
                if (pItem.margin === null) {
                    lMargin = pShop.baseMargin;
                } else {
                    lMargin = pItem.margin;
                }
                let lItem: model.IShopItem = {
                    basePrice: pItem.price,
                    idItem: pItem.idItem,
                    subIdItem: pItem.subIdItem,
                    isDefaultPrice: pItem.isDefaultPrice,
                    name: null,
                    margin: lMargin,
                    isDefaultMargin: pItem.margin === null,
                    nbIntoShop: pItem.quantity,
                    nbToBuy: pItem.buy,
                    nbToSell: pItem.sell,
                    priceBuy: pItem.price * (1 + lMargin),
                    priceSell: pItem.price * (1 - lMargin)
                };
                lPromises.push(this.trService.getText(pItem.name).then((pNameValue: string) => {
                    if (pItem.nameDetails) {
                        pNameValue += ' (' + pItem.nameDetails + ')';
                    }
                    lItem.name = pNameValue;
                    lResults.push(lItem);
                }));
            });
            return Helpers.promisesAll(lPromises);
        }).then(() => {
            return lResults;
        });
    }
}

interface IShopSubscribeRequest {
    idShop: number;
}

interface IShopEvent {
    type: string;
}
interface IShopItemUpdateEvent extends IShopEvent {
    idShop: number;
    idItem: number;
    subIdItem: number;
    margin: number;
    price: number;
    isDefaultPrice: boolean;
    buy: number;
    sell: number;
    quantity: number;
}
interface IShopUpdateEvent extends IShopEvent {
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
    space: number;
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
interface IShopBuySpaceRequest {
    idShop: number;
    quantity: number;
}
interface IShopBuySpaceResponse {
    isOk: boolean;
}
export interface IShopUpdateEventListener {
    (pShop: model.IShop): void;
}
export interface IShopItemUpdateEventListener {
    (pIdShop: number, pShopItem: model.IShopItem): void;
}
interface IGetShops {
    shops: IShopUpdateEvent[];
}

interface IShopItemRequest {
    idShop: number;
}
interface IShopItemResponse {
    items: IShopItemElementResponse[];
}
interface IShopItemElementResponse {
    idItem: number;
    subIdItem: number;
    sell: number;
    buy: number;
    price?: number;
    isDefaultPrice?: boolean;
    margin?: number;
    quantity: number;
    name: string;
    nameDetails: string;
}
export type IListenerNearShop = (pEvent: model.IShopDistance) => void;