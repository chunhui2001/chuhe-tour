import {Component, HostBinding, OnInit} from '@angular/core';

@Component({
  selector: 'app-product-details',
  templateUrl: './product-details.component.html',
  styleUrls: ['./product-details.component.css']
})
export class ProductDetailsComponent implements OnInit {

  @HostBinding('@.disabled') disabled = true;

  constructor() { }

  ngOnInit() {
  }

}
