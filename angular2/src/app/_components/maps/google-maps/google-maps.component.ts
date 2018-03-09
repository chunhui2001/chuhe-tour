import {Component, ElementRef, Input, OnInit, AfterViewInit, ViewChild } from '@angular/core';

import { } from '@types/googlemaps';

@Component({
  selector: 'app-google-maps',
  templateUrl: './google-maps.component.html',
  styleUrls: ['./google-maps.component.css']
})
export class GoogleMapsComponent implements OnInit, AfterViewInit {

  @Input() mapId: String;
  @Input() width: String = '100%';
  @Input() height: String = '200px';
  @Input() zoom: any = 15;
  @Input() mapType: any = 'ROADMAP';
  @Input() lat: any = 18.5793;
  @Input() lng: any = 73.8143;
  @Input() mapTypeControl: String = 'true';

  mapTypeId: any;

  gmapElement: any;
  map: google.maps.Map;

  constructor(private myElement: ElementRef) {


  }

  ngOnInit() {

    switch (this.mapType) {
      case 'SATELLITE':
        this.mapTypeId = google.maps.MapTypeId.SATELLITE;
        break;
      case 'HYBRID':
        this.mapTypeId = google.maps.MapTypeId.HYBRID;
        break;
      case 'TERRAIN':
        this.mapTypeId = google.maps.MapTypeId.TERRAIN;
        break;
      default:
        this.mapTypeId = google.maps.MapTypeId.ROADMAP;
    }

  }

  ngAfterViewInit() {

    this.gmapElement = this.myElement.nativeElement.querySelector('#' + this.mapId);

    const mapProp = {
      center: new google.maps.LatLng(this.lat, this.lng),
      zoom: parseFloat(this.zoom),
      mapTypeId: this.mapTypeId,
      mapTypeControl: this.mapTypeControl === 'true',
      streetViewControl: true
    };

    this.map = new google.maps.Map(this.gmapElement, mapProp);

  }

}
