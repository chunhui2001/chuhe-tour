import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { HttpIntercepor } from './_interceptor/http.interceptor';
import { routers } from './routers/app.router';

import { AppComponent } from './_components/app/app.component';
import { SignupComponent } from './_components/signup/signup.component';
import { EmptyComponent } from './_components/empty/empty.component';

@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    EmptyComponent
  ],
  imports: [
    BrowserModule,
    routers,
    BrowserAnimationsModule,
    FormsModule,
    HttpModule
  ],
  providers: [
  	{
      provide: HTTP_INTERCEPTORS,
      useClass: HttpIntercepor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
