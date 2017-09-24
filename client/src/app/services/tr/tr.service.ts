import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import 'rxjs/add/operator/map';

import { Helpers, IDeferred } from '../../helpers';

@Injectable()
export class TrService {

  private promiseLoad: Promise<{ [key: string]: string }> = null;

  constructor(
    private httpClient: HttpClient
  ) {
    this.promiseLoad = Helpers.createPromise((pDefer: IDeferred<{[key: string]: string}>) => {
      this.httpClient.get('/assets/tr/fr.json').subscribe((pData: {[key: string]:string}) => {
        pDefer.resolve(pData);
      });
    });
  }

  public getText(pKey: string): Promise<string> {
    return this.promiseLoad.then((pKeys: { [key: string]: string }) => {
      return pKeys[pKey];
    });
  }

}
