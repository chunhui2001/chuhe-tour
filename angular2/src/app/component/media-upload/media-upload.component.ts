import {Component, Input, Output, OnInit, EventEmitter, AfterViewInit, ElementRef} from '@angular/core';
import * as $ from 'jquery';

import * as _ from 'lodash';

@Component({
  selector: 'app-media-upload',
  templateUrl: './media-upload.component.html',
  styleUrls: ['./media-upload.component.css']
})
export class MediaUploadComponent implements OnInit, AfterViewInit {


  orderableLists = [
    "Item 1a",
    "Item 2a",
    "Item 3a"
  ]


  @Input()
  uploadFieldName: String;

  @Input()
  mediasHiddenField: String;

  @Input()
  medias: any = [];

  @Output()
  onReupload: EventEmitter<any> = new EventEmitter();

  medias_field: String;
  uploadFiles: any = [];

  constructor(private myElement: ElementRef) {

  }


  ngAfterViewInit() {
    const _that = this;
    const _upload_hand_selector = '.media-upload-component .upload_hand';

    $(document).on('click', _upload_hand_selector, function (event) {

      const currentComponentInstance = $(_that.myElement.nativeElement);
      const currentFileUploadElement = $(currentComponentInstance).find('.upload_holder');

      $(currentFileUploadElement).unbind('change').on('change', function (ev) {
        const currentFiles = (<HTMLInputElement>ev.target).files;
        _that.appendNewUploadFile(currentFileUploadElement, _that.uploadFieldName, currentFiles);
      });

      $(currentFileUploadElement).click();

    });
  }

  ngOnInit() {

    this.medias_field = this.medias.join(',');

  }

  appendNewUploadFile(fileUploadElementHolder, formFieldName, filesList) {

    if (filesList.length === 0) {
      return;
    }

    const _that = this;
    const _appendPlaceHolder = $($(fileUploadElementHolder).parents('div')[0]).find('input:file').last()
    const newFile = $(fileUploadElementHolder).clone();

    $(newFile).insertAfter(_appendPlaceHolder);

    $(fileUploadElementHolder).attr('class', formFieldName);
    $(fileUploadElementHolder).attr('name', formFieldName);

    // $(fileUploadElementHolder).show();

    let prviewName = '';

    if (filesList.length === 1) {
      prviewName = filesList[0].name[0].toUpperCase();
    } else {
      prviewName = '+' + filesList.length;
    }

    _that.uploadFiles.push(prviewName);

  }

  reupload() {
    this.onReupload.emit();
  }

  builderDrop(event): void {

    const _that = this;

    this.medias_field = $(this.myElement.nativeElement)
      .find('.media_preview.thumbnail')
      .map(function (){
        return _that.getBackgroundImageUrl(this);
    }).get().join(',');

  }

  removeProductMedia(event, url: String): void {

    const _that = this;
debugger;
    const result = $(this.myElement.nativeElement)
      .find('.media_preview.thumbnail').filter(function () {
      return _that.getBackgroundImageUrl(this) === url;
    });

    this.medias_field = _.trimEnd(_.trimStart((',' + this.medias_field + ',')
                        .replace(',' + url + ',', ',').trim(), ','), ',');

    $(result).remove();

  }

  getBackgroundImageUrl(element: any): String {
    return $(element).attr('data-url');
    // return '/' + $(element).css('background-image').replace(/^url\(['"](.+)['"]\)/, '$1')
    //  .replace (/^[a-z]{4,5}\:\/{2}[a-zA-Z.]{1,}(\:[0-9]{1,5})?.(.*)/, '$2');
  }

}
