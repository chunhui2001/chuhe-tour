import {Directive, ElementRef, OnInit, Renderer2} from '@angular/core';

@Directive({
  selector: '[appSelectTextOnFocus]'
})
export class SelectTextOnFocusDirective implements OnInit {

  constructor(private elem: ElementRef, private renderer: Renderer2) {

  }


  ngOnInit() {

    const _this = this;

    $(this.elem.nativeElement).on('focus', function (event) {

      setTimeout(() => {
        if (_this.elem.nativeElement.value.length > 0) {
          _this.elem.nativeElement.setSelectionRange(0, _this.elem.nativeElement.value.length);
        }
      }, 50);

    });
  }

}
