import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {FormBuilder, FormControl} from '@angular/forms';
import {startWith} from 'rxjs/operators/startWith';
import {map} from 'rxjs/operators/map';
import {BasicComponent} from '../../basic.component';
import {ActivatedRoute} from '@angular/router';
import {ProductService} from '../../../_services/product/product.service';


import * as _ from 'lodash';
import * as $ from 'jquery';
import {BasicOrderComponent} from '../orders/basic-order.component';

@Component({
  selector: 'app-orders-replenish-page',
  templateUrl: './orders-replenish-page.component.html',
  styleUrls: ['./orders-replenish-page.component.css']
})
export class OrdersReplenishPageComponent extends BasicOrderComponent implements OnInit {


  constructor(protected route: ActivatedRoute, fb: FormBuilder, protected productService: ProductService) {
    super(route, fb, productService);
  }

  ngOnInit() {
    this.loadData(null);
  }


  saveReplenishOrder(): void {

    const orderItems = this.tableCellsProduct.getTableData().map(item => {
      return { product_id: item.product_id, product_price: item.product_price, product_buy_count: item.product_buy_count };
    });

    for (let i = 0; i < orderItems.length; i++) {
      $('<input type=\'hidden\' name=\'order_item_' + i + '\' value=\'' + JSON.stringify(orderItems[i]) + '\' />')
        .appendTo($('#orders-order-detail-form'));
    }

    $('<input type=\'hidden\' name=\'order_item_count\' value=\'' + orderItems.length + '\' />')
        .appendTo($($('#orders-order-detail-form')));

    $('form#orders-order-detail-form').submit();

  }


}
