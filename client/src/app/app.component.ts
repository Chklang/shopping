import {Component, TemplateRef, ChangeDetectorRef} from '@angular/core';
import {BsModalService} from 'ngx-bootstrap/modal';
import {BsModalRef} from 'ngx-bootstrap/modal/modal-options.class';

import {LogService, IGetToken, GetTokenStatus, ISendToken, SendTokenStatus} from './services/log/log.service';
import {LoadingService} from './services/loading/loading.service';
import {PositionService, IPosition} from './services/position/position.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    public modalRef: BsModalRef;

    public login: string = null;
    public ConnexionStepStatus = ConnexionStepStatus;
    public connexionStepStatus: ConnexionStepStatus = null;
    public loadingInProgress: boolean = false;
    public errorMessage: string = null;
    public currentPosition: IPosition = {
        x: 0,
        y: 0,
        z: 0
    };

    constructor(
        private changeDetectorRef: ChangeDetectorRef,
        private modalService: BsModalService,
        private logService: LogService,
        private loadingService: LoadingService,
        private positionService: PositionService
    ) {
        this.init();
        window['test'] = loadingService;
    }

    public openModal(template: TemplateRef<any>) {
        this.login = null;
        this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
        this.modalRef = this.modalService.show(template);
    }

    public init(): Promise<void> {
        this.loadingService.setListenerLoading((pLoading: boolean) => {
            this.loadingInProgress = pLoading;
            this.changeDetectorRef.detectChanges();
        });
        this.positionService.addListener((pPosition: IPosition) => {
            this.currentPosition.x = Math.trunc(pPosition.x);
            this.currentPosition.y = Math.trunc(pPosition.y);
            this.currentPosition.z = Math.trunc(pPosition.z);
            this.changeDetectorRef.detectChanges();
        });
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

    public connexionStep1(pLogin: string): void {
        this.loadingService.show();
        this.connexionStepStatus = ConnexionStepStatus.GET_TOKEN;
        this.logService.getToken(pLogin).then((pResponse: IGetToken) => {
            switch (pResponse.status) {
                case GetTokenStatus.CONNECT_FAIL:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = 'Cannot connect to Minecraft server';
                    break;
                case GetTokenStatus.NO_PLAYER_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = 'Player ' + pLogin + ' not found!';
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
