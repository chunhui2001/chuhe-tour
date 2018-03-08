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
  MatChipsModule, MatProgressBarModule, MAT_LABEL_GLOBAL_OPTIONS, MatTabsModule
} from '@angular/material';


// Import your library
import { SlickModule } from 'ngx-slick';

// Ngx-Charts
import { NgxChartsModule } from '@swimlane/ngx-charts';

import { HttpIntercepor } from './_interceptor/http.interceptor';
import { routers } from './routers/app.router';

// _service
import { VoteService } from './_services/vote/vote.service';

// _components
import { BasicComponent } from './_components/basic.component';
import { AppComponent } from './_components/app/app.component';
import { SignupComponent } from './_components/signup/signup.component';
import { EmptyComponent } from './_components/empty/empty.component';
import { LoginComponent } from './_components/login/login.component';
import { ProductNewComponent } from './_components/product/product-new/product-new.component';
import { MediaUploadComponent } from './component/media-upload/media-upload.component';
import { ProductDetailsComponent } from './_components/product/product-details/product-details.component';
import { SlickCarouselComponent } from './component/slick-carousel/slick-carousel.component';

import { PerfectScrollbarModule, PERFECT_SCROLLBAR_CONFIG, PerfectScrollbarConfigInterface } from 'ngx-perfect-scrollbar';
import { ChartsComponent } from './_components/charts/charts.component';

const DEFAULT_PERFECT_SCROLLBAR_CONFIG: PerfectScrollbarConfigInterface = {
  suppressScrollX: true
};

@NgModule({
  exports: [
    MatIconModule, MatToolbarModule, MatMenuModule, MatCardModule,
    MatButtonModule, MatCheckboxModule, MatSelectModule,
    MatChipsModule, MatProgressBarModule, MatTabsModule
  ]
})
export class GoogleMaterialModule {}


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


// rxjs: doc
// https://www.sitepoint.com/ultimate-angular-cli-reference/
// https://www.learnrxjs.io/operators/filtering/takewhile.html

@NgModule({
  declarations: [
    BasicComponent,
    AppComponent,
    SignupComponent,
    EmptyComponent,
    LoginComponent,
    ProductNewComponent,
    MediaUploadComponent,
    ProductDetailsComponent,
    SlickCarouselComponent, ChartsComponent
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
    }, VoteService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
