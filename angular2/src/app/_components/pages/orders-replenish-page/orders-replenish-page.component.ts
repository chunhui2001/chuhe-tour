import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormControl} from '@angular/forms';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {BasicComponent} from '../../basic.component';
import {ActivatedRoute} from '@angular/router';
import {ProductService} from '../../../_services/product/product.service';


import * as _ from 'lodash';

@Component({
  selector: 'app-orders-replenish-page',
  templateUrl: './orders-replenish-page.component.html',
  styleUrls: ['./orders-replenish-page.component.css']
})
export class OrdersReplenishPageComponent extends BasicComponent implements OnInit {

  @ViewChild('tableCellsProduct') tableCellsProduct;

  inputText: String;

  myControl: FormControl = new FormControl();
  options: any = [];
  filteredOptions: Observable<string[]>;

  constructor(protected route: ActivatedRoute, fb: FormBuilder, private productService: ProductService) {
    super(route, fb);
  }

  ngOnInit() {
    this.loadData(null);
  }

  filter(val: any): any[] {
    return this.options.filter(item => item.name.toLowerCase().indexOf(val.toLowerCase()) === 0);
    // return this.options.filter(item => item.id === val);
  }

  onOptionSelected(val: string): void {
    const selectedOption = this.filter(val);
    this.newEmptyRow({product_id: selectedOption[0].id, product_name: selectedOption[0].name});
  }

  inputTextChange(text): void {

    if (!text || text.trim().length < 2) {
      return;
    }

    this.loadData(text);

  }

  loadData(pName: string): void {

    this.productService.getProsucts(pName).subscribe(result => {

      const restult = result.data.map(item => {
        return {id: item.product_id, name: item.product_name };
      });

      this.options = _.uniqBy(restult.concat(this.options), 'name');

      this.filteredOptions = this.myControl.valueChanges.pipe(
        startWith(''),
        map(val => this.filter(val))
      );

    });

  }

  newEmptyRow(data: any): void {
    this.tableCellsProduct.appendNewRow(data);
  }

}
