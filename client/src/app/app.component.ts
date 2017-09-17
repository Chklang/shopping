import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import * as model from './models';
import { LoadingService } from './services/loading/loading.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    public loadingInProgress: boolean = false;

    constructor(
        private changeDetectorRef: ChangeDetectorRef,
        private loadingService: LoadingService
    ) {
    }

    ngOnInit() {
        this.init();
    }

    public init(): void {
        this.loadingService.setListenerLoading((pLoading: boolean) => {
            this.loadingInProgress = pLoading;
            this.changeDetectorRef.detectChanges();
        });
    }
}
