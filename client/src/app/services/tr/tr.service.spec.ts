import { TestBed, inject } from '@angular/core/testing';

import { TrService } from './tr.service';

describe('TrService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TrService]
    });
  });

  it('should be created', inject([TrService], (service: TrService) => {
    expect(service).toBeTruthy();
  }));
});
