import {Injectable} from '@angular/core';
import {CommunicationService} from '../communication/communication.service';
import {Helpers} from '../../helpers';

import * as model from '../../models';

@Injectable()
export class PlayersService {

    private players: model.MapArray<model.IPlayer> = new model.MapArray();

    private listeners: IPlayerEventListener[] = [];
    private promiseGetPlayers: Promise<model.MapArray<model.IPlayer>> = null;

    constructor(
        private communicationService: CommunicationService
    ) {
        this.communicationService.addListener("PLAYER_EVENT", (pEvent: IPlayerEvent) => {
            let lPlayer: model.IPlayer = this.players.getElement(pEvent.idPlayer);
            if (!lPlayer) {
                lPlayer = {
                    coordinates: null,
                    idPlayer: pEvent.idPlayer,
                    isOnline: null,
                    money: null,
                    pseudo: null
                };
                this.players.addElement(lPlayer.idPlayer, lPlayer);
            }
            lPlayer.isOnline = pEvent.joinType === 1;
            lPlayer.money = pEvent.money;
            lPlayer.pseudo = pEvent.name;

            this.listeners.forEach((pListener: IPlayerEventListener) => {
                pListener(lPlayer);
            });
        });
        this.communicationService.addListener('MONEY_EVENT', (pEvent: IMoneyEvent) => {
            let lPlayer: model.IPlayer = this.players.getElement(pEvent.idPlayer);
            if (lPlayer) {
                lPlayer.money = pEvent.money;
            }
            this.listeners.forEach((pListener: IPlayerEventListener) => {
                pListener(lPlayer);
            });
        });
        let lResolve: (e: model.MapArray<model.IPlayer>) => void = null;
        let lReject: (e: Error) => void = null;
        const lPromise: Promise<model.MapArray<model.IPlayer>> = new Promise((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject;
        });
        this.promiseGetPlayers = lPromise;
        this.communicationService.sendWithResponse('PLAYERS_GETALL').then((pResponse: IGetPlayers) => {
            pResponse.players.forEach((pPlayer: model.IPlayer) => {
                this.players.addElement(pPlayer.idPlayer, pPlayer);
            });
            lResolve(this.players);
        });
    }
    
    public addListener(pListener: IPlayerEventListener): void {
        this.listeners.push(pListener);
    }
    
    public removeListener(pListener: IPlayerEventListener): void {
        Helpers.remove(this.listeners, (pListenerCurrent: IPlayerEventListener) => {
            return pListenerCurrent === pListener;
        });
    }
    
    public getPlayer(pIdPlayer: number): Promise<model.IPlayer> {
        return this.getPlayers().then(() => {
            return this.players.getElement(pIdPlayer);
        });
    }

    public getPlayers(): Promise<model.MapArray<model.IPlayer>> {
        return this.promiseGetPlayers;
    }
}

interface IPlayerEvent {
    idPlayer: number;
    joinType: number;
    uuid: string;
    name: string;
    money: number;
}
interface IMoneyEvent {
    idPlayer: number;
    money: number;
}

export interface IPlayerEventListener {
    (pPlayer: model.IPlayer): void;
}
export interface IGetPlayers {
    players: model.IPlayer[];
}