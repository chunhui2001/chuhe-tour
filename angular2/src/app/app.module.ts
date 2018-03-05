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
  MatChipsModule, MatProgressBarModule, MAT_LABEL_GLOBAL_OPTIONS
} from '@angular/material';


// Import your library
import { SlickModule } from 'ngx-slick';

// https://www.learnrxjs.io/operators/filtering/takewhile.html

import { HttpIntercepor } from './_interceptor/http.interceptor';
import { routers } from './routers/app.router';

import { BasicComponent } from './_components/basic.component';
import { AppComponent } from './_components/app/app.component';
import { SignupComponent } from './_components/signup/signup.component';
import { EmptyComponent } from './_components/empty/empty.component';
import { LoginComponent } from './_components/login/login.component';
import { ProductNewComponent } from './_components/product/product-new/product-new.component';
import { MediaUploadComponent } from './component/media-upload/media-upload.component';
import { ProductDetailsComponent } from './_components/product/product-details/product-details.component';

@NgModule({
  declarations: [
    BasicComponent,
    AppComponent,
    SignupComponent,
    EmptyComponent,
    LoginComponent,
    ProductNewComponent,
    MediaUploadComponent,
    ProductDetailsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule, FormsModule, ReactiveFormsModule, BrowserAnimationsModule,
    FroalaEditorModule.forRoot(), FroalaViewModule.forRoot(),
    MatIconModule, MatToolbarModule, MatMenuModule, MatCardModule, MatButtonModule,
    MatCheckboxModule, MatSelectModule, MatChipsModule, MatProgressBarModule,
    routers, AngularFontAwesomeModule, SlickModule.forRoot()
  ],
  providers: [{
    provide: MAT_LABEL_GLOBAL_OPTIONS, useValue: {float: 'always'}
  }, {
    provide: HTTP_INTERCEPTORS,
    useClass: HttpIntercepor,
    multi: true,
  }],
  bootstrap: [AppComponent]
})
export class AppModule { }
