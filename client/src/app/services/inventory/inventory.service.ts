import { Injectable } from '@angular/core';
import { CommunicationService } from '../communication/communication.service';
import { LogService } from '../log/log.service';
import { TrService } from '../tr/tr.service';
import { Helpers } from '../../helpers';

import * as model from '../../models';

@Injectable()
export class InventoryService {

  private listeners: IPlayerItemListener[] = [];
  private inventory: model.MapArray<model.IPlayerItem> = null;

  constructor(
    private communicationService: CommunicationService,
    private logService: LogService,
    private trService: TrService
  ) {
    this.communicationService.addListener('PLAYER_INVENTORY', (pEvent: IPlayerInventoryEvent) => {
      if (!this.inventory) {
        return;
      }
      const lPromises: Promise<void>[] = [];
      let lItemsUpdated: model.IPlayerItem[] = [];
      pEvent.items.forEach((pItem: IPlayerInventoryElementEvent) => {
        const lId: string = pItem.idItem + '_' + pItem.subIdItem;
        let lItem: model.IPlayerItem = this.inventory.getElement(lId);
        if (!lItem) {
          if (pItem.quantity === 0) {
            //No elements, ignore it
            return;
          }
          lItem = {
            idItem: pItem.idItem,
            subIdItem: pItem.subIdItem,
            name: null,
            quantity: pItem.quantity
          };
          lPromises.push(this.trService.getText(pItem.name).then((pNameValue: string) => {
            if (pItem.nameDetails) {
              pNameValue += ' (' + pItem.nameDetails + ')';
            }
            lItem.name = pNameValue;
            this.inventory.addElement(lId, lItem);
          }));
        } else {
          if (pItem.quantity === 0) {
            this.inventory.removeElement(lId);
          }
          lItem.quantity = pItem.quantity;
        }
        lItemsUpdated.push(lItem);
      });
      Helpers.promisesAll(lPromises).then(() => {
        this.listeners.forEach((pListener: IPlayerItemListener) => {
          pListener(this.inventory, lItemsUpdated);
        });
      });
    });
    this.logService.getCurrentIdPlayer().then((pCurrentIdPlayer: number) => {
      this.initInventory(pCurrentIdPlayer);
    });
  }

  private initInventory(pIdPlayer: number): void {
    const lInventory: model.MapArray<model.IPlayerItem> = new model.MapArray();
    this.communicationService.sendWithResponse('PLAYERS_GET_INVENTORY', <IPlayerInventoryRequest>{
      idPlayer: pIdPlayer
    }).then((pResponse: IPlayerInventoryResponse) => {
      if (!pResponse.isOk) {
        throw new Error('PLAYERS_GET_INVENTORY not ok');
      }
      const lPromises: Promise<void>[] = [];
      pResponse.items.forEach((pItem: IPlayerInventoryElementResponse) => {
        const lItem: model.IPlayerItem = {
          idItem: pItem.idItem,
          subIdItem: pItem.subIdItem,
          name: null,
          quantity: pItem.quantity
        };
        lPromises.push(this.trService.getText(pItem.name).then((pNameValue: string) => {
          if (pItem.nameDetails) {
            pNameValue += ' (' + pItem.nameDetails + ')';
          }
          lItem.name = pNameValue;
          lInventory.addElement(lItem.idItem + '_' + lItem.subIdItem, lItem);
        }));
      });
      return Helpers.promisesAll(lPromises);
    }).then(() => {
      this.inventory = lInventory;
      this.listeners.forEach((pListener: IPlayerItemListener) => {
        pListener(this.inventory, this.inventory);
      });
    });
  }

  public addListenerInventory(pListener: IPlayerItemListener): void {
    this.listeners.push(pListener);
    if (this.inventory) {
      pListener(this.inventory, this.inventory);
    }
  }

  public removeListenerInventory(pListener: IPlayerItemListener): void {
    Helpers.remove(this.listeners, (pListenerInList: IPlayerItemListener): boolean => {
      return pListener === pListenerInList;
    });
  }
}
interface IPlayerInventoryEvent {
  items: IPlayerInventoryElementEvent[];
}
interface IPlayerInventoryElementEvent {
  idItem: number;
  subIdItem: number;
  quantity: number;
  name: string;
  nameDetails: string;
}
interface IPlayerInventoryRequest {
  idPlayer: number;
}
interface IPlayerInventoryResponse {
  isOk: boolean;
  items?: IPlayerInventoryElementResponse[];
}
interface IPlayerInventoryElementResponse {
  idItem: number;
  subIdItem: number;
  quantity: number;
  name: string;
  nameDetails: string;
}

export type IPlayerItemListener = (pItems: model.MapArray<model.IPlayerItem>, pItemsUpdated: model.IPlayerItem[]) => void;