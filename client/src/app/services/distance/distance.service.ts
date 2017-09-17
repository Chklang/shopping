import { Injectable } from '@angular/core';

import * as Model from '../../models';

@Injectable()
export class DistanceService {

  constructor() { }

  public calculate(pShops: Model.IShop[], pCoordinates: Model.ICoordinates): Model.IShopDistance[] {
    const lResults: Model.IShopDistance[] = [];
    pShops.forEach((pShop: Model.IShop) => {
      lResults.push(<Model.IShopDistance>{
        shop: pShop,
        distance: this.calculateShop(pShop, pCoordinates)
      });
    });
    lResults.sort((pShop1: Model.IShopDistance, pShop2: Model.IShopDistance): number => {
      return pShop1.distance - pShop2.distance;
    });
    return lResults;
  }

  public calculateShop(pShop: Model.IShop, pCoordinates: Model.ICoordinates): number {
    if (pShop.xmin <= pCoordinates.x && pCoordinates.x <= pShop.xmax &&
      pShop.ymin <= pCoordinates.y && pCoordinates.y <= pShop.ymax &&
      pShop.zmin <= pCoordinates.z && pCoordinates.z <= pShop.zmax) {
      // Inside cube
      return 0;
    }
    if (pShop.xmin <= pCoordinates.x && pCoordinates.x <= pShop.xmax &&
      pShop.ymin <= pCoordinates.y && pCoordinates.y <= pShop.ymax) {
      if (pCoordinates.z < pShop.zmin) {
        // Face Z-
        return Math.abs(pCoordinates.z - pShop.zmin);
      } else {
        // Face Z+
        return Math.abs(pCoordinates.z - pShop.zmax);
      }
    }
    if (pShop.xmin <= pCoordinates.x && pCoordinates.x <= pShop.xmax &&
      pShop.zmin <= pCoordinates.z && pCoordinates.z <= pShop.zmax) {
      if (pCoordinates.y < pShop.ymin) {
        // Face Y-
        return Math.abs(pCoordinates.y - pShop.ymin);
      } else {
        // Face Y+
        return Math.abs(pCoordinates.y - pShop.ymax);
      }
    }
    if (pShop.zmin <= pCoordinates.z && pCoordinates.z <= pShop.zmax &&
      pShop.ymin <= pCoordinates.y && pCoordinates.y <= pShop.ymax) {
      if (pCoordinates.x < pShop.xmin) {
        // Face X-
        return Math.abs(pCoordinates.x - pShop.xmin);
      } else {
        // Face X+
        return Math.abs(pCoordinates.x - pShop.xmax);
      }
    }
    if (pShop.xmin <= pCoordinates.x && pCoordinates.x <= pShop.xmax) {
      if (pCoordinates.y < pShop.ymin) {
        if (pCoordinates.z < pShop.zmin) {
          // Arrete Y-/Z-
          return Math.sqrt(Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Arrete Y-/Z+
          return Math.sqrt(Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      } else {
        if (pCoordinates.z < pShop.zmin) {
          // Arrete Y+/Z-
          return Math.sqrt(Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Arrete Y+/Z+
          return Math.sqrt(Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      }
    }
    if (pShop.ymin <= pCoordinates.y && pCoordinates.y <= pShop.ymax) {
      if (pCoordinates.x < pShop.xmin) {
        if (pCoordinates.z < pShop.zmin) {
          // Arrete X-/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Arrete X-/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      } else {
        if (pCoordinates.z < pShop.zmin) {
          // Arrete X+/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Arrete X+/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      }
    }
    if (pShop.zmin <= pCoordinates.z && pCoordinates.z <= pShop.zmax) {
      if (pCoordinates.x < pShop.xmin) {
        if (pCoordinates.y < pShop.ymin) {
          // Arrete X-/Y-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2));
        } else {
          // Arrete X-/Y+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2));
        }
      } else {
        if (pCoordinates.y < pShop.ymin) {
          // Arrete X+/Y-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2));
        } else {
          // Arrete X+/Y+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2));
        }
      }
    }
    if (pCoordinates.x < pShop.xmin) {
      if (pCoordinates.y < pShop.ymin) {
        if (pCoordinates.z < pShop.zmin) {
          // Sommet X-/Y-/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Sommet X-/Y-/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      } else {
        if (pCoordinates.z < pShop.zmin) {
          // Sommet X-/Y+/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Sommet X-/Y+/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmin, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      }
    } else {
      if (pCoordinates.y < pShop.ymin) {
        if (pCoordinates.z < pShop.zmin) {
          // Sommet X+/Y-/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Sommet X+/Y-/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymin, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      } else {
        if (pCoordinates.z < pShop.zmin) {
          // Sommet X+/Y+/Z-
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmin, 2));
        } else {
          // Sommet X+/Y+/Z+
          return Math.sqrt(Math.pow(pCoordinates.x - pShop.xmax, 2) + Math.pow(pCoordinates.y - pShop.ymax, 2) + Math.pow(pCoordinates.z - pShop.zmax, 2));
        }
      }
    }
  }

}
