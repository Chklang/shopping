import { Injectable } from '@angular/core';

@Injectable()
export class LoadingService {
    
    private listener: (pLoading: boolean) => void = null;
    
    public constructor () {
        
    }
    
    public setListenerLoading(pListener: (pLoading: boolean) => void): void {
        this.listener = pListener;
    }
    
    public show(): void {
        this.listener(true);
    }
    
    public hide(): void {
        this.listener(false);
    }

}