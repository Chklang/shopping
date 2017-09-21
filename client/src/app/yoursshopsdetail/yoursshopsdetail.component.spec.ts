import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { YoursshopsdetailComponent } from './yoursshopsdetail.component';

describe('YoursshopsdetailComponent', () => {
  let component: YoursshopsdetailComponent;
  let fixture: ComponentFixture<YoursshopsdetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ YoursshopsdetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(YoursshopsdetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
