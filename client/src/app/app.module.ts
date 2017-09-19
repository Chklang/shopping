import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ModalModule } from 'ngx-bootstrap/modal';
import { CookieService } from 'angular2-cookie/services/cookies.service';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AllshopsComponent } from './allshops/allshops.component';
import { YoursshopsComponent } from './yoursshops/yoursshops.component';
import { InventoryComponent } from './inventory/inventory.component';
import { IdentificationComponent } from './identification/identification.component';
import { StatusbarComponent } from './statusbar/statusbar.component';

import { LogService } from './services/log/log.service';
import { LoadingService } from './services/loading/loading.service';
import { CommunicationService } from './services/communication/communication.service';
import { PositionService } from './services/position/position.service';
import { PlayersService } from './services/players/players.service';
import { ShopsService } from './services/shops/shops.service';
import { DistanceService } from './services/distance/distance.service';
import { AllshopsdetailsComponent } from './allshopsdetails/allshopsdetails.component';

const appRoutes: Routes = [
    {
        path: 'home',
        component: HomeComponent
    },
    {
        path: 'allshops',
        component: AllshopsComponent
    },
    {
        path: 'allshops/:id',
        component: AllshopsdetailsComponent
    },
    {
        path: 'yoursshops',
        component: YoursshopsComponent
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
        AllshopsComponent,
        YoursshopsComponent,
        InventoryComponent,
        IdentificationComponent,
        StatusbarComponent,
        AllshopsdetailsComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpClientModule,
        ModalModule.forRoot(),
        RouterModule.forRoot(appRoutes)
    ],
    providers: [LogService, LoadingService, CookieService, CommunicationService, PositionService, PlayersService, ShopsService, DistanceService],
    bootstrap: [AppComponent]
})
export class AppModule { }
