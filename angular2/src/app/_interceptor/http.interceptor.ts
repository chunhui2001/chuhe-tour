import { Injectable } from '@angular/core';
import { HttpEvent, HttpRequest, HttpHandler, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';


// import { environment } from '../environments/environment';

@Injectable()
export class HttpIntercepor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    /*const authReq = req.clone({
      headers: req.headers.set('Authorization', 'token [pick token from github profile setting page] ')
    }); */

    const _req = req.clone();

    return next.handle(_req);

  }

}
