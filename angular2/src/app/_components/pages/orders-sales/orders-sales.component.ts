import {Component, OnInit, ViewChild} from '@angular/core';
import {ProductService} from '../../../_services/product/product.service';
import {FormBuilder, FormControl} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {ActivatedRoute} from '@angular/router';
import {BasicOrderComponent} from '../orders/basic-order.component';

import * as $ from 'jquery';

@Component({
  selector: 'app-orders-sales',
  templateUrl: './orders-sales.component.html',
  styleUrls: ['./orders-sales.component.css']
})
export class OrdersSalesComponent extends BasicOrderComponent implements OnInit {

  constructor(protected route: ActivatedRoute, fb: FormBuilder, protected productService: ProductService) {
    super(route, fb, productService);
  }


  ngOnInit() {
    this.loadData(null);
  }


  saveSalesOrder(): void {

    const orderItems = this.tableCellsProduct.getTableData().map(item => {
      return { product_id: item.product_id, product_price: item.product_price, product_sale_count: item.product_sale_count, order_item_desc: item.order_item_desc };
    });

    debugger;

    for (let i = 0; i < orderItems.length; i++) {
      $('<input type=\'hidden\' name=\'order_item_' + i + '\' value=\'' + JSON.stringify(orderItems[i]) + '\' />')
        .appendTo($('#orders-order-detail-form'));
    }

    $('<input type=\'hidden\' name=\'order_item_count\' value=\'' + orderItems.length + '\' />')
      .appendTo($($('#orders-order-detail-form')));

    $('form#orders-order-detail-form').submit();

  }

}
