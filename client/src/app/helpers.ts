export class Helpers {
    public static promiseSelfResolved: Promise<void> = null;

    public static remove<T>(pTab: T[], pCallback: (e: T) => boolean): T[] {
        const lNbElements: number = pTab.length;
        let lDecallage: number = 0;
        for (let i = 0; i < lNbElements; i++) {
            if (pCallback(pTab[i])) {
                lDecallage++;
            } else {
                pTab[i - lDecallage] = pTab[i];
            }
        }
        pTab.length -= lDecallage;
        return pTab;
    }

    public static promisesAll<T>(pPromises: Promise<T>[]): Promise<T[]> {
        let lResolve: (pResults: T[]) => void = null;
        let lReject: (pError: Error) => void = null;
        const lPromise: Promise<T[]> = new Promise((pResolve, pReject) => {
            lResolve = pResolve;
            lReject = pReject;
        });
        let lNbPromises = pPromises.length;
        let lNbPromisesFinished = 0;
        let lHasError: boolean = false;
        let lResults: T[] = [];
        let lCheckWhenFinish = () => {
            lNbPromisesFinished++;
            if (lHasError) {
                return;
            }
            if (lNbPromisesFinished === lNbPromises) {
                lResolve(lResults);
            }
        };
        pPromises.forEach((pPromise: Promise<T>, pIndex: number) => {
            lResults.push(null);
            pPromise.then((pResult: T) => {
                lResults[pIndex] = pResult;
                lCheckWhenFinish();
            }, (pError: Error) => {
                if (lHasError) {
                    return;
                }
                lHasError = true;
                lReject(pError);
            });
        });
        return lPromise;
    }

    public static createPromise<T>(pRunnable: (pDeferred: IDeferred<T>) => void): Promise<T> {
        let lResolve: (pResult: T) => any = null;
        let lReject: (pError: Error) => any = null;
        let lPromise = new Promise<T>((pResolve: (pResult: T) => any, pReject: (pError: Error) => any) => {
            lResolve = pResolve;
            lReject = pReject;
        });
        try {
            pRunnable({
                resolve: lResolve,
                reject: lReject,
                promise: lPromise
            });
        } catch (e) {
            lReject(e);
        }
        return lPromise;
    };

    public static createDefer<T>(): IDeferred<T> {
        let lResolve: (pResult: T) => any = null;
        let lReject: (pError: Error) => any = null;
        let lPromise = new Promise<T>((pResolve: (pResult: T) => any, pReject: (pError: Error) => any) => {
            lResolve = pResolve;
            lReject = pReject;
        });
        return {
            resolve: lResolve,
            reject: lReject,
            promise: lPromise
        };
    }
}

Helpers.promiseSelfResolved = Helpers.createPromise((pDefer: IDeferred<void>) => {
    pDefer.resolve();
});

export interface IDeferred<T> {
    resolve: (pResult?: T) => any;
    reject: (pError: Error) => any; 
    promise: Promise<T>;
}