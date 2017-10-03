import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopsdetailsviewComponent } from './shopsdetailsview.component';

describe('YoursshopsdetailComponent', () => {
  let component: ShopsdetailsviewComponent;
  let fixture: ComponentFixture<ShopsdetailsviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShopsdetailsviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShopsdetailsviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
