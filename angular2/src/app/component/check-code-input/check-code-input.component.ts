import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as _ from 'lodash';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/from';
import 'rxjs/add/observable/interval';
import { CheckcodeService } from '../../_services/_index';

@Component({
  selector: 'app-check-code-input',
  templateUrl: './check-code-input.component.html',
  styleUrls: ['./check-code-input.component.css']
})
export class CheckCodeInputComponent implements OnInit {

  checkCodeValue: any;
  steps: String = 'pre_send_click';       // clicked_send_click
  placeholder: String = '请输入验证码';
  checkCodeTime: any;
  checkCodeSrc: String ;
  checkcodeInvalid: Boolean = false;
  checkCodeLength: Number = 6;
  timerCount: any = 10;
  checkcodeTimeButtonText: String;
  checkCodeSign: String;

  @Output()
  checkCodeChange = new EventEmitter<string>();

  @Input()
  get checkCode(){
    return this.checkCodeValue;
  }

  set checkCode(val) {
    this.checkCodeValue = val;
    this.checkCodeChange.emit(this.checkCodeValue);
  }

  constructor(private checkcodeService: CheckcodeService) {
    this.changeCode();
  }

  ngOnInit() {
    this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount));
  }

  setCheckcodeTimeButtonText(text: String): void {
    this.checkcodeTimeButtonText = text;
  }

  getTimeStr(timeCount: Number): String {
    const s = timeCount.toString().length === 1 ? '0' + timeCount : timeCount;
    return '倒计时' + s + '秒';
  }

  changeSteps(val: String): void {

    if (val === 'clicked_send_click') {
      this.placeholder = '请输入图形验证码';
    }

    if (val === 'pre_send_click' || val === 'clicked_checkcode') {
      this.placeholder = '请输入验证码';
    }

    this.steps = val;

  }

  changeCode(): void {
    this.checkCodeSign = this.randomStr();
    this.checkCodeSrc = '/checkcode?sign=' + this.checkCodeSign;
  }

  resendCode(): void {
    this.changeSteps('clicked_send_click');
    this.changeCode();
    this.checkcodeInvalid = false;
    this.checkCode = null;
  }

  validateCheckCode(): void {
    // pre_send_click

    const isValid = false;

    if (isValid) {

    } else {
      // this.steps = 'clicked_send_click';
      // this.setCheckcodeTimeButtonText('验证码有误，请重新输入');
      this.checkcodeInvalid = true;
    }

  }

  randomStr(): String {
    return _.times(48, () => _.random(35).toString(36)).join('');
  }

  onKeyup(event): void {

    if ( this.steps === 'clicked_send_click') {

      if (this.checkCodeValue.length >= this.checkCodeLength) {

        if (this.checkCodeValue.length > this.checkCodeLength) {
          this.checkcodeInvalid = true;
          return;
        }

        if (this.checkCodeValue.length === this.checkCodeLength) {

          this.checkcodeInvalid = false;

          this.checkcodeService.check(this.checkCodeSign, this.checkCodeValue).subscribe(result => {
            if (result.data) {
              // good
              this.steps = 'clicked_checkcode';
              this.checkCode = null;
              this.placeholder = '已发送, 请输入验证码';

              let timer =  Observable.interval(1000).subscribe((v) => {

                this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount - v));

                if (this.timerCount - v <= 0) {
                  timer.unsubscribe();
                  timer = null;
                  this.setCheckcodeTimeButtonText('重新发送');
                }

              });

            } else {
              // bad
              // invalide checkcode
              this.checkcodeInvalid = true;
            }
          });

        }
      }

    }

  }

  onKeydown(event): void {

    const code = event.keyCode || event.which;

    if ( this.steps === 'clicked_send_click' && this.checkCodeValue && this.checkCodeValue.length >= this.checkCodeLength) {

      // 8: back, 37: left arrow
      if (code === 8 || code === 37 || code === 39) {
        return;
      }

      event.preventDefault();

    }

  }

}
