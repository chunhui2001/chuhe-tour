import React, { Component } from 'react';

import { BrowserRouter as Router, Route, Link } from 'react-router-dom'

import SignupComponent from '../signup/signup.component';

class BasicRouterExample extends Component {
    render() {
        return (
            <Router>
        <div className="router">

            {/* <ul>
                <li> <Link to="/">Home</Link> </li>
                <li> <Link to="/about">Abount</Link> </li>
                <li> <Link to="/topics">Topics</Link> </li>
                <li> <Link to="/products">Product</Link> </li>
                <li> <Link to="/signup">Sign up</Link> </li>
            </ul>
            <hr /> */}

            <Route exact path="/" component={HomeComponent} />
            <Route path="/about" component={AboutComponent} />
            <Route path="/topics" component={TopicsComponent} />
            <Route path="/products" component={ProductsComponent} />
            <Route path="/signup" component={SignupPageComponent} />

        </div>
    </Router>
        );
    }
}

class HomeComponent extends Component {

    render() {
        return (
            <div>
                <h1>Home Page</h1>
            </div>
        );
    }
    
}
class AboutComponent extends Component {
    render() {
        return (
            <div>
                <h1>About Page</h1>
            </div>
        );
    }
}
class TopicsComponent extends Component {
    render() {
        return (
            <div>
        <h1>Topics Page</h1>
    </div>
        );
    }
}
class ProductsComponent extends Component {
    render() {
        return (
            <div>
        <h1>Products Page</h1>
    </div>
        );
    }
}

class SignupPageComponent extends Component {
    render() {
        return (
            <div >
        <SignupComponent message={'哈哈'} />
    </div>
        );
    }
}

class AppRouterComponent extends Component {


    constructor(props) {
        super(props);

    }


    render() {
  
        return (

            <BasicRouterExample />

        );

    }

}


export default AppRouterComponent;