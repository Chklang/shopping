import { Component, TemplateRef, ChangeDetectorRef } from '@angular/core';
import { BsModalService } from 'ngx-bootstrap/modal';
import { BsModalRef } from 'ngx-bootstrap/modal/modal-options.class';

import { LogService, IGetToken, GetTokenStatus, ISendToken, SendTokenStatus } from './services/log/log.service';
import { LoadingService } from './services/loading/loading.service';

@Component( {
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
} )
export class AppComponent {
    public modalRef: BsModalRef;

    private login: string = null;
    private codeRequest: string = null;
    public ConnexionStepStatus = ConnexionStepStatus;
    public connexionStepStatus: ConnexionStepStatus = null;
    public loadingInProgress: boolean = false;
    public errorMessage: string = null;

    constructor( private lc: ChangeDetectorRef, private modalService: BsModalService, private logService: LogService, private loadingService: LoadingService ) {
        loadingService.setListenerLoading(( pLoading: boolean ) => {
            this.loadingInProgress = pLoading;
            lc.detectChanges();
        } );
        window['test'] = loadingService;
    }

    public openModal( template: TemplateRef<any> ) {
        this.login = null;
        this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
        this.modalRef = this.modalService.show( template );
    }

    public connexionStep1( pLogin: string ): void {
        this.loadingService.show();
        this.connexionStepStatus = ConnexionStepStatus.GET_TOKEN;
        this.login = pLogin;
        this.logService.getToken( pLogin ).then(( pResponse: IGetToken ) => {
            switch ( pResponse.status ) {
                case GetTokenStatus.CONNECT_FAIL:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = "Cannot connect to Minecraft server";
                    break;
                case GetTokenStatus.NO_PLAYER_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
                    this.errorMessage = "Player " + pLogin + " not found!";
                    break;
                case GetTokenStatus.PLAYER_FOUND:
                    this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                    this.codeRequest = pResponse.codeRequest;
                    this.errorMessage = null;
                    break;
            }
        }, ( pErreur: Error ) => {
            this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
            console.error( "Error : ", pErreur );
        } ).then(() => {
            this.loadingService.hide();
        } );
    }

    public connexionStep2( pCode: string ): void {
        this.loadingService.show();
        this.connexionStepStatus = ConnexionStepStatus.CHECK_TOKEN;
        this.logService.sendToken(this.codeRequest, pCode).then((pResponse: ISendToken) => {
            switch (pResponse.status) {
            case SendTokenStatus.CONNECT_FAIL :
                this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                this.errorMessage = "Cannot connect to Minecraft server";
                break;
            case SendTokenStatus.TOKEN_NOT_FOUND :
                this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                this.errorMessage = "Bad token value!";
                break;
            case SendTokenStatus.TOKEN_OK :
                this.connexionStepStatus = ConnexionStepStatus.FILL_TOKEN;
                this.errorMessage = null;
                break;
            }
        }, ( pErreur: Error ) => {
            this.connexionStepStatus = ConnexionStepStatus.FILL_LOGIN;
            console.error( "Error : ", pErreur );
        } ).then(() => {
            this.loadingService.hide();
            this.modalRef.hide();
        });
    }
}

export enum ConnexionStepStatus {
    FILL_LOGIN,
    GET_TOKEN,
    FILL_TOKEN,
    CHECK_TOKEN
}
