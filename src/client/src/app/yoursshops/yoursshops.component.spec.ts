import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { YoursshopsComponent } from './yoursshops.component';

describe('YoursshopsComponent', () => {
  let component: YoursshopsComponent;
  let fixture: ComponentFixture<YoursshopsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ YoursshopsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(YoursshopsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
