import {Component, Input, OnInit} from '@angular/core';
import { of } from 'rxjs/observable/of';
import { from } from 'rxjs/observable/from';

@Component({
  selector: 'app-slick-carousel',
  templateUrl: './slick-carousel.component.html',
  styleUrls: ['./slick-carousel.component.css']
})
export class SlickCarouselComponent implements OnInit {

  constructor() { }

  @Input()
  images: any;

  @Input()
  thumbnails: any;

  images_slide = [
    {idx: 0, img: '//placehold.it/645x370/000000'},
    {idx: 1, img: '//placehold.it/645x370/111111'},
    {idx: 2, img: '//placehold.it/645x370/333333'},
    {idx: 3, img: '//placehold.it/645x370/444444'},
    {idx: 4, img: '//placehold.it/645x370/555555'},
    {idx: 5, img: '//placehold.it/645x370/666666'},
    {idx: 6, img: '//placehold.it/645x370/777777'}
  ];


  thumbnails_slide = [
    {idx: 0, img: '//placehold.it/180x100/000000'},
    {idx: 1, img: '//placehold.it/180x100/111111'},
    {idx: 2, img: '//placehold.it/180x100/333333'},
    {idx: 3, img: '//placehold.it/180x100/444444'},
    {idx: 4, img: '//placehold.it/180x100/555555'},
    {idx: 5, img: '//placehold.it/180x100/666666'},
    {idx: 6, img: '//placehold.it/180x100/777777'}
  ];

  currentSlide: any;

  ngOnInit() {

    this.currentSlide = this.thumbnails_slide[0];

    if (this.images) {
      // from(this.images).subscribe(val => { this.images_slide.push({ idx: 0, img: val }); });
      this.images_slide = this.images.map((val, index) => {
          return { idx: index, img: val};
      });
    }

    if (this.thumbnails) {
      this.thumbnails_slide = this.thumbnails.map((val, index) => {
        return { idx: index, img: val};
      });
    }

  }


  beforeChange(e) {
    this.currentSlide = this.thumbnails_slide[e.slick.currentSlide];
  }


}
