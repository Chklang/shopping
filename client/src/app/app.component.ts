import {Component, ChangeDetectorRef} from '@angular/core';
import {BsModalService} from 'ngx-bootstrap/modal';
import {BsModalRef} from 'ngx-bootstrap/modal/modal-options.class';

import * as model from './models';
import {LogService, IGetToken, GetTokenStatus, ISendToken, SendTokenStatus} from './services/log/log.service';
import {LoadingService} from './services/loading/loading.service';
import {PositionService, IPosition} from './services/position/position.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent {
    public loadingInProgress: boolean = false;
    
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

    public init(): void {
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
    }
}
