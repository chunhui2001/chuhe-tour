import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckCodeInputComponent } from './check-code-input.component';

describe('CheckCodeInputComponent', () => {
  let component: CheckCodeInputComponent;
  let fixture: ComponentFixture<CheckCodeInputComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CheckCodeInputComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckCodeInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
