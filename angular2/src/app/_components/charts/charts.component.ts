import { Component, OnInit } from '@angular/core';

import { VoteService } from '../../_services/vote/vote.service';

@Component({
  selector: 'app-charts',
  templateUrl: './charts.component.html',
  styleUrls: ['./charts.component.css']
})
export class ChartsComponent implements OnInit {

  single: any[] = [
      {
          'name': 'Germany',
          'value': 8940000
      }, {
        'name': 'USA',
        'value': 5000000
      }, {
        'name': 'France',
        'value': 7200000
      }
  ];

  multi: any[] = [
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
          }
        ]
      }
  ];

  view: any[] = [700, 400];

  obj: {};

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showLegend = true;
  showXAxisLabel = true;
  xAxisLabel = 'Country';
  showYAxisLabel = true;
  yAxisLabel = 'Population';

  // line, area
  autoScale = true;

  colorScheme = {
    domain: ['#5AA454', '#A10A28', '#C7B42C', '#AAAAAA']
  };

  // pie
  showLabels = true;
  explodeSlices = false;
  doughnut = false;

  survey: any = {
    country: '',
    gender: '',
    rating: 0
  }

  chartData: any;

  onSelect(e) {

  }

  constructor(private vote: VoteService) {

  }

  ngOnInit() {
    this.vote.getAllEntries().subscribe((result) => this.processData(result));
  }

  saveEntry() {
    this.vote.saveEntry(this.survey);
  }

  processData(entries) {
    console.log(entries, 'entries');
  }

}
