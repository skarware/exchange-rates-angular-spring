import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FxratesComponent } from './fxrates.component';

describe('FxratesComponent', () => {
  let component: FxratesComponent;
  let fixture: ComponentFixture<FxratesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FxratesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FxratesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
