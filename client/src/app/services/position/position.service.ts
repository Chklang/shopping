import {Injectable} from '@angular/core';
import {CommunicationService} from '../communication/communication.service';
import {Helpers} from '../../helpers';

@Injectable()
export class PositionService {

    private listeners: Array<(pPosition: IPosition) => void> = [];

    constructor(
        private communicationService: CommunicationService
    ) {
        this.communicationService.addListener('POSITION_CURRENT', this.listener);
    }

    public addListener(pListener: (pPosition: IPosition) => void): void {
        this.listeners.push(pListener);
    }

    public removeListener(pListener: (pPosition: IPosition) => void): void {
        Helpers.remove(this.listeners, (pListenerFromArray) => {
            return pListenerFromArray === pListener;
        });
    }

    private listener = (pMessage: IPositionUpdateResponse): void => {
        const lPosition: IPosition = {
            x: pMessage.x,
            y: pMessage.y,
            z: pMessage.z,
        };
        this.listeners.forEach((pListener: (pPosition: IPosition) => void) => {
            pListener(lPosition);
        });
    }
}

export interface IPosition {
    x: number;
    y: number;
    z: number;
}

interface IPositionUpdateResponse {
    x: number;
    y: number;
    z: number;
}
