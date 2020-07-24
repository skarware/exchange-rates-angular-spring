import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FxrateListComponent } from './fxrate-list.component';

describe('FxrateListComponent', () => {
  let component: FxrateListComponent;
  let fixture: ComponentFixture<FxrateListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FxrateListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FxrateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
