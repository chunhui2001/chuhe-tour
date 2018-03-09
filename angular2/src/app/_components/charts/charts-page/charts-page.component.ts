import {Component, OnInit, ViewChild} from '@angular/core';
import { } from '@types/googlemaps';

@Component({
  selector: 'app-charts-page',
  templateUrl: './charts-page.component.html',
  styleUrls: ['./charts-page.component.css']
})
export class ChartsPageComponent implements OnInit {

  @ViewChild('gmap1') gmapElement1: any;
  map1: google.maps.Map;

  @ViewChild('gmap2') gmapElement2: any;
  map2: google.maps.Map;

  @ViewChild('gmap3') gmapElement3: any;
  map3: google.maps.Map;

  constructor() {

  }

  ngOnInit() {

    const mapProp1 = {
      center: new google.maps.LatLng(18.5793, 73.8143),
      zoom: 1,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    this.map1 = new google.maps.Map(this.gmapElement1.nativeElement, mapProp1);

    const mapProp2 = {
      center: new google.maps.LatLng(18.5793, 73.8143),
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    this.map2 = new google.maps.Map(this.gmapElement2.nativeElement, mapProp2);

    const mapProp3 = {
      center: new google.maps.LatLng(18.5793, 73.8143),
      zoom: 15,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    this.map3 = new google.maps.Map(this.gmapElement3.nativeElement, mapProp3);

  }

}


