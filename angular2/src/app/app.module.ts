import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
// import { HttpModule } from '@angular/http';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AngularFontAwesomeModule } from 'angular-font-awesome';

// import * as $ from 'jquery';
// import 'froala-editor/js/froala_editor.pkgd.min.js';

// window['$'] = $;
// window['jQuery'] = $;

// angular html editer:
// https://github.com/froala/angular-froala-wysiwyg
// https://www.froala.com/wysiwyg-editor/docs/overview
import { FroalaEditorModule, FroalaViewModule } from 'angular-froala-wysiwyg';

// google material
import {
  MatIconModule, MatToolbarModule, MatMenuModule, MatCardModule,
  MatButtonModule, MatCheckboxModule, MatChipInputEvent, MatSelectModule,
  MatChipsModule, MatProgressBarModule, MAT_LABEL_GLOBAL_OPTIONS, MatTabsModule,
  MatFormFieldModule, MatAutocompleteModule, MatOptionModule,
  MatInputModule
} from '@angular/material';


// Import your library
import { SlickModule } from 'ngx-slick';

// Ngx-Charts
import { NgxChartsModule } from '@swimlane/ngx-charts';

// perfect scroll bar
import { PerfectScrollbarModule, PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';

import { HttpIntercepor } from './_interceptor/http.interceptor';
import { routers } from './routers/app.router';

// _service
import { VoteService } from './_services/vote/vote.service';
import { ProductService } from './_services/product/product.service';

// _components
import { BasicComponent } from './_components/basic.component';
import { BasicChartComponent } from './_components/basic.chart.component';


import { IndexPageComponent } from './_components/pages/index-page/index-page.component';

import { AppComponent } from './_components/app/app.component';
import { SignupComponent } from './_components/signup/signup.component';
import { EmptyComponent } from './_components/empty/empty.component';
import { LoginComponent } from './_components/login/login.component';
import { ProductNewComponent } from './_components/product/product-new/product-new.component';
import { MediaUploadComponent } from './component/media-upload/media-upload.component';
import { ProductDetailsComponent } from './_components/product/product-details/product-details.component';
import { SlickCarouselComponent } from './component/slick-carousel/slick-carousel.component';


// doc: https://swimlane.gitbooks.io/ngx-charts/content/intro/installing.html
// demo: https://swimlane.github.io/ngx-charts/#/ngx-charts/combo-chart
// npm install @swimlane/ngx-charts --save
// npm install d3 --save
// @NgModule({
//   exports: [
//     NgxChartsModule
//   ],
//   declarations: [ChartsComponent]
// })
// export class ChartsModule {}


import { ChartsPageComponent } from './_components/charts/charts-page/charts-page.component';
import { LineChartComponent } from './_components/charts/line-chart/line-chart.component';
import { HeatMapChartComponent } from './_components/charts/heat-map-chart/heat-map-chart.component';
import { PieGridChartComponent } from './_components/charts/pie-grid-chart/pie-grid-chart.component';
import { GaugeChartComponent } from './_components/charts/gauge-chart/gauge-chart.component';

// https://blog.cloudboost.io/integrating-google-maps-in-angular-5-ca5f68009f29
// https://angular-maps.com/
import { GoogleMapsComponent } from './_components/maps/google-maps/google-maps.component';
import { OrdersReplenishPageComponent } from './_components/pages/orders-replenish-page/orders-replenish-page.component';
import { TableCellsComponent } from './_components/table/table-cells/table-cells.component';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

@NgModule({
  exports: [
    MatIconModule, MatToolbarModule, MatMenuModule, MatCardModule,
    MatButtonModule, MatCheckboxModule, MatSelectModule,
    MatChipsModule, MatProgressBarModule, MatTabsModule,
    MatFormFieldModule, MatAutocompleteModule, MatOptionModule, MatInputModule
  ],
  declarations: [ ]
})
export class GoogleMaterialModule {}



// rxjs: doc
// https://www.sitepoint.com/ultimate-angular-cli-reference/
// https://www.learnrxjs.io/operators/filtering/takewhile.html

@NgModule({
  declarations: [
    BasicComponent,
    BasicChartComponent,
    AppComponent,
    SignupComponent,
    EmptyComponent,
    LoginComponent,
    ProductNewComponent,
    MediaUploadComponent,
    ProductDetailsComponent,
    SlickCarouselComponent,

    // table
    TableCellsComponent,

    // charts components
    ChartsPageComponent, LineChartComponent, HeatMapChartComponent,
    PieGridChartComponent, GaugeChartComponent,

    // pages components
    IndexPageComponent,
    OrdersReplenishPageComponent,

    GoogleMapsComponent

  ],
  imports: [
    BrowserModule, BrowserAnimationsModule, NgxChartsModule,
    HttpClientModule, FormsModule, ReactiveFormsModule,
    FroalaEditorModule.forRoot(), FroalaViewModule.forRoot(),
    GoogleMaterialModule, PerfectScrollbarModule,
    routers, AngularFontAwesomeModule, SlickModule.forRoot()
  ],
  providers: [
    {
      provide: MAT_LABEL_GLOBAL_OPTIONS, useValue: {float: 'always'}
    }, {
      provide: PERFECT_SCROLLBAR_CONFIG,
      useValue: DEFAULT_PERFECT_SCROLLBAR_CONFIG
    }, {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpIntercepor,
      multi: true,
    }, VoteService, ProductService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
