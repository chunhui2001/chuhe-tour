import {Component, Input, OnInit} from '@angular/core';

import {VoteService} from '../../../_services/vote/vote.service';
import {BasicChartComponent} from '../../basic.chart.component';

@Component({
  selector: 'app-line-chart',
  templateUrl: './line-chart.component.html',
  styleUrls: ['./line-chart.component.css']
})
export class LineChartComponent extends BasicChartComponent implements OnInit {


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

  survey: any = {
    country: '',
    gender: '',
    rating: 0
  };

  onSelect(e) {

  }

  constructor(private vote: VoteService) {

    super();

    if (! this.chartData) {
      this.chartData = this.chartDataExample;
    }

    if (! this.chartView) {
      this.chartView = this.chartViewExample;
    }

  }

  ngOnInit() {
    this.vote.getAllEntries().subscribe((result) => this.processData(result));
  }

  saveEntry() {
    this.vote.saveEntry(this.survey);
  }

  processData(entries) {
    // console.log(entries, 'entries');
  }
}
