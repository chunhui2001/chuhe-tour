import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';

@Injectable()
export class VoteService {

  constructor() { }


  saveEntry(survey): void {
    console.log(survey, 'survey');
  }

  getAllEntries(): Observable<any> {
    return of([
      {
        country: 'US',
        gender: 'M',
        rating: 60
      }, {
        country: 'CN',
        gender: 'M',
        rating: 95
      }
    ]);
  }


}
