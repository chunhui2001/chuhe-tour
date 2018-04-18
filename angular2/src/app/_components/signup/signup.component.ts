
import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

import * as $ from 'jquery';

import {fallIn, moveIn, comp} from '../../routers/router.animations';
import {ValidatorService} from "../../_services/_index";

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
  phoneOrEmail: String = '';
  checkCode: any;
  address: String = '';
  isAgreeRule: Boolean = false;

  @ViewChild('check_code_input') check_code_input;

  constructor(private http: HttpClient, private router: Router, private validatorService: ValidatorService ) {

  }

  onSubmit(formData) {

    if (!formData.valid) {
      return;
    }

    if (this.phoneOrEmail && this.phoneOrEmail.trim().length > 0) {
      if (this.validatorService.validPhone(this.phoneOrEmail.toString())) {
        this.phoneOrEmail = this.validatorService.validPhone(this.phoneOrEmail.toString());
      }
    }

    $('.signup-component #signup-form').submit();


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
    // this.check_code_input.isDisable = false;
  }

  disableSubmit(): boolean {
    return !(this.isAgreeRule && this.check_code_input.validSuccess) || !this.phoneOrEmail || this.phoneOrEmail.length === 0;
  }



}
