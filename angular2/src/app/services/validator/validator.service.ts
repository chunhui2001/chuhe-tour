import { Injectable } from '@angular/core';

@Injectable()
export class ValidatorService {

  constructor() { }

  validEmail(email: string): string {

    if (!email || email.trim().length === 0) {
      return null;
    }

    const re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    // return re.test(email);

    const tmp = email.replace(/\s/g, '');

    if (tmp.match(re)) {
      return tmp.match(re)[0];
    }

    return null;
  }

  validPhone(phone: string): string {

    if (!phone || phone.trim().length === 0) {
      return null;
    }

    const re_us = /^(\d{3}-\d{3}-\d{4})$/;
    const re_cn = /^([1]+[^126]+\d{9})$/;

    // return re_us.test(phone) || re_cn.test(phone.replace(/-/g, '').replace(/\s/g, '').replace(/^(\+86)/, ''));

    if (phone.match(re_us)) {
      return phone.match(re_us)[0];
    }

    const tmp = phone.replace(/-/g, '').replace(/\s/g, '').replace(/^(\+86)/, '');
    if (tmp.match(re_cn)) {
      return tmp.match(re_cn)[0];
    }

    return null;

  }

}
