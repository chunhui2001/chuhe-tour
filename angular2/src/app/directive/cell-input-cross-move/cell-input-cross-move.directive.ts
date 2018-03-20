import {Directive, ElementRef, Input, OnInit, Renderer2} from '@angular/core';

@Directive({
  selector: '[appCellInputCrossMove]'
})
export class CellInputCrossMoveDirective implements OnInit {

  constructor(private elem: ElementRef, private renderer: Renderer2) {
    // renderer.setStyle(elem.nativeElement, 'box-shadow', '2px 2px 12px #58A362');
  }


  ngOnInit() {
    // const shadowStr = `${ this.appShadowX } ${ this.appShadowY } ${ this.appShadowBlur } ${ this.appShadow }`;
    // this.renderer.setStyle(this.elem.nativeElement, 'box-shadow', shadowStr);
    // this.renderer.setStyle(this.elem.nativeElement, 'background-color', 'red');
    const _this = this;
    $(this.elem.nativeElement).on('keyup', function (event) {

      const code = event.keyCode || event.which;
      const currentTdIndex = $($(_this.elem.nativeElement).parents('td')[0]).prevAll('td').length;
      const currentTr = $($(_this.elem.nativeElement).parents('tr')[0]);

      if (code === 38) {
        // up
        $(currentTr).prev('tr').find('td:eq(' + currentTdIndex + ')').find('input:text').focus();
      } else if (code === 40) {
        // down
        $(currentTr).next('tr').find('td:eq(' + currentTdIndex + ')').find('input:text').focus();
      }

    });
  }

}
