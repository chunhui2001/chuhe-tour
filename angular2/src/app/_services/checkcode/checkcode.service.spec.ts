import { TestBed, inject } from '@angular/core/testing';

import { CheckcodeService } from './checkcode.service';

describe('CheckcodeService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [CheckcodeService]
    });
  });

  it('should be created', inject([CheckcodeService], (service: CheckcodeService) => {
    expect(service).toBeTruthy();
  }));
});
