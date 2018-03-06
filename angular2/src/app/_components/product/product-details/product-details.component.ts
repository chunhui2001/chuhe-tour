import {Component, HostBinding, OnInit} from '@angular/core';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import { interval } from 'rxjs/observable/interval';
import { takeWhile } from 'rxjs/operators';
import 'rxjs/add/operator/map';



@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {

  @HostBinding('@.disabled') disabled = true;

  images_slide = [
    '//placehold.it/646x370/000000',
    '//placehold.it/646x370/111111',
    '//placehold.it/646x370/333333',
    '//placehold.it/646x370/444444',
    '//placehold.it/646x370/555555',
    '//placehold.it/646x370/666666',
    '//placehold.it/646x370/777777'
  ];


  thumbnails_slide = [
    '//placehold.it/181x100/000000',
    '//placehold.it/181x100/111111',
    '//placehold.it/181x100/333333',
    '//placehold.it/181x100/444444',
    '//placehold.it/181x100/555555',
    '//placehold.it/181x100/666666',
    '//placehold.it/181x100/777777'
  ];

  skuItems: any = [];

  constructor() {

  }


  ngOnInit() {
    this.skuItems.push({
      name: '15个免费DepositPhotos图片', price: 120, selected: false
    });
    this.skuItems.push({
      name: '快速和可靠主机解决方案', price: 297, selected: false
    });

  }


  skuClick(skuItem) {
    skuItem.selected = !skuItem.selected;
  }

}
