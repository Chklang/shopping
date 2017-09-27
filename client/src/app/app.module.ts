import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ModalModule } from 'ngx-bootstrap/modal';
import { CookieService } from 'angular2-cookie/services/cookies.service';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { AlertModule } from 'ngx-bootstrap/alert';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { ShopsComponent } from './shops/shops.component';
import { InventoryComponent } from './inventory/inventory.component';
import { IdentificationComponent } from './identification/identification.component';
import { StatusbarComponent } from './statusbar/statusbar.component';
import { ShopsdetailsComponent } from './shopsdetails/shopsdetails.component';

import { LogService } from './services/log/log.service';
import { LoadingService } from './services/loading/loading.service';
import { CommunicationService } from './services/communication/communication.service';
import { PositionService } from './services/position/position.service';
import { PlayersService } from './services/players/players.service';
import { ShopsService } from './services/shops/shops.service';
import { DistanceService } from './services/distance/distance.service';
import { TrService } from './services/tr/tr.service';
import { AlertsService } from './services/alerts/alerts.service';

const appRoutes: Routes = [
    {
        path: 'home',
        component: HomeComponent
    },
    {
        path: 'shops/:mode',
        component: ShopsComponent
    },
    {
        path: 'shops/:mode/:idShop',
        component: ShopsdetailsComponent
    },
    {
        path: 'inventory',
        component: InventoryComponent
    }
];

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        ShopsComponent,
        InventoryComponent,
        IdentificationComponent,
        StatusbarComponent,
        ShopsdetailsComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        ModalModule.forRoot(),
        RouterModule.forRoot(appRoutes),
        PaginationModule.forRoot(),
        AlertModule.forRoot()
    ],
    providers: [LogService, LoadingService, CookieService, CommunicationService, PositionService, PlayersService, ShopsService, DistanceService, TrService, AlertsService],
    bootstrap: [AppComponent]
})
export class AppModule { }
