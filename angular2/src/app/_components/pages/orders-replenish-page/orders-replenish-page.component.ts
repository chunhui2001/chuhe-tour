import { Component, OnInit } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormControl} from '@angular/forms';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {BasicComponent} from '../../basic.component';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-orders-replenish-page',
  templateUrl: './orders-replenish-page.component.html',
  styleUrls: ['./orders-replenish-page.component.css']
})
export class OrdersReplenishPageComponent extends BasicComponent implements OnInit {

  myControl: FormControl = new FormControl();
  options = ['One', 'Two', 'Three'];
  filteredOptions: Observable<string[]>;

  constructor(protected route: ActivatedRoute, fb: FormBuilder) {

    super(route, fb);

  }

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(val => this.filter(val))
    );
  }

  filter(val: string): string[] {
    return this.options.filter(option => option.toLowerCase().indexOf(val.toLowerCase()) === 0);
  }

}
