import React, { Component } from "react";
import PropTypes from "prop-types";


import CheckCodeInputComponent from '../input-components/check-code-input/check-code-input.component';

import './signup.component.css';

const propTypes = {
  message: PropTypes.string.isRequired,
  onClick: PropTypes.func
};

const defaultProps = {
  content: "default Content here"
};

class SignupComponent extends Component {
  componentWillMount() {
    console.log("componentWillMount");
  }

  componentDidMount() {
    console.log("componentDidMount");
  }

  componentWillUpdate(prevProps, prevState) {
    console.log(
      this.props,
      this.state,
      prevProps,
      prevState,
      "componentWillUpdated"
    );
  }

  shouldComponentUpdate(nextProps, nextState) {
    console.log(
      this.props,
      this.state,
      nextProps,
      nextState,
      "shouldComponentUpdate"
    );
    return true; // or true;
  }

  constructor(props) {

    super(props);

    this.state = {
      title: "Sign up Component",
      phoneOrEmail: null
    };

    this.onSubmit = this.onSubmit.bind(this);
    this.onPhoneOrEmailChange = this.onPhoneOrEmailChange.bind(this);

  }

  onSubmit(event) {
    event.preventDefault();

    console.log(this.userName.value, 'this.userName');
    console.log(this.passwd.value, 'this.passwd');
    console.log(this.phoneOrEmail.value, 'this.phoneOrEmail');
  }

  onPhoneOrEmailChange(event) {

    this.setState({ 
      phoneOrEmail: event.target.value
    });
  }

  render() {
    const { message, onClick, content } = this.props;

    return (
      <div className="signup-component-wapper">
        <div className="signup-component">
          <div style={{display: 'none'}}>
            {this.state.title}, {message},
            <a href="javascript:void(this)" onClick={onClick}>
              Click Me
            </a>,
            {content || defaultProps.content}
          </div>

          <span className="flash-message" style={{display: 'none'}}>注册失败</span>

          <div className="form-container signup-component">
            <h5 style={{textAlign: 'left',fontWeight: 'bold', fontSize: '.8em',color: 'dimgray'}}>
              农资经营者管理软件
            </h5>

            <div className="input-group">
                <img style={{width: '25px', height: '25px', marginRight: '.425em'}} src="/assets/images/chuhe-logo.png" />
              <span style={{lineHeight: '25px'}}>新用户注册</span>
            </div>


            <form onSubmit={this.onSubmit} id="signup-form" action="/registry" method="POST">

              <div className="input-group">
                <span className="input-group-label" style={{padding: '0 .425rem', color: 'black'}}>
                  <i className="material-icons">&#xE8D3;</i>
                </span>
                <input className="input-group-field" type="text"  ref={input => this.userName = input}
                  placeholder="请输入用户名或邮箱地址" id="user_name" name="user_name" required />
              </div>

              <div className="input-group">
                <span className="input-group-label" style={{padding: '0 .425rem', color: 'black'}}>
                  <i className="material-icons">&#xE897;</i>
                </span>
                <input className="input-group-field" type="password" ref={input => this.passwd = input}
                  placeholder="请输入密码" id="user_passwd" name="user_passwd" required />
              </div>

              <div className="input-group">
                <span className="input-group-label" style={{padding: '0 .425rem', color:'black'}}>
                  <i className="material-icons">&#xE0CD;</i>
                </span>
                <input className="input-group-field" type="text" ref={input => this.phoneOrEmail = input}
                  onChange={this.onPhoneOrEmailChange}
                  placeholder="请输入手机号码或电子邮箱地址" id="user_phone" name="user_phone" required />
              </div>

              <CheckCodeInputComponent phoneOrEmail={ this.state.phoneOrEmail } placeholder={'请输入验证码'} />

              <div style={{textAlign: 'right', fontSize: '.9em'}}>
                <a href="/login">登录</a>
                <span className="split">&Iota;</span>
                <a href="javascript:void(this);">忘记密码!</a>
              </div>

              <div style={{textAlign: 'left', marginTop: '1em', marginBottom: '2em'}}>
                <button style={{marginBottom: '0'}} type="submit"
                        className="basic-btn button">立即注册</button>
              </div>

            </form>

          </div>
        </div>
      </div>
    );
  }
}

SignupComponent.propTypes = propTypes;
SignupComponent.defaultProps = defaultProps;

export default SignupComponent;
