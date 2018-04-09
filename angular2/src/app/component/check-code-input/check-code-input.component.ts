import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import * as _ from 'lodash';

@Component({
  selector: 'app-check-code-input',
  templateUrl: './check-code-input.component.html',
  styleUrls: ['./check-code-input.component.css']
})
export class CheckCodeInputComponent implements OnInit {

  checkCodeValue: any;
  steps: String = 'pre_send_click';       // clicked_send_click
  placeholder: String = '请输入短信验证码';
  checkCodeTime: any;
  checkCodeSrc: String ;
  checkcodeInvalid: Boolean = false;
  checkCodeLength: Number = 6;

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

  constructor() {
    this.changeCode();
  }

  ngOnInit() {

  }

  changeSteps(val: String): void {

    if (val === 'clicked_send_click') {
      this.placeholder = '请输入图形验证码';
    }

    if (val === 'pre_send_click' || val === 'clicked_checkcode') {
      this.placeholder = '请输入短信验证码';
    }

    this.steps = val;

  }

  changeCode(): void {
    this.checkCodeSrc = '/checkcode?sign=' + this.randomStr();
  }

  randomStr(): String {
    return _.times(48, () => _.random(35).toString(36)).join('');
  }

  onKeyup(event): void {

    if ( this.steps === 'clicked_send_click' && this.checkCodeValue.length >= this.checkCodeLength) {

      if (this.checkCodeValue.length > this.checkCodeLength) {
        this.checkcodeInvalid = true;
        return;
      }

      if (this.checkCodeValue.length === this.checkCodeLength) {

        // this.steps = 'clicked_checkcode';
      }

    }

  }

  onKeydown(event): void {

    if ( this.steps === 'clicked_send_click' && this.checkCodeValue && this.checkCodeValue.length >= this.checkCodeLength) {

      event.preventDefault();

    }

  }

}
