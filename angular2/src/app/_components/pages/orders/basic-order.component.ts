import {Component, OnInit, ViewChild} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {ENTER, COMMA, TAB} from '@angular/cdk/keycodes';
import {BasicComponent} from '../../basic.component';
import {map} from 'rxjs/operators/map';
import {startWith} from 'rxjs/operators/startWith';
import {ProductService} from '../../../_services/product/product.service';
import {Observable} from 'rxjs/Observable';


import * as _ from 'lodash';
import * as $ from 'jquery';

@Component({
  selector: 'app-basic',
  template: ``,
  styleUrls: []
})
export class BasicOrderComponent extends BasicComponent {


  @ViewChild('tableCellsProduct') tableCellsProduct;

  inputText: String;

  myControl: FormControl = new FormControl();
  options: any = [];
  filteredOptions: Observable<string[]>;

  constructor(protected route: ActivatedRoute, fb: FormBuilder, protected productService: ProductService) {

    super(route, fb);

    this.formGroupOptions = fb.group({
      hideRequired: false,
      floatLabel: 'always',  // or auto
    });

  }

  filter(val: any): any[] {
    return this.options.filter(item => item.name.toLowerCase().indexOf(val.toLowerCase()) === 0);
    // return this.options.filter(item => item.id === val);
  }


  onOptionSelected(event, val: string): void {
    const selectedOption = this.filter(val);

    this.productService.getProsuctById(selectedOption[0].id).subscribe(result => {

      if (result.data) {
        this.newRow(result.data);
      }

    });


    // this.tableCellsProduct.txt_input_count.last.nativeElement.focus();
  }


  newRow(data: any): void {
    this.tableCellsProduct.appendNewRow(data);
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

}
