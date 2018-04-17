import {
  AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, Renderer2,
  ViewChild
} from '@angular/core';
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

  @Input() check_input_element: ElementRef;

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
  validateCodeSign: String;
  checkNewSign: String;             // 通过图片验证之后，服务器会返回一个新的sign, 拿着这个新的sign去服务器验证用户收到的短信验证码(或邮件验证码)
  validSuccess: boolean;
  validProgress: boolean;
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

  constructor(private element: ElementRef, private renderer: Renderer2, private checkcodeService: CheckcodeService) {

  }

  ngOnInit() {
    this.setCheckcodeTimeButtonText(this.getTimeStr(this.timerCount));
  }

  ngAfterViewInit() {
    // this.renderer.selectRootElement(this.check_input_element['nativeElement']).focus();
    // this.check_input_element.nativeElement.focus();

    const _this = this;

    $(this.check_input_element).on('keydown', function (e) {
      _this.onUserInput(_this, e);
    });
  }

  setCheckcodeTimeButtonText(text: String): void {
    this.checkcodeTimeButtonText = text;
  }

  getTimeStr(timeCount: Number): String {
    const s = timeCount.toString().length === 1 ? '0' + timeCount : timeCount;
    return '倒计时' + s + '秒';
  }

  changeSteps(val: String): void {

    if (this.isDisable) {
      return ;
    }

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

    if (this.isDisable) {
      return ;
    }

    this.checkCodeSign = this.randomStr();
    this.checkCodeSrc = '/checkcode?sign=' + this.checkCodeSign + '&receiver=' + this.phoneOrEmail.replace(/\s+/g, '');
  }

  resendCode(clearTimer: boolean): void {

    if (clearTimer) {
      if (this.timer != null) {
        this.timer.unsubscribe();
        this.timer = null;
      }
    } else {
      if (this.timer !== null || this.validSuccess) {
        return;
      }
    }


    this.changeSteps('clicked_send_click');
    this.changeCode();
    this.checkcodeInvalid = false;
    this.checkCode = null;
    this.validSuccess = null;
    this.freezeInputBox(false);
  }

  validateCheckCode(): void {

    if (this.validSuccess) {
      return;
    }

    const _that = this;

    if (this.checkNewSign && this.checkCode) {

      this.validateProgress();

      this.checkcodeService.check(this.checkNewSign, this.checkCode, this.phoneOrEmail, 'email').subscribe(result => {

        setTimeout(() => {

          if (result.code !== 200) {

            _that.validateFailed();
            return;
          }

          _that.validateCodeSign = result.data;

          this.validateSuccess();

        }, 500);

      });

    } else {
      _that.validateFailed();
    }

  }

  validateProgress(): void {
    this.validSuccess = null;
    this.validProgress = true;
    this.freezeInputBox(true);
  }

  validateFailed(): void {
    this.validSuccess = false;
    this.checkcodeInvalid = true;
    this.validProgress = false;
  }

  freezeInputBox(t: boolean): void {
    if (!t) {
      $(this.check_input_element).removeClass('input-freeze');
      return;
    }
    $(this.check_input_element).addClass('input-freeze');
  }

  validateSuccess(): void {

    this.validSuccess = true;
    this.checkcodeInvalid = false;
    this.validProgress = false;
    this.setCheckcodeTimeButtonText('验证成功');
    this.freezeInputBox(true);

    if (this.timer != null) {
      this.timer.unsubscribe();
      this.timer = null;
    }
  }

  randomStr(): String {
    return _.times(48, () => _.random(35).toString(36)).join('');
  }

  onUserInput(_this, event): void {

    const code = event.keyCode || event.which;

    if ((this.steps === 'clicked_checkcode' || this.validSuccess) && !_this.isInputTabKey(event, code)) {
      event.preventDefault();
    }

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

    this.checkcodeService.check(this.checkCodeSign, this.checkCodeValue, this.phoneOrEmail, 'image').subscribe(result => {

      if (result.code === 200) {

        // good and get new sign
        this.checkNewSign = result.data.sign;

        if (result.data.seconds) {
          this.timerCount = result.data.seconds;
        }

        this.freezeInputBox(true);
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
        this.freezeInputBox(false);
      }
    });

  }

  isInputTabKey(event, code: Number): Boolean {

    if ((code === 9)) {
      return true;   // tab
    }

    if (event.getModifierState
      && (event.getModifierState('Meta') || event.getModifierState('Control'))
      && (code === 9)) {
      return true;   // shift + tab
    }

    return false;
  }

  onKeydown(event) {

    const code = event.keyCode || event.which;

    if (code === 229) {

      event.preventDefault();
      event.stopPropagation();
      return;
    }

    if (this.validSuccess) {
      // 一旦验证成功，冻结输入框，不让用户在改, 但是可以按tab键

      if ((code === 9)) {
        return;   // tab
      }

      if (event.getModifierState
        && (event.getModifierState('Meta') || event.getModifierState('Control'))
        && (code === 9)) {
        return;   // shift + tab
      }

      event.preventDefault();
      return;
    }

    if ( this.steps === 'clicked_send_click' && this.checkCodeValue && this.checkCodeValue.length >= this.checkCodeLength) {

      // 8: back, 37: left arrow
      if (code === 8 || code === 9 || code === 37 || code === 39) {
        return;
      }

      event.preventDefault();

    }

  }

}
