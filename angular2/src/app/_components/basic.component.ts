///<reference path="../../../node_modules/@angular/core/src/metadata/directives.d.ts"/>
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ENTER, COMMA, TAB} from '@angular/cdk/keycodes';


@Component({
  selector: 'app-basic',
  template: ``,
  styleUrls: []
})
export class BasicComponent {

  // Enter, comma
  separatorKeysCodes = [ENTER, COMMA];

  formGroupOptions: FormGroup;

  state: String = '';
  error: any;


  getCurrentUrl() {
    return '/' + this.route.snapshot.url.map(p => {
      return p.path;
    }).join('/');
  }

  constructor(protected route: ActivatedRoute, fb: FormBuilder) {

    this.formGroupOptions = fb.group({
      hideRequired: false,
      floatLabel: 'always',  // or auto
    });

  }

}
