import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrdersReplenishPageComponent } from './orders-replenish-page.component';

describe('OrdersReplenishPageComponent', () => {
  let component: OrdersReplenishPageComponent;
  let fixture: ComponentFixture<OrdersReplenishPageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrdersReplenishPageComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrdersReplenishPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
