import {Injectable} from '@angular/core';
import {CookieService} from 'angular2-cookie/core';
import {CommunicationService} from '../communication/communication.service';

@Injectable()
export class LogService {

    public constructor(private communicationService: CommunicationService, private cookieService: CookieService) {

    }

    public getToken(pLogin: string): Promise<IGetToken> {
        return this.communicationService.sendWithResponse("LOGIN_GET_TOKEN", <IGetTokenRequest>{
            pseudo: pLogin
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
        return this.communicationService.sendWithResponse("LOGIN_SEND_TOKEN", <ISendTokenRequest>{
            key: pValue
        }).then((pReponse: ISendTokenResponse): ISendToken => {
            if (!pReponse.keyIsOk) {
                return {
                    token: null,
                    status: SendTokenStatus.TOKEN_NOT_FOUND
                };
            } else {
                let expires: Date = new Date();
                expires.setTime(expires.getTime() + 1000 * 3600 * 24 * 30);//Add 30 days
                this.cookieService.put("tokenconnexion", pReponse.token, {
                    expires: expires
                });
                return {
                    status: SendTokenStatus.TOKEN_OK,
                    token: pReponse.token
                };
            }
        });
    }

    public checkConnexion(): Promise<string> {
        let lToken: string = this.cookieService.get("tokenconnexion");
        return this.communicationService.sendWithResponse("LOGIN_CHECK", {
            token: lToken
        }).then((pReponse: ICheckConnexionResponse): string => {
            if (pReponse.tokenIsOk) {
                return pReponse.pseudo;
            } else {
                return null;
            }
        });
    }
    
    public logout(): Promise<void> {
        return this.communicationService.sendWithResponse<void>("LOGIN_LOGOUT", <ILogoutRequest>{
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
    token: string;
}

interface IGetTokenRequest {
    pseudo: string;
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
}
interface ICheckConnexion {
    pseudo: string;
}
interface ICheckConnexionRequest {
    token: string;
}
interface ICheckConnexionResponse {
    tokenIsOk: string;
    pseudo?: string;
}
interface ILogoutRequest {
}
interface ILogoutResponse {
}
