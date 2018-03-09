import {Component, Input, OnInit} from '@angular/core';
import {BasicChartComponent} from '../../basic.chart.component';

@Component({
  selector: 'app-heat-map-chart',
  templateUrl: './heat-map-chart.component.html',
  styleUrls: ['./heat-map-chart.component.css']
})
export class HeatMapChartComponent extends BasicChartComponent implements OnInit {

  single: any[];

  @Input()
  chartView: any[];

  @Input()
  chartData: any[];

  // options
  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showXAxisLabel = true;
  xAxisLabel = 'Country';
  showYAxisLabel = true;
  yAxisLabel = 'Population';


  constructor() {
    super();

    if (! this.chartData) {
      this.chartData = this.chartDataExample;
    }

    if (! this.chartView) {
      this.chartView = this.chartViewExample;
    }

  }

  onSelect(event) {
    console.log(event);
  }

  ngOnInit() {
  }

}
