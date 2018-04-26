import { observable, action } from 'mobx'

import _assign from 'lodash/assign';

// https://yarnpkg.com/lang/zh-hans/docs/install/#mac-stable
// sudo port install yarn
// yarn run eject

// https://www.youtube.com/watch?v=ZA1brjm7zDA
// https://www.youtube.com/watch?v=Dp75-DnGFrU

class CheckCodeService {

    @observable all = [];
    @observable isLoading = false;

    @action async fetchAll() {

        this.isLoading = true;

        const response = await fetch('http://www.baidu.com');
        const status = await response.status;

        if (status === 200) {
            this.all = await response.json();
        }

        this.isLoading = false;

    }

    @action async checkCheckCode(data) {

        const postData = _assign({ 
            checkcode:"tttttt",
            checktype:"image",
            receiver:"18500183080",
            sign:"8qwbcyp21u6zzzgk65a22u6nujl7k51t3kd8cgm2msmj5u6n"}, data);

        const headers = new Headers();

        headers.append('Content-Type', 'application/json');

        const options = {
            method: 'POST',
            headers,
            body: JSON.stringify(postData)
        };

        const request = new Request('/checkcode', options);
        const response = await fetch(request);

        const status = await response.status;

        // 201 created
        if (status === 200) {
            return await response.json();
        }

    }

}

export default new CheckCodeService();