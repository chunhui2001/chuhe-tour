import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FloatInputComponent } from './float-input.component';

describe('FloatInputComponent', () => {
  let component: FloatInputComponent;
  let fixture: ComponentFixture<FloatInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FloatInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FloatInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
