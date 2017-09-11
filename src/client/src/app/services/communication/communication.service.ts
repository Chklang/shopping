import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {Observer} from 'rxjs/Observer';
import {Subject} from 'rxjs/Subject';

@Injectable()
export class CommunicationService {

    private isOpen: boolean = false;
    private writter: (pMessage: IMessage<any>) => void = null;
    private listeners: {[key: string]: Array<(pMessage: any) => void>} = {};

    constructor() {}

    private checkInit(): void {
        if (!this.isOpen) {
            this.init();
        }
    }

    private init(): void {
        let ws = new WebSocket('/ws/');
        this.writter = (pMessage: IMessage<any>): void => {
            ws.send(JSON.stringify(pMessage));
        };
        ws.onclose = () => {
            this.writter = null;
            this.isOpen = false;
        };
        ws.onmessage = (pEvent: MessageEvent) => {
            let lMessage: IMessage<any> = pEvent.data;
            if (this.listeners[lMessage.type]) {
                this.listeners[lMessage.type].forEach((pListener: (pMessage: any) => void) => {
                    pListener(lMessage.content);
                });
            }
        };
        ws.onerror = (pError: Event) => {
            console.error("Error was occured", pError);
        };
        this.isOpen = true;
    }

    public send(pType: string, pContent: any): void {
        this.checkInit();
        this.writter({
            type: pType,
            content: pContent
        });
    }
}

interface IMessage<T> {
    type: string;
    isReply?: boolean;
    answerId?: string;
    content: T;
}