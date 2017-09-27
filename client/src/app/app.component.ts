import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import * as model from './models';
import { LoadingService } from './services/loading/loading.service';
import { AlertsService, IAlert } from './services/alerts/alerts.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    public loadingInProgress: boolean = false;
    public alerts: IAlert[] = null;

    constructor(
        private changeDetectorRef: ChangeDetectorRef,
        private loadingService: LoadingService,
        private alertsService: AlertsService
    ) {
    }

    ngOnInit() {
        this.init();
    }

    public init(): void {
        this.alerts = [];
        this.alertsService.setListenerUpdates((pAlerts: IAlert[]) => {
            this.alerts = pAlerts;
            this.changeDetectorRef.detectChanges();
        });
        this.loadingService.setListenerLoading((pLoading: boolean) => {
            this.loadingInProgress = pLoading;
            this.changeDetectorRef.detectChanges();
        });
    }

    public closeAlert(pAlert: IAlert): void {
        this.alertsService.removeAlert(pAlert);
    }
}
