import {Injectable} from '@angular/core';
import {CommunicationService} from '../communication/communication.service';
import {Helpers} from '../../helpers';
import * as model from '../../models';

@Injectable()
export class PositionService {

    private listeners: Array<(pPosition: model.ICoordinates) => void> = [];
    private currentPosition: model.ICoordinates = null;

    constructor(
        private communicationService: CommunicationService
    ) {
        this.communicationService.addListener('POSITION_CURRENT', this.listener);
    }

    public addListener(pListener: (pPosition: model.ICoordinates) => void): void {
        this.listeners.push(pListener);
        if (this.currentPosition) {
            pListener(this.currentPosition);
        }
    }

    public removeListener(pListener: (pPosition: model.ICoordinates) => void): void {
        Helpers.remove(this.listeners, (pListenerFromArray) => {
            return pListenerFromArray === pListener;
        });
    }

    private listener = (pMessage: IPositionUpdateResponse): void => {
        const lPosition: model.ICoordinates = {
            x: pMessage.x,
            y: pMessage.y,
            z: pMessage.z,
        };
        this.currentPosition = lPosition;
        this.listeners.forEach((pListener: (pPosition: model.ICoordinates) => void) => {
            pListener(lPosition);
        });
    }

    public setPosition(pPosition: model.ICoordinates): void {
        console.log("Set position");
        this.currentPosition = pPosition;
        this.listeners.forEach((pListener: (pPosition: model.ICoordinates) => void) => {
            pListener(pPosition);
        });
    }

    public getPosition(): model.ICoordinates {
        return this.currentPosition;
    }
}

interface IPositionUpdateResponse {
    x: number;
    y: number;
    z: number;
}
