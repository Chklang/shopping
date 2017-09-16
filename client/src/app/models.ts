export class IMapArray<T> extends Array<T> {
    public getElement(pKey: number): T {
        return this[pKey];
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
    xmin: number;
    xmax: number;
    ymin: number;
    ymax: number;
    zmin: number;
    zmax: number;
    name: string;
    owner?: IPlayer;
    items: IMapArray<IItemShop>;
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
}

export interface IItem {
    name: ITranslatedString;
}

export interface IItemShop {
    item: ITranslatedString;
    price: number;
    nbToSell: number;
    nbToBuy: number;
    nbIntoShop: number;
}
