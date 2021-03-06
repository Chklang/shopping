import {Helpers} from './helpers';

export class MapArray<T> extends Array<T> {
    public getElement = (pKey: number|string): T => {
        return this['id_' + pKey];
    }
    public addElement = (pKey: number|string, pElement: T): boolean => {
        if (this['id_' + pKey] !== undefined) {
            //Object already into map
            this.removeElement(pKey);
        }
        this.push(pElement);
        this['id_' + pKey] = pElement;
        return true;
    }
    public removeElement = (pKey: number|string): boolean => {
        let lElementToRemove: T = this['id_' + pKey];
        if (lElementToRemove === undefined) {
            return false;
        }
        Helpers.remove(this, (pElementCurrent: T) => {
            return pElementCurrent === lElementToRemove;
        });
        delete this['id_' + pKey];
        return true;
    }
}

export enum Language {
    EN
}

export interface ITranslatedString {
    [key: string]: string;
}

export interface ICoordinates {
    x: number;
    y: number;
    z: number;
}

export interface IShop {
    idShop: number;
    xmin: number;
    xmax: number;
    ymin: number;
    ymax: number;
    zmin: number;
    zmax: number;
    name: string;
    owner?: IPlayer;
    items: MapArray<IShopItem>;
    baseMargin: number;
    space: number;
}

export interface IShopDistance {
    shop: IShop;
    distance: number;
}

export interface IPlayer {
    idPlayer: number;
    pseudo: string;
    isOnline: boolean;
    coordinates?: ICoordinates;
    money: number;
    isOp: boolean;
}

export interface IPlayerItem {
    idItem: number;
    subIdItem?: number;
    name: string;
    quantity: number;
}

export interface IShopItem {
    idItem: number;
    subIdItem?: number;
    name: string;
    isDefaultPrice: boolean;
    priceBuy: number;
    priceSell: number;
    nbToSell: number;
    nbToBuy: number;
    nbIntoShop: number;
    basePrice: number;
    margin: number;
    isDefaultMargin: boolean;
}
