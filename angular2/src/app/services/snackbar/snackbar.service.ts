import { Injectable } from '@angular/core';
import {MatSnackBar} from '@angular/material';

@Injectable()
export class SnackbarService {

  duration: any = 8000;

  constructor(private snackBar: MatSnackBar) { }

  show(message: string): void {
    const _this = this;
    this.snackBar.open(message, '关闭', {
      duration: _this.duration,
    });
  }

}
