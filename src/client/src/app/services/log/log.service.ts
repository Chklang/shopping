import {Injectable} from '@angular/core';
import {CookieService} from 'angular2-cookie/core';
import {CommunicationService} from '../communication/communication.service';

@Injectable()
export class LogService {

    public constructor(private communicationService: CommunicationService, private cookieService: CookieService) {

    }

    public getToken(pLogin: string): Promise<IGetToken> {
        let lResolve = null;
        let lReject = null;
        let lPromise: Promise<IGetToken> = new Promise<IGetToken>((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject
        });
        this.httpClient.post<IGetTokenResponse>('/rest/login/getcode', {
            pseudo: pLogin
        }).subscribe((pData: IGetTokenResponse) => {
            if (!pData.playerFound) {
                lResolve({
                    status: GetTokenStatus.NO_PLAYER_FOUND
                });
            } else {
                lResolve({
                    status: GetTokenStatus.PLAYER_FOUND,
                    codeRequest: pData.codeRequest
                });
            }
        }, (pErreur) => {
            if (pErreur && pErreur.status === 500) {
                lReject(pErreur);
                return;
            }
            lResolve({
                status: GetTokenStatus.CONNECT_FAIL
            });
        });
        return lPromise;
    }

    public sendToken(pTokenRequest: string, pValue: string): Promise<ISendToken> {
        let lResolve = null;
        let lReject = null;
        let lPromise: Promise<ISendToken> = new Promise<ISendToken>((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject
        });
        this.httpClient.post<ISendTokenResponse>('/rest/login/sendcode', {
            token: pTokenRequest,
            key: pValue
        }).subscribe((pData: ISendTokenResponse) => {
            if (!pData.tokenFound) {
                lResolve({
                    status: SendTokenStatus.TOKEN_NOT_FOUND
                });
            } else {
                let expires: Date = new Date();
                expires.setTime(expires.getTime() + 1000 * 3600 * 24 * 30);//Add 30 days
                this.cookieService.put("tokenconnexion", pValue, {
                    expires: expires
                });
                lResolve({
                    status: SendTokenStatus.TOKEN_OK,
                    token: pData.token
                });
            }
        }, (pErreur) => {
            if (pErreur && pErreur.status === 500) {
                lReject(pErreur);
                return;
            }
            lResolve({
                status: SendTokenStatus.CONNECT_FAIL
            });
        });
        return lPromise;
    }

    public checkLogin(): Promise<boolean> {
        let lToken: string = this.cookieService.get("tokenconnexion");
        let lResolve = null;
        let lReject = null;
        let lPromise: Promise<boolean> = new Promise<boolean>((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject
        });
        this.httpClient.post('/rest/login/sendcode', {
            token: lToken
        }).subscribe(() => {
            lResolve(true);
        }, () => {
            lResolve(false);
        });
        return lPromise;
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
    codeRequest?: string;
}

export interface ISendToken {
    status: SendTokenStatus;
    token: string;
}

interface IGetTokenResponse {
    playerFound: boolean;
    codeRequest: string;
}
interface ISendTokenResponse {
    tokenFound: boolean;
    token?: string;
}