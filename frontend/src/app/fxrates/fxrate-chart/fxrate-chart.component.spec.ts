import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FxrateChartComponent } from './fxrate-chart.component';

describe('FxrateChartComponent', () => {
  let component: FxrateChartComponent;
  let fixture: ComponentFixture<FxrateChartComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FxrateChartComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FxrateChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
