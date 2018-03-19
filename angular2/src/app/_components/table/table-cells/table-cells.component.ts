import { Component, OnInit } from '@angular/core';


import * as _ from 'lodash';

@Component({
  selector: 'app-table-cells',
  templateUrl: './table-cells.component.html',
  styleUrls: ['./table-cells.component.css']
})
export class TableCellsComponent implements OnInit {

  rowList: any = [];

  constructor() { }

  ngOnInit() {
    this.appendNewRow(null);
  }

  appendNewRow(data: any): void {

    if (!data) {
      this.rowList.push(this.newEmptyProduct());
    } else {
      this.appendProductToEmptyRow(data);
    }

  }

  newEmptyProduct(): any {
    return {product_id: null, product_name: null, product_brand: null, product_spec: null, product_price: null, product_unit: null, product_buy_count: null, product_total_money: null, product_vender: null, order_item_desc: null};
  }

  appendProductToEmptyRow(data: any) {

    const result = _.extend(this.newEmptyProduct(), {product_buy_count: 1}, data);
    const emptyRowIndex = this.getEmptyRowIndex();

    if (emptyRowIndex === -1) {
      this.rowList.push(result);
    } else {
      this.rowList[emptyRowIndex] = result;
    }

  }

  getEmptyRowIndex(): number {
    for (let i = 0; i < this.rowList.length; i++) {
      if (this.rowList[i].product_id === null) {
        return i;
      }
    }
    return -1;
  }

}
