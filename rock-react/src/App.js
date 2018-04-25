import React, { Component } from 'react';
import SignupComponent from './components/signup/signup.component';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    
    super(props);

    this.state = {
      title: 'This is my first React page..',
      message: 'message',
      content: null,
      checked: true
    };

    this.onSubmit = this.onSubmit.bind(this);
    this.changeTitle = this.changeTitle.bind(this);
    this.setChecked = this.setChecked.bind(this);
    this.onTextChange = this.onTextChange.bind(this);
  }

  onItemClick() {
    alert('Clicked');
  }

  onTextChange(event) {
    console.log(event.target.value);
    this.setState(
      { 
        content: event.target.value
     }
    );
  }

  onSubmit(event) {
    event.preventDefault();
    console.log(this.input.value, 'this.input');
  }

  changeTitle() {
    this.setState(
      { 
        title: 'Title Changed',
        content: 'content changed'
     }
    );

    console.log(this.state, 'this.state');
  }

  setChecked() {
    this.setState(
      { 
        checked: !this.state.checked
     }
    );
  }

  render() {

    const list1 = [ 'Item1','Item2','Item3','Other items', ];

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <h1 className="App-intro">
          {this.state.title}
        </h1>

        <div>
        {
          true ? 'Login | Sign up' : 'Log out'
        }
        </div>
        <div>
          {
            list1.map(item => {
                return (
                  <p key={item} onClick={this.onItemClick}>{item}</p>
                );
            })
          }
        </div>

        <div>
          <input type="button" onClick={this.changeTitle} value="Change Title" />
        </div>

        <div>
          <form onSubmit={this.onSubmit} method="POST" action="/signup" >
            <input onChange={this.onTextChange} ref={input => this.input = input} />
            <input type="submit" value="Submit" />
          </form>
        </div>

        <SignupComponent message={'Pass Message to component'} content={this.state.content} onClick={this.changeTitle} />

        <input type="checkbox" checked={this.state.checked} onChange={this.setChecked} />

      </div>
    );
  }
}

export default App;
