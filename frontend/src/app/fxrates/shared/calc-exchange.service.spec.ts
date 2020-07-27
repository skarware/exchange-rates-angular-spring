import { TestBed } from '@angular/core/testing';

import { CalcExchangeService } from './calc-exchange.service';

describe('CalcExchangeService', () => {
  let service: CalcExchangeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CalcExchangeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
