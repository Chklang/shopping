import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ShopsdetailsmodifyComponent } from './shopsdetailsmodify.component';

describe('YoursshopsdetailComponent', () => {
  let component: ShopsdetailsmodifyComponent;
  let fixture: ComponentFixture<ShopsdetailsmodifyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ShopsdetailsmodifyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ShopsdetailsmodifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
