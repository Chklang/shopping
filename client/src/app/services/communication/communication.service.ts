import {Injectable} from '@angular/core';
import {Jsonp} from '@angular/http';
import {Observable} from 'rxjs/Observable';
import {Observer} from 'rxjs/Observer';
import {Subject} from 'rxjs/Subject';
import {Helpers} from '../../helpers';

@Injectable()
export class CommunicationService {

    private isOpen: boolean = false;
    private writter: (pMessage: IMessage<any>) => void = null;
    private listeners: {[key: string]: Array<(pMessage: any) => void>} = {};
    private answers: {[key: string]: (pResponse: any) => void} = {};

    private waitingMessages: IMessage<any>[] = [];

    constructor() {}

    private checkInit(): void {
        if (!this.isOpen) {
            this.init();
        }
    }

    private init(): void {
        const ws = new WebSocket('ws://' + location.host + '/ws/');
        ws.onopen = () => {
            this.writter = (pMessage: IMessage<any>): void => {
                ws.send(JSON.stringify(pMessage));
            };
            this.waitingMessages.forEach(this.writter);
            this.waitingMessages.length = 0;
        };
        ws.onclose = () => {
            this.writter = null;
            this.isOpen = false;
        };
        ws.onmessage = (pEvent: MessageEvent) => {
            const lMessage: IMessage<any> = JSON.parse(pEvent.data);
            if (lMessage.isReply) {
                if (!this.answers[lMessage.answerId]) {
                    console.error('No reply id : ' + lMessage.answerId);
                    return;
                }
                const lListener = this.answers[lMessage.answerId];
                delete this.answers[lMessage.answerId];
                lListener(lMessage.content);
            } else {
                if (this.listeners[lMessage.type]) {
                    this.listeners[lMessage.type].forEach((pListener: (pMessage: any) => void) => {
                        pListener(lMessage.content);
                    });
                }
            }
        };
        ws.onerror = (pError: Event) => {
            console.error('Error was occured', pError);
        };
        this.isOpen = true;
    }

    private sendMessage(pMessage: IMessage<any>): void {
        if (this.writter) {
            this.writter(pMessage);
        } else {
            this.waitingMessages.push(pMessage);
        }
    }

    public send(pType: string, pContent: any): void {
        this.checkInit();
        this.sendMessage({
            type: pType,
            content: pContent
        });
    }

    public sendWithResponse<T>(pType: string, pContent: any): Promise<T> {
        this.checkInit();
        let lId = null;
        do {
            lId = Math.random().toString(36).substring(2);
        } while (this.answers[lId] === null);
        const lPromise: Promise<T> = new Promise<T>((pResolve, pReject) => {
            this.answers[lId] = pResolve;
        });
        this.sendMessage({
            type: pType,
            answerId: lId,
            content: pContent
        });
        return lPromise;
    }

    public addListener<T>(pType: string, pListener: (pMessage: T) => void): void {
        if (!this.listeners[pType]) {
            this.listeners[pType] = [];
        }
        this.listeners[pType].push(pListener);
    }

    public removeListener<T>(pType: string, pListener: (pMessage: T) => void): void {
        if (!this.listeners[pType]) {
            return;
        }
        Helpers.remove(this.listeners[pType], (pListenerInList) => {
            return pListenerInList === pListener;
        });
    }
}

interface IMessage<T> {
    type: string;
    isReply?: boolean;
    answerId?: string;
    content: T;
}
