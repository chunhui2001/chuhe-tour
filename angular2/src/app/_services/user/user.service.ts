import { Injectable } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {of} from 'rxjs/observable/of';
import {RestResponse} from '../../_entities/_index';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class UserService {

  constructor(private http: HttpClient) { }

  userNameDuplicate(username: String):  Observable<any> {

    const endpoint = '/username_duplicate.json';

    return this.http.post(endpoint, {username: username})
      .map((response: RestResponse) => {

        if (response.error) {
          return response.message[0];
        }

        return response;
      });


  }

}
