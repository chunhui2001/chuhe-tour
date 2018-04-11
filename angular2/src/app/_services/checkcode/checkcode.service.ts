import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { RestResponse } from '../../_entities/_index';

@Injectable()
export class CheckcodeService {

  constructor(private http: HttpClient) { }

  check(sign: String, code: String, receiver: String, checktype: String): Observable<any> {

    const endpoint = '/checkcode';

    return this.http.post(endpoint, {sign: sign, checkcode: code, receiver: receiver, checktype: checktype})
      .map((response: RestResponse) => {
        if (response.error) {
          return response.message[0];
        }

        return response;
      });

  }

}
