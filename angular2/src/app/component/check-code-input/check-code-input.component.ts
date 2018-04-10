import { AfterViewInit, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
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
export class CheckCodeInputComponent implements OnInit, AfterViewInit {

  @Input()
  isDisable: Boolean;

  @Input()
  phoneOrEmail: any;

  checkCodeValue: any;

  steps: String = 'pre_send_click';       // clicked_send_click
  placeholder: String = '请输入验证码';
  checkCodeSrc: String ;
  checkcodeInvalid: Boolean = false;
  checkCodeLength: Number = 6;
  timerCount: any = 10;
  checkcodeTimeButtonText: String;
  checkCodeSign: String;
  checkNewSign: String;             // 通过图片验证之后，服务器会返回一个新的sign, 拿着这个新的sign去服务器验证用户收到的短信验证码(或邮件验证码)
  isValidate: boolean;
  timer: any;

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

  }

  ngOnInit() {
    this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount));
  }

  ngAfterViewInit() {

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
      this.changeCode();
      this.placeholder = '请输入图形验证码';
    }

    if (val === 'pre_send_click' || val === 'clicked_checkcode') {
      this.placeholder = '请输入验证码';
    }

    this.steps = val;

  }

  changeCode(): void {
    this.checkCodeSign = this.randomStr();
    this.checkCodeSrc = '/checkcode?sign=' + this.checkCodeSign + '&receiver=' + this.phoneOrEmail;
  }

  resendCode(): void {

    if (this.timer !== null || this.isValidate) {
      return;
    }

    this.changeSteps('clicked_send_click');
    this.changeCode();
    this.checkcodeInvalid = false;
    this.checkCode = null;
    this.isValidate = null;
  }

  validateCheckCode(): void {
    // pre_send_click

    this.isValidate = null;

    if (this.checkNewSign) {

      alert(this.checkNewSign);
      this.isValidate = true;
      this.setCheckcodeTimeButtonText('验证成功');

      if (this.timer != null) {
        this.timer.unsubscribe();
        this.timer = null;
      }

    } else {
      this.isValidate = false;
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
          this.checkCodeCheck();
        }
      }

    }

  }

  checkCodeCheck(): void {

    this.checkcodeInvalid = false;

    this.checkcodeService.check(this.checkCodeSign, this.checkCodeValue, this.phoneOrEmail).subscribe(result => {

      if (result.code === 200) {

        // good and get new sign
        this.checkNewSign = result.data.sign;
        this.timerCount = result.data.seconds;
        this.steps = 'clicked_checkcode';
        this.checkCode = null;
        this.placeholder = '已发送, 请输入验证码';
        this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount));

        this.timer = Observable.interval(1000).subscribe((v) => {

          this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount - v - 1));

          if (this.timerCount - v <= 0) {
            this.timer.unsubscribe();
            this.timer = null;
            this.setCheckcodeTimeButtonText('重新发送');
          }

        });

      } else {
        // bad
        // invalide checkcode
        this.checkNewSign = null;
        this.checkcodeInvalid = true;
      }
    });

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
