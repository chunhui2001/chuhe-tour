import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdersSalesComponent } from './orders-sales.component';

describe('OrdersSalesComponent', () => {
  let component: OrdersSalesComponent;
  let fixture: ComponentFixture<OrdersSalesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrdersSalesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrdersSalesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
