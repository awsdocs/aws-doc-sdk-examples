// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * The base rendering of the React application.
 */

import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';

import 'bootstrap/dist/css/bootstrap.min.css';

// The REST service for this examples is run by Flask, which uses port 5000.
const apiUrl = 'http://localhost:5000';

ReactDOM.render(
  <React.StrictMode>
    <App apiUrl={apiUrl}/>
  </React.StrictMode>,
  document.getElementById('root')
);
