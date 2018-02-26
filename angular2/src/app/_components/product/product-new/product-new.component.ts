import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

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

  editOrNew: String = 'new';
  the_action: String = '/mans/products';
  the_url: String = '';

  product_id: Number;
  product_name: String = '';
  product_type: String = '';
  product_unit: String = '';
  product_price: String = '0.0';
  product_spec: String = '';
  product_desc: String = '';
  created_at: Date;
  last_updated: Date;

  getCurrentUrl() {
    return '/' + this.route.snapshot.url.map(p => {
      return p.path;
    }).join('/');
  }

  constructor(private route: ActivatedRoute) {


  }

  ngOnInit() {

    if ($('#hid_product_model_json').length > 0 && $('#hid_product_model_json').val().trim().length > 0) {
      this.loadProduct(JSON.parse($('#hid_product_model_json').val()));
    }

  }

  loadProduct(productObject) {

    this.product_id = productObject.product_id;
    this.product_name = productObject.product_name;
    this.product_price = productObject.product_price.toFixed(2);
    this.product_spec = productObject.product_spec;
    this.product_type = productObject.product_type;
    this.product_unit = productObject.product_unit;
    this.product_desc = productObject.product_desc;
    this.created_at = productObject.created_at;
    this.last_updated = productObject.last_updated;

    this.the_url = this.getCurrentUrl();
    this.editOrNew = this.the_url.endsWith('/edit') ? 'edit' : 'new';
    this.the_action = this.editOrNew === 'edit' ? (this.the_action + '/' + this.product_id) : '/mans/products';

  }

  onSubmit(formData) {

    if (!formData.valid) {
      return;
    }

    $('.product-new-component #product-new-form').submit();

  }

}
