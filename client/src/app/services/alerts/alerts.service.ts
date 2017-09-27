import { Injectable } from '@angular/core';

import {Helpers} from '../../helpers';

@Injectable()
export class AlertsService {

  private alerts: IAlert[] = [];
  private listenerUpdates: (pAlerts: IAlert[]) => void = null;

  constructor() { }

  public setListenerUpdates(pListener: (pAlerts: IAlert[]) => void): void {
    this.listenerUpdates = pListener;
  }

  private show(pLevel: LEVEL, pMessage: string, pAutoclose: boolean): void {
    let lLevelString = '';
    switch (pLevel) {
      case LEVEL.SUCESS:
        lLevelString = 'success';
        break;
      case LEVEL.WARN:
        lLevelString = 'warning';
        break;
      case LEVEL.ERROR:
        lLevelString = 'danger';
        break;
    }
    let lAlert: IAlert = {
      level: lLevelString,
      message: pMessage
    };
    this.alerts.push(lAlert);
    if (pAutoclose) {
      window.setTimeout(() => {
        this.removeAlert(lAlert);
      }, 2000);
    }
    if (this.listenerUpdates) {
      this.listenerUpdates(this.alerts);
    }
  }

  public success(pMessage: string, pAutoclose: boolean = true): void {
    this.show(LEVEL.SUCESS, pMessage, pAutoclose);
  }

  public warn(pMessage: string, pAutoclose: boolean = false): void {
    this.show(LEVEL.WARN, pMessage, pAutoclose);
  }

  public error(pMessage: string, pAutoclose: boolean = false): void {
    this.show(LEVEL.ERROR, pMessage, pAutoclose);
  }

  public removeAlert(pAlert: IAlert): void {
    Helpers.remove(this.alerts, (pAlertInList: IAlert) => {
      return pAlert === pAlertInList;
    });
    if (this.listenerUpdates) {
      this.listenerUpdates(this.alerts);
    }
  }
}

export interface IAlert {
  level: string;
  message: string;
}
export enum LEVEL {
  SUCESS,
  WARN,
  ERROR
}