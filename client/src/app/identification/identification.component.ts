import {Component, OnInit, TemplateRef, ChangeDetectorRef} from '@angular/core';
import {BsModalService} from 'ngx-bootstrap/modal';
import {BsModalRef} from 'ngx-bootstrap/modal/modal-options.class';

import * as model from '../models';
import {LogService, IGetToken, GetTokenStatus, ISendToken, SendTokenStatus, IGetPlayers} from '../services/log/log.service';
import {LoadingService} from '../services/loading/loading.service';

@Component({
    selector: 'app-identification',
    templateUrl: './identification.component.html',
    styleUrls: ['./identification.component.css']
})
export class IdentificationComponent implements OnInit {
    public login: string = null;
    public modalRef: BsModalRef;
    public ConnexionStepStatus = ConnexionStepStatus;
    public connexionStepStatus: ConnexionStepStatus = null;
    public errorMessage: string = null;
    public onlineplayers: model.IPlayer[] = null;
    public offlineplayers: model.IPlayer[] = null;

    public constructor(
        private changeDetectorRef: ChangeDetectorRef,
        private modalService: BsModalService,
        private logService: LogService,
        private loadingService: LoadingService,
    ) {
    }

    ngOnInit() {
        return this.logService.checkConnexion().then((pPseudo: string) => {
            if (pPseudo !== null) {
                this.login = pPseudo;
                console.log('Déjà connecté avec le pseudo ' + pPseudo);
            } else {
                this.login = null;
                console.log('Non connecté');
            }
            this.changeDetectorRef.detectChanges();
        });
    }

    public openModal(template: TemplateRef<any>) {
        this.login = null;
        this.onlineplayers = null;
        this.offlineplayers = null;
        this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
        this.modalRef = this.modalService.show(template);
        this.loadingService.show();
        this.logService.getPlayers().then((pPlayers: IGetPlayers) => {
            this.onlineplayers = [];
            this.offlineplayers = [];
            pPlayers.players.forEach((pPlayer) => {
                if (pPlayer.isOnline) {
                    this.onlineplayers.push(pPlayer);
                } else {
                    this.offlineplayers.push(pPlayer);
                }
            });
            this.loadingService.hide();
        });
    }

    public connexionStep1(pIdPlayer: number): void {
        this.loadingService.show();
        this.connexionStepStatus = ConnexionStepStatus.GET_TOKEN;
        this.logService.getToken(pIdPlayer).then((pResponse: IGetToken) => {
            switch (pResponse.status) {
                case GetTokenStatus.CONNECT_FAIL:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = 'Cannot connect to Minecraft server';
                    break;
                case GetTokenStatus.NO_PLAYER_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = 'Player with id ' + pIdPlayer + ' not found!';
                    break;
                case GetTokenStatus.PLAYER_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                    this.errorMessage = null;
                    break;
            }
        }, (pErreur: Error) => {
            this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
            console.error('Error : ', pErreur);
        }).then(() => {
            this.loadingService.hide();
        });
    }

    public connexionStep2(pCode: string): void {
        this.loadingService.show();
        this.connexionStepStatus = ConnexionStepStatus.CHECK_TOKEN;
        this.logService.sendToken(pCode).then((pResponse: ISendToken) => {
            switch (pResponse.status) {
                case SendTokenStatus.CONNECT_FAIL:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                    this.errorMessage = 'Cannot connect to Minecraft server';
                    break;
                case SendTokenStatus.TOKEN_NOT_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                    this.errorMessage = 'Bad token value!';
                    break;
                case SendTokenStatus.TOKEN_OK:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                    this.errorMessage = null;
                    this.login = pResponse.pseudo;
                    this.modalRef.hide();
                    break;
            }
        }, (pErreur: Error) => {
            this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
            console.error('Error : ', pErreur);
        }).then(() => {
            this.loadingService.hide();
        });
    }

    public logout(): void {
        this.loadingService.show();
        this.logService.logout().then(() => {
            this.login = null;
            this.loadingService.hide();
        });
    }
}

export enum ConnexionStepStatus {
    FILL_LOGIN,
    GET_TOKEN,
    FILL_TOKEN,
    CHECK_TOKEN
}
