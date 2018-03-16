import { Component, OnInit } from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormControl} from '@angular/forms';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {BasicComponent} from '../../basic.component';
import {ActivatedRoute} from '@angular/router';
import {ProductService} from '../../../_services/product/product.service';

@Component({
  selector: 'app-orders-replenish-page',
  templateUrl: './orders-replenish-page.component.html',
  styleUrls: ['./orders-replenish-page.component.css']
})
export class OrdersReplenishPageComponent extends BasicComponent implements OnInit {

  inputText: String;

  myControl: FormControl = new FormControl();
  options: any = [];
  filteredOptions: Observable<string[]>;

  constructor(protected route: ActivatedRoute, fb: FormBuilder, private productService: ProductService) {

    super(route, fb);

  }

  ngOnInit() {
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(''),
      map(val => this.filter(val))
    );
  }

  filter(val: any): any[] {
    return this.options.filter(item => item.name.toLowerCase().indexOf(val.toLowerCase()) === 0);
    // return this.options.filter(item => item.id === val);
  }

  onOptionSelected(val: string): void {
    const selectedOption = this.filter(val);
  }

  inputTextChange(text): void {

    if (!text || text.trim().length < 2) {
      return;
    }

    this.productService.getProsucts('').subscribe(result => {

      this.options = result.data.map(item => {
        return {id: item.product_id, name: item.product_name };
      });

    });


  }

}
