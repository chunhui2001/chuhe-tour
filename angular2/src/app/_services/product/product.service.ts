import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs/observable/of';
import { HttpClient } from '@angular/common/http';


import { RestResponse } from '../../_entities/_index';

@Injectable()
export class ProductService {

  constructor(private http: HttpClient) { }

  getProsucts(pName: String): Observable<any> {

    let endpoint = '/mans/products.json';

    if (pName) {
      endpoint = endpoint + '?pname=' + pName;
    }

    return this.http.get(endpoint)
      .map((response: RestResponse) => {

        if (response.error) {
          return response.message[0];
        }

        return response;
      });

    // return of([{id: 1, name: 'One'}, {id: 2, name: 'Two'}, {id: 3, name: 'Three'}, {id: 4, name: 'Three 666'}]);
  }

  getProsuctById(pId: any): Observable<any> {

    const endpoint = '/store/p/' + pId + '.json';

    return this.http.get(endpoint)
      .map((response: RestResponse) => {

        if (response.error) {
          return response.message[0];
        }

        return response;
      });

    // return of([{id: 1, name: 'One'}, {id: 2, name: 'Two'}, {id: 3, name: 'Three'}, {id: 4, name: 'Three 666'}]);
  }

}
