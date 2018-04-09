import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-check-code-input',
  templateUrl: './check-code-input.component.html',
  styleUrls: ['./check-code-input.component.css']
})
export class CheckCodeInputComponent implements OnInit {

  checkCodeValue: any;
  steps: String = 'pre_send_click';       // clicked_send_click
  placeholder: String = '请输入短信验证码';
  checkCodeTime: any = 1;
  checkCodeSrc: String ;

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
    this.checkCodeSrc = '/checkcode?time=' + this.checkCodeTime;
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
    this.checkCodeSrc = '/checkcode?time=' + ++this.checkCodeTime;
  }

}
