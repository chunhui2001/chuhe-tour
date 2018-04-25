import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import AppRouterComponent from './components/app-router/app-router.component';
import registerServiceWorker from './registerServiceWorker';

// http://blog.jakoblind.no/4-libraries-to-use-in-your-react-app/

if (document.getElementById('root')) {
    ReactDOM.render(<App />, document.getElementById('root'));
}

if (document.getElementById('root2')) {
    ReactDOM.render(<AppRouterComponent />, document.getElementById('root2'));
}

registerServiceWorker();
