import {
  Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild,
  ViewChildren
} from '@angular/core';

@Component({
  selector: 'app-float-input',
  templateUrl: './float-input.component.html',
  styleUrls: ['./float-input.component.css']
})
export class FloatInputComponent implements OnInit {

  @Input() model: any;
  @Input() field: any;
  @Output() onInput: EventEmitter<any> = new EventEmitter();


  @ViewChild('input_float') input_float: ElementRef;

  constructor() { }

  ngOnInit() {
  }

  onKeydown(event): void {

    const code = event.keyCode || event.which;


    console.log(event.key, 'code 2');
    console.log(code, 'code 2');
    if ((code < 48 || code > 57) && [8, 9, 13, 16, 17, 37, 39, 91, 190].indexOf(code) === -1) {

      if (event.getModifierState
          && (event.getModifierState('Meta') || event.getModifierState('Control'))
          && (event.keyCode === 65 || event.keyCode === 67 || event.keyCode === 86 || event.keyCode === 88)) {

      } else {
        event.preventDefault();
      }


    }

  }

  focused(): void {
    this.input_float.nativeElement.focus();
    // const inputElem = <HTMLInputElement>this.input_float.nativeElement;
    // inputElem.select();
    // inputElem.setSelectionRange(0, 4);
  }

  onFloatChange(val: string): void {
    this.model[this.field] = isNaN(parseFloat(this.model[this.field])) ? '' : parseFloat(this.model[this.field]).toFixed(2);
    this.onInput.emit();
  }


}
