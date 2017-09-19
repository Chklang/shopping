import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AllshopsdetailsComponent } from './allshopsdetails.component';

describe('AllshopsdetailsComponent', () => {
  let component: AllshopsdetailsComponent;
  let fixture: ComponentFixture<AllshopsdetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AllshopsdetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AllshopsdetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
