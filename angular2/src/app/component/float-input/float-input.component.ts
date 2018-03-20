import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-float-input',
  templateUrl: './float-input.component.html',
  styleUrls: ['./float-input.component.css']
})
export class FloatInputComponent implements OnInit {

  @Input() model: any;
  @Input() field: any;
  @Output() onInput: EventEmitter<any> = new EventEmitter();

  constructor() { }

  ngOnInit() {
  }

  onKeydown(event): void {

    const code = event.keyCode || event.which;

    if ((code < 48 || code > 57) && [8, 9, 13, 16, 17, 37, 39, 67, 86, 188, 190].indexOf(code) === -1) {
      event.preventDefault();

    }

  }

  onPriceChange(val: string): void {
    this.onInput.emit();
  }


}
