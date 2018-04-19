import { TestBed, inject } from '@angular/core/testing';

import { ValidatorService } from './validator.service';

describe('ValidateService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ValidatorService]
    });
  });

  it('should be created', inject([ValidatorService], (service: ValidatorService) => {
    expect(service).toBeTruthy();
  }));
});
