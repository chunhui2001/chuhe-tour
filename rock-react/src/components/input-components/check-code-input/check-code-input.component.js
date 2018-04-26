import React, { Component } from "react";
import PropTypes from "prop-types";

import { Subject, Observable } from 'rxjs';

import _times from "lodash/times";
import _random from "lodash/random";

import CheckCodeService from "../../../services/checkcode.service.js";

import "./check-code-input.component.css";

const propTypes = {
  placeholder: PropTypes.string,
  phoneOrEmail: PropTypes.string
};

const defaultProps = {
  placeholder: "请输入验证码.."
};

class CheckCodeInputComponent extends Component {
  constructor(props) {

    super(props);

    this.state = {
      placeholder: this.props.placeholder,
      checkCodeSign: this.getCheckCodeSign(),
      checkNewSign: null,                   // 通过图片验证后返回的新签名
      timerCount: 10,                       // 默认倒计时时间
      checkcodeTimeButtonText: null,        // 
      defaultStep: "pre_send_click",        // pre_send_click, clicked_send_click, clicked_checkcode
      checkcodeInvalid: null,              // 针对图片验证码是否通过的标识
      checkcodeIsValid: null,              // 针对手机或邮件验证码是否通过的标识 
      phoneOrEmailInValid: true,            // 针对手机或邮箱格式是否正确的标识   
    };

    this.changeCheckCodeImage = this.changeCheckCodeImage.bind(this);
    this.onCheckCodeChange = this.onCheckCodeChange.bind(this);
    this.onCheckCheckCode = this.onCheckCheckCode.bind(this);
  }

  componentWillReceiveProps(nextProps) {

    if (nextProps.phoneOrEmail === '18500183080') {
        this.setState({
          phoneOrEmailInValid: false
        });
    } else {
        
    }
  }

  componentDidUpdate(nextProps, nextState) {
    return true; // or true;
  }

  changeSteps(step) {
    this.setState({
      defaultStep: step
    });
  }

  changeCheckCodeImage() {
    this.setState({
      checkCodeSign: this.getCheckCodeSign()
    });
  }

  getCheckCodeSign() {
    return _times(48, () => _random(35).toString(36)).join("");
  }

  onCheckCodeChange(event) {
    // console.log(event.target.value, "onCheckCodeChange");
  }

  onValidateCheckCode() {
    alert(this.checkCodeInput.value);
  }

  onCheckCheckCode(event) {

    const code = event.keyCode || event.which;

    if (code !== 13 || this.checkCodeInput.value.trim().length == 0) {
      return;
    }

    if (this.state.checkcodeInvalid !== null && !this.state.checkcodeInvalid) {
      this.onValidateCheckCode();
      return;
    }

    // check checkCode
    // post http://52.192.42.233:8081/checkcode

    CheckCodeService.checkCheckCode({
      checkcode: this.checkCodeInput.value,
      checktype: "image",
      receiver: this.props.phoneOrEmail,
      sign: this.state.checkCodeSign
    }).then(result => {

      let good = false;

      if (result.code === 200) {
        good = true;
      }
      
      this.setState({
        checkcodeInvalid: !good,
        placeholder: good ? "验证码已发送" : this.props.placeholder,
        checkNewSign: good ? result.data.sign : null,
        timerCount: good ? result.data.seconds : this.state.timerCount
      });

      if (!good) {
        return;
      }

      this.runTimerInterval();
      this.changeSteps('clicked_checkcode') ;
      this.checkCodeInput.value = null;

    });
  }

  retry() {
    
    if (this.state.interval) clearInterval(this.state.interval);

    this.setState({
      checkcodeInvalid: null,
      placeholder: this.props.placeholder,
      checkNewSign: null,
      timerCount: 10
    });

    this.changeSteps("clicked_send_click");
    this.changeCheckCodeImage();

  }

