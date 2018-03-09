import {Component, Input, OnInit} from '@angular/core';
import {BasicChartComponent} from '../../basic.chart.component';

@Component({
  selector: 'app-gauge-chart',
  templateUrl: './gauge-chart.component.html',
  styleUrls: ['./gauge-chart.component.css']
})
export class GaugeChartComponent extends BasicChartComponent implements OnInit  {

  @Input()
  chartView: any[] ;

  @Input()
  chartData: any[] = [
    {
      'name': 'Germany',
      'value': 8940000
    },
    {
      'name': 'USA',
      'value': 5000000
    },
    {
      'name': 'France',
      'value': 7200000
    }
  ];

  constructor() {
    super();
  }

  ngOnInit() {
  }

  onSelect(e) {

  }

}
