import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import {ENTER, COMMA, TAB} from '@angular/cdk/keycodes';

import {MatChipInputEvent} from '@angular/material';

import * as $ from 'jquery';
import * as _ from 'lodash';
import {BasicComponent} from '../../basic.component';


@Component({
  selector: 'app-product-new',
  templateUrl: './product-new.component.html',
  styleUrls: ['./product-new.component.css']
})
export class ProductNewComponent extends BasicComponent implements OnInit {

  froalaOptions: Object = {
    placeholderText: '请输入产品描述',
    events : {
      'froalaEditor.initialized' : function(e, editor) {
        // editor.edit.off();
        editor.edit.on();
      }
    }
  };

  editOrNew: String = 'new';
  the_action: String = '/mans/products';
  the_url: String = '';

  product_id: Number;
  product_name: String = '';
  product_type: String = '';
  product_unit: String = '';
  product_price: String = '0.0';
  product_medias: any = [];
  product_spec: String = '';
  product_desc: String = '';
  created_at: Date;
  last_updated: Date;

  // visible: Boolean = true;
  pTypeSelectable: Boolean = true;
  pTypeRemovable: Boolean = true;
  pTypeAddOnBlur: Boolean = true;


  productTypeFruits = [ ];

  add(event: MatChipInputEvent): void {

    const input = event.input;
    const value = event.value;

    // Add our fruit
    if ((value || '').trim()) {

      (value.trim().replace(/,|-|\.|;|\/|\s/g, ',').split(','))
        .forEach(v => {
          const currentType = {name: v.trim()};
          const isFind = _.find(this.productTypeFruits, { 'name': v.trim()});
          if (v.trim().length > 0 && !isFind) {
            this.productTypeFruits.push(currentType);
          }
      });

    }

    // Reset the input value
    if (input) {
      input.value = '';
    }
  }

  remove(pType: any): void {
    const index = this.productTypeFruits.indexOf(pType);
    if (index >= 0) {
      this.productTypeFruits.splice(index, 1);
    }
  }

  constructor(protected route: ActivatedRoute, fb: FormBuilder) {

    super(route, fb);

  }

  ngOnInit() {

    if ($('#hid_product_model_json').length > 0 && $('#hid_product_model_json').val().trim().length > 0) {
      this.editOrNew = 'edit';
      this.loadProduct(JSON.parse($('#hid_product_model_json').val()));
    } else {
      this.editOrNew = 'new';
    }

  }

  onReupload () {
    window.location.href = '/mans/products/' + this.product_id + '/images';
  }

  loadProduct(productObject) {

    this.product_id = productObject.product_id;
    this.product_name = productObject.product_name;
    this.product_price = productObject.product_price.toFixed(2);
    this.product_medias = (productObject.product_medias && productObject.product_medias.trim().split(',')) || [];
    this.product_spec = productObject.product_spec;
    this.product_type = productObject.product_type;
    this.product_unit = productObject.product_unit;
    this.product_desc = productObject.product_desc;
    this.created_at = productObject.created_at;
    this.last_updated = productObject.last_updated;

    this.the_action = this.editOrNew === 'edit' ? (this.the_action + '/' + this.product_id) : '/mans/products';


    this.productTypeFruits = this.product_type
                                  .split(',')
                                  .filter(t => t !== '无')
                                  .map(t => {
                                  return {name: t};
                                });

    if (this.productTypeFruits.length === 0) {
      this.product_type = '';
    }

  }

  onSubmit(formData) {

    if (!formData.valid) {
      return;
    }

    if (this.productTypeFruits.length === 0) {
      this.product_type = '';
    } else {
      $('#product_type').val(
              this.productTypeFruits.map( t => {
                return t.name;
              }).join(',')
      );
    }

    $('.product-new-component #product-new-form').submit();

  }

}