  runTimerInterval() {
    this.runInterval(() => {
      this.setState({
        checkcodeTimeButtonText: this.getTimeStr(this.state.timerCount)
      });
    }, () => {
      this.setState({
        timerCount: this.state.timerCount - 1
      });
    }, () => {
      return this.state.timerCount <= 0;
    });

  }

  runInterval(func, prefunc, stop) {

    func();

    this.state.interval = setInterval(() => {
      if (prefunc) prefunc();
      if (stop()) clearInterval(this.state.interval);
      func();
    } , 1000);

  }

  getTimeStr(timeCount) {
    const s = timeCount.toString().length === 1 ? '0' + timeCount : timeCount;
    
    return (<span style={{fontSize: '.825em'}}>倒计时 <b style={{display:'inline-block', width: '22px',fontSize: '1.5em'}}> {s} </b> 秒</span>);

  }

  render() {
    
    const { placeholder, phoneOrEmail, watch } = this.props;

    // watch(phoneOrEmail, (newPhoneOrEmail) => {
    //     console.log(newPhoneOrEmail, 'newPhoneOrEmail');
    // });

    return (
      <div className="check-code-input-component input-group">
      
        <span
          className="input-group-label"
          style={{ padding: "0 .425rem", color: "black" }}
        >
          <i className="material-icons">&#xE8CE;</i>
        </span>
        <input
          className={
            "input-group-field " +
            (this.state.checkcodeInvalid ? "checkcode-invalid" : "")
          }
          type="text"
          pattern="[a-z0-9]{1,15}"
          disabled={ this.state.phoneOrEmailInValid }
          ref={input => (this.checkCodeInput = input)}
          onChange={this.onCheckCodeChange}
          onKeyDown={this.onCheckCheckCode}
          placeholder={this.state.placeholder}
          id="check_code"
          name="check_code"
          required
        />

        <input type="hidden" id="check_code_sign" name="check_code_sign" />
        <input
          type="hidden"
          id="validate_code_sign"
          name="validate_code_sign"
        />

        <span
          className={"input-group-label " + (this.state.phoneOrEmailInValid ? "checkcode-disable-button" : "")}
          onClick={() => this.changeSteps("clicked_send_click")}
          style={{
            cursor: "pointer",
            padding: "0 .425rem",
            color: "blueviolet",
            borderLeft: "none",
            backgroundColor: "antiquewhite",
            display:
              this.state.defaultStep === "pre_send_click" ? "flex" : "none"
          }}
        >
          发送验证码
        </span>
        <span
          className="input-group-label checkcode "
          onClick={this.changeCheckCodeImage}
          style={{
            width: "104px",
            borderLeft: "none",
            backgroundSize: "100% 100%",
            display:
              this.state.defaultStep === "clicked_send_click" ? "flex" : "none",
            backgroundImage:
              'url("' +
              "/checkcode?sign=" +
              this.state.checkCodeSign +
              "&receiver=18500183080" +
              '")'
          }}
        />
        <span
          className="input-group-label checkcode-valid-button checkcode-correct"
          onClick={() => this.onValidateCheckCode() }
          style={{ borderLeft: "none", display: this.state.checkcodeInvalid === null || this.state.checkcodeInvalid ? "none" : "flex" }}
        >
        验证
        </span>
        <span
          className="input-group-label checkcode-valid-button"
          onClick={() => this.retry()}
          style={{ width: "auto", borderLeft: "none", display: this.state.checkcodeInvalid === null || this.state.checkcodeInvalid ? "none" : "flex" }}
        >
          重试
        </span>
        <span
          className="input-group-label checkcode-time-button"
          style={{ display: this.state.checkcodeInvalid === null || this.state.checkcodeInvalid ? "none" : "flex" }}
        >
          {this.state.checkcodeTimeButtonText}
        </span>
      </div>
    );
  }
}

CheckCodeInputComponent.propTypes = propTypes;
CheckCodeInputComponent.defaultProps = defaultProps;

export default CheckCodeInputComponent;
