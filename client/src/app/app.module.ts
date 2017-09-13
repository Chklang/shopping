import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { ModalModule } from 'ngx-bootstrap/modal';
import { CookieService } from 'angular2-cookie/services/cookies.service';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AllshopsComponent } from './allshops/allshops.component';
import { YoursshopsComponent } from './yoursshops/yoursshops.component';
import { InventoryComponent } from './inventory/inventory.component';

import { LogService } from './services/log/log.service';
import { LoadingService } from './services/loading/loading.service';
import { CommunicationService } from './services/communication/communication.service';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AllshopsComponent,
    YoursshopsComponent,
    InventoryComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    ModalModule.forRoot()
  ],
  providers: [LogService, LoadingService, CookieService, CommunicationService],
  bootstrap: [AppComponent]
})
export class AppModule { }