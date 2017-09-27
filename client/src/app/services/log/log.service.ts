import {Injectable} from '@angular/core';
import {CookieService} from 'angular2-cookie/core';
import {CommunicationService} from '../communication/communication.service';
import {PositionService} from '../position/position.service';

import {Helpers, IDeferred} from '../../helpers';
import * as model from '../../models';

@Injectable()
export class LogService {

    private deferredCurrentPlayer: IDeferred<number> = null;

    public constructor(
        private communicationService: CommunicationService,
        private cookieService: CookieService,
        private positionService: PositionService
    ) {
        this.deferredCurrentPlayer = Helpers.createDefer();
    }

    public getCurrentIdPlayer(): Promise<number> {
        return this.deferredCurrentPlayer.promise;
    }

    public getToken(pIdPlayer: number): Promise<IGetToken> {
        return this.communicationService.sendWithResponse('LOGIN_GET_TOKEN', <IGetTokenRequest>{
            idPlayer: pIdPlayer
        }).then((pReponse: IGetTokenResponse): IGetToken => {
            if (!pReponse.playerFound) {
                return {
                    status: GetTokenStatus.NO_PLAYER_FOUND
                };
            } else {
                return {
                    status: GetTokenStatus.PLAYER_FOUND
                };
            }
        });
    }

    public sendToken(pValue: string): Promise<ISendToken> {
        return this.communicationService.sendWithResponse('LOGIN_SEND_TOKEN', <ISendTokenRequest>{
            key: pValue
        }).then((pReponse: ISendTokenResponse): ISendToken => {
            if (!pReponse.keyIsOk) {
                return {
                    pseudo: null,
                    status: SendTokenStatus.TOKEN_NOT_FOUND
                };
            } else {
                const expires: Date = new Date();
                expires.setTime(expires.getTime() + 1000 * 3600 * 24 * 30); // Add 30 days
                this.cookieService.put('tokenconnexion', pReponse.token, {
                    expires: expires
                });
                this.positionService.setPosition({
                    x: pReponse.x,
                    y: pReponse.y,
                    z: pReponse.z
                });
                this.deferredCurrentPlayer.resolve(pReponse.idPlayer);
                return {
                    status: SendTokenStatus.TOKEN_OK,
                    pseudo: pReponse.pseudo
                };
            }
        });
    }

    public checkConnexion(): Promise<string> {
        const lToken: string = this.cookieService.get('tokenconnexion');
        return this.communicationService.sendWithResponse('LOGIN_CHECK', {
            token: lToken
        }).then((pReponse: ICheckConnexionResponse): string => {
            if (pReponse.tokenIsOk) {
                this.positionService.setPosition({
                    x: pReponse.x,
                    y: pReponse.y,
                    z: pReponse.z
                });
                this.deferredCurrentPlayer.resolve(pReponse.idPlayer);
                return pReponse.pseudo;
            } else {
                this.deferredCurrentPlayer.resolve(null);
                return null;
            }
        });
    }

    public logout(): Promise<void> {
        return this.communicationService.sendWithResponse<void>('LOGIN_LOGOUT', <ILogoutRequest>{
        }).then(() => {
            window.location.reload();
        });
    }
}

export enum GetTokenStatus {
    CONNECT_FAIL,
    NO_PLAYER_FOUND,
    PLAYER_FOUND
}
export enum SendTokenStatus {
    CONNECT_FAIL,
    TOKEN_NOT_FOUND,
    TOKEN_OK
}

export interface IGetToken {
    status: GetTokenStatus;
}

export interface ISendToken {
    status: SendTokenStatus;
    pseudo: string;
}

interface IGetTokenRequest {
    idPlayer: number;
}

interface IGetTokenResponse {
    playerFound: boolean;
}
interface ISendTokenRequest {
    key: string;
}
interface ISendTokenResponse {
    keyIsOk: boolean;
    token?: string;
    idPlayer: number;
    pseudo?: string;
    x:number;
    y:number;
    z:number;
    isOp: boolean;
}
interface ICheckConnexion {
    pseudo: string;
}
interface ICheckConnexionRequest {
    token: string;
}
interface ICheckConnexionResponse {
    tokenIsOk: string;
    idPlayer: number;
    pseudo?: string;
    x:number;
    y:number;
    z:number;
    isOp: boolean;
}
interface ILogoutRequest {
}
interface ILogoutResponse {
}
