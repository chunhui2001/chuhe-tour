import { Component, OnInit } from '@angular/core';


import * as $ from 'jquery';


@Component({
  selector: 'app-product-new',
  templateUrl: './product-new.component.html',
  styleUrls: ['./product-new.component.css']
})
export class ProductNewComponent implements OnInit {

  state: String = '';
  error: any;

  froalaOptions: Object = {
    placeholderText: '请输入产品描述',
    events : {
      'froalaEditor.initialized' : function(e, editor) {
        // editor.edit.off();
        editor.edit.on();
      }
    }
  }

  product_name: String = '';
  product_type: String = '';
  product_unit: String = '';
  product_price: Number = 0.0;
  product_spec: String = '';
  product_desc: String = '';

  constructor() { }


  onSubmit(formData) {

    if (!formData.valid) {
      return;
    }

    $('.product-new-component #product-new-form').submit();

  }

  ngOnInit() {
  }

}
