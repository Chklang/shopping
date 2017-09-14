export class Helpers {

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
}
