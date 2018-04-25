import React, { Component } from "react";
import PropTypes from "prop-types";

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
      checkCodeSign: this.getCheckCodeSign(),
      defaultStep: "pre_send_click", // pre_send_click, clicked_send_click
      checkcodeInvalid: false
    };

    this.changeCheckCodeImage = this.changeCheckCodeImage.bind(this);
    this.onCheckCodeChange = this.onCheckCodeChange.bind(this);
    this.onCheckCheckCode = this.onCheckCheckCode.bind(this);
  }

  componentWillReceiveProps(nextProps) {

    if (nextProps.phoneOrEmail === '666') {
        this.setState({
            phoneOrEmailInValid: false
        });
    } else {
        
    }
  }

  componentDidUpdate(nextProps, nextState) {

  
    
    return true; // or true;

  }

  phoneOrEmailInValid() {
      return this.phoneOrEmail !== '666';
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
    console.log(event.target.value, "onCheckCodeChange");
  }

  onCheckCheckCode(event) {
    const code = event.keyCode || event.which;

    if (code !== 13) {
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
      this.setState({
        checkcodeInvalid: result.code !== 200
      });

      if (result.code !== 200) {
        return;
      }
    });
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
          disabled={ phoneOrEmail !== '666'}
          ref={input => (this.checkCodeInput = input)}
          onChange={this.onCheckCodeChange}
          onKeyDown={this.onCheckCheckCode}
          placeholder={placeholder}
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
          className="input-group-label checkcode-disable-button"
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
          style={{ display: "none" }}
        >
          &ensp;验证&ensp;
        </span>
        <span
          className="input-group-label checkcode-time-button"
          style={{ width: "auto", borderLeft: "none", display: "none" }}
        >
          重试
        </span>
        <span
          className="input-group-label checkcode-time-button"
          style={{ display: "none" }}
        >
          <i>checkcodeTimeButtonText</i>
        </span>
      </div>
    );
  }
}

CheckCodeInputComponent.propTypes = propTypes;
CheckCodeInputComponent.defaultProps = defaultProps;

export default CheckCodeInputComponent;
