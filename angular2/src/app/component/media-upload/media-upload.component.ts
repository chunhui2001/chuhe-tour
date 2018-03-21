import {Component, Input, Output, OnInit, EventEmitter, AfterViewInit} from '@angular/core';
import * as $ from 'jquery';



@Component({
  selector: 'app-media-upload',
  templateUrl: './media-upload.component.html',
  styleUrls: ['./media-upload.component.css']
})
export class MediaUploadComponent implements OnInit, AfterViewInit {

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

  constructor() {

  }


  ngAfterViewInit() {
    const _that = this;
    const _upload_hand_selector = '.media-upload-component .upload_hand';

    $(document).on('click', _upload_hand_selector, function (event) {

      const currentComponentInstance = $($(event.target).parents('.media-upload-component')[0]);
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

}
