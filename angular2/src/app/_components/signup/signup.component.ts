
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import * as $ from 'jquery';

import {fallIn, moveIn, comp} from '../../routers/router.animations';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css'],
  animations: [ comp() ],
  host: { '[@comp]': '' }
})
export class SignupComponent implements OnInit {

  state: String = '';
  error: any;

  username: String = '';
  passwd: String = '';
  phone: String = '';
  address: String = '';
  isAgreeRule: boolean = false;

  constructor(private http: HttpClient, private router: Router ) {

  }

  onSubmit(formData) {


    if (!formData.valid) {
      return;
    }

    debugger;
    $(".signup-component #signup-form").submit();

    debugger;

    // validate user
    // curl -v -l -H "Content-type: application/json" \
    // -X POST -d '{"username":"tongtong","password":"Cc"}' http://localhost:8844/login

    // need to setup a proxy to backend

    /*const req = this.http.post('/login', {
      username: formData.controls.email._value,
      password: formData.controls.password._value
    }).subscribe(
      res => {
        console.log(res);

        window.location.href = '/index';
      },
      (err: HttpErrorResponse) => {
        if (err.error instanceof Error) {
          console.log(err.message, 'Client-side Error Occured');
        } else {
          console.log(err.message, ' #Server-side Error Occured');
        }
      }
    ); */

    return;

    // if (formData.controls.email._value !== 'chunhui2001@gmail.com') {
    //   this.error = 'Invalid email formatter';
    //   return;
    // }
    //
    // this.router.navigateByUrl('/members');

  }

  ngOnInit() {

  }

}
