import {Component, Input, OnInit} from '@angular/core';
import {BasicChartComponent} from '../../basic.chart.component';

@Component({
  selector: 'app-pie-grid-chart',
  templateUrl: './pie-grid-chart.component.html',
  styleUrls: ['./pie-grid-chart.component.css']
})
export class PieGridChartComponent extends BasicChartComponent implements OnInit {


  @Input()
  chartView: any[];

  @Input()
  chartData: any[];

  constructor() {
    super();

    this.chartData = [
      {
        'name': 'Germany',
        'value': 40632
      },
      {
        'name': 'United States',
        'value': 49737
      },
      {
        'name': 'France',
        'value': 36745
      },
      {
        'name': 'United Kingdom',
        'value': 36240
      },
      {
        'name': 'Spain',
        'value': 33000
      },
      {
        'name': 'Italy',
        'value': 35800
      }
    ];
  }

  onSelect(e) {

  }

  ngOnInit() {

  }

}
