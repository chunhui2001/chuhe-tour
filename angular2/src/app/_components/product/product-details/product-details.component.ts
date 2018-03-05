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
    {img: 'http://placehold.it/645x370/000000'},
    {img: 'http://placehold.it/645x370/111111'},
    {img: 'http://placehold.it/645x370/333333'},
    {img: 'http://placehold.it/645x370/666666'},
    {img: 'http://placehold.it/645x370/666666'},
    {img: 'http://placehold.it/645x370/666666'},
    {img: 'http://placehold.it/645x370/666666'}
  ];


  thumbnails_slide = [
    {img: 'http://placehold.it/180x100/000000'},
    {img: 'http://placehold.it/180x100/111111'},
    {img: 'http://placehold.it/180x100/333333'},
    {img: 'http://placehold.it/180x100/666666'},
    {img: 'http://placehold.it/180x100/666666'},
    {img: 'http://placehold.it/180x100/666666'},
    {img: 'http://placehold.it/180x100/666666'}
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
