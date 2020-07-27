import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CalcExchangeComponent } from './calc-exchange.component';

describe('CalcExchangeComponent', () => {
  let component: CalcExchangeComponent;
  let fixture: ComponentFixture<CalcExchangeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CalcExchangeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CalcExchangeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
