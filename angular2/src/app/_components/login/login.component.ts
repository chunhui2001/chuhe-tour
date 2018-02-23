import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import {fallIn, moveIn, comp} from '../../routers/router.animations';

interface ContextResponse {
  login: string,
  bio: string
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  animations: [ comp() ],
  host: { '[@comp]': '' }
})
export class LoginComponent implements OnInit {

  state: String = '';
  error: any;

  username: String = '';
  passwd: String = '';
  phone: String = '';
  address: String = '';

  constructor(private http: HttpClient, private router: Router ) {

  }

  onSubmit(formData) {


    if (!formData.valid) {
      return;
    }

    this.username = formData.controls.user_name.value;
    this.passwd = formData.controls.user_name.value;

    // curl -v -X POST -H "Content-Type:application/json" \
    // -d '{"username": "keesh", "password": "keesh"}' \
    // http://localhost:8081/access_token

    
    alert(1);

    debugger;

  }

  // prepare data
  ngOnInit() {


    /*this.http.get<ContextResponse>('https://api.github.com/users/chunhui2001').subscribe(
      (response: ContextResponse) => {
        console.log(response.login);
        console.log(response.bio);
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log("client error");
        } else {
          console.log("server error");

        }

      }
    );

    this.http.post<ContextResponse>('/access_token.json', {
      username: 'admin', password: 'admin'
    }).subscribe(
      (response: ContextResponse) => {
        let token = response.data;
        debugger;
      }, 
      (err: HttpErrorResponse) => {
        debugger;
        if (err.error instanceof Error) {
          console.log("client error");
        } else {
          console.log("server error");
        }
      }
    ); */

  }

}


