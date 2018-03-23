import {Component, HostBinding, OnInit, ViewChild} from '@angular/core';

import {Observable} from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import { interval } from 'rxjs/observable/interval';
import { takeWhile } from 'rxjs/operators';
import 'rxjs/add/operator/map';
import {ActivatedRoute} from '@angular/router';
import {ProductService} from '../../../_services/product/product.service';



@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {

  @HostBinding('@.disabled') disabled = true;


  @ViewChild('slickCarouselProduct') slickCarouselProduct;

  productId: number;
  productModel: any = { };

  skuItems: any = [];
  images_slide = [];
  thumbnails_slide = [];


  constructor(private route: ActivatedRoute, private productService: ProductService) {
    this.route.params.subscribe( params => {
        this.productId = params.pid;
        this.loadData();
      }
    );
  }

  loadData() {

    this.productService.getProsuctById(this.productId).subscribe(result => {

      if (result.code === 404) {
        window.location.href = '/p/' + this.productId;
        return;
      }

      this.productModel = result.data;

      this.skuItems.push({
        name: this.productModel.product_name, price: this.productModel.product_price.toFixed(2), selected: false
      });

      this.images_slide = this.productModel.product_medias.split(',');
      this.thumbnails_slide = this.images_slide;

      this.slickCarouselProduct.reload(this.images_slide, this.thumbnails_slide );

      console.log(this.productModel, 'model');
    });

  }


  ngOnInit() {

    this.images_slide = [
      '//placehold.it/646x370/000000',
      '//placehold.it/646x370/111111',
      '//placehold.it/646x370/333333',
      '//placehold.it/646x370/444444',
      '//placehold.it/646x370/555555',
      '//placehold.it/646x370/666666',
      '//placehold.it/646x370/777777'
    ];

    this.thumbnails_slide = [
      '//placehold.it/181x100/000000',
      '//placehold.it/181x100/111111',
      '//placehold.it/181x100/333333',
      '//placehold.it/181x100/444444',
      '//placehold.it/181x100/555555',
      '//placehold.it/181x100/666666',
      '//placehold.it/181x100/777777'
    ];

  }


  skuClick(skuItem) {
    skuItem.selected = !skuItem.selected;
  }

}
