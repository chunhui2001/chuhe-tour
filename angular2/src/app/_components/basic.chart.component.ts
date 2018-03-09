///<reference path="../../../node_modules/@angular/core/src/metadata/directives.d.ts"/>
import { Component, OnInit } from '@angular/core';



@Component({
  selector: 'app-basic',
  template: ``,
  styleUrls: []
})
export class BasicChartComponent {

  state: String = '';
  error: any;

  public showLegend = true;

  // line, area
  public autoScale = true;

  public colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
  };

  protected chartViewExample: any[] = [500, 300];
  protected chartDataExample: any[] = [
    {
      'name': 'Germany',
      'series': [
        {
          'name': '2010',
          'value': 7300000
        },
        {
          'name': '2011',
          'value': 8940000
        },
        {
          'name': '2012',
          'value': 8840000
        },
        {
          'name': '2013',
          'value': 8740000
        },
        {
          'name': '2014',
          'value': 9840000
        },
        {
          'name': '2015',
          'value': 5340000
        },
        {
          'name': '2016',
          'value': 6840000
        },
        {
          'name': '2017',
          'value': 5840000
        }
      ]
    }, {
      'name': 'USA',
      'series': [
        {
          'name': '2010',
          'value': 7870000
        },
        {
          'name': '2011',
          'value': 8270000
        },
        {
          'name': '2012',
          'value': 8260000
        },
        {
          'name': '2014',
          'value': 6260000
        },
        {
          'name': '2015',
          'value': 7260000
        },
        {
          'name': '2016',
          'value': 5260000
        },
        {
          'name': '2017',
          'value': 7260000
        }
      ]
    }, {
      'name': 'France',
      'series': [
        {
          'name': '2010',
          'value': 5000002
        },
        {
          'name': '2011',
          'value': 5800000
        },
        {
          'name': '2012',
          'value': 5700000
        },
        {
          'name': '2013',
          'value': 6700000
        },
        {
          'name': '2014',
          'value': 7700000
        },
        {
          'name': '2015',
          'value': 8700000
        },
        {
          'name': '2016',
          'value': 8800000
        },
        {
          'name': '2017',
          'value': 9110000
        }
      ]
    }
  ];



  constructor() {

  }

}
