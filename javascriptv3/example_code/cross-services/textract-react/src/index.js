// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * The main entry point for the React application.
 *
 * Gets the Amazon Cognito token from the URL query parameters and uses it to
 * initialize AWS client objects.
 *
 * Creates a model layer that handles moving data between AWS and the user interface.
 *
 * Subscribes the main application render function to the model so that the application
 * can be rendered whenever the underlying model changes.
 */

import "bootstrap/dist/css/bootstrap.css";
import React from "react";
import ReactDOM from "react-dom";
import { Config } from "./Config.js";
import { awsFactory } from "./AwsFactory.js";
import App from "./App.js";
import TextractModel from "./TextractModel.js";

const params = new URLSearchParams(window.location.hash.slice(1));
const idToken = params.get("id_token");
let awsClients = null;
if (typeof Config.CognitoId !== "undefined") {
  awsClients = awsFactory({
    cognitoId: Config.CognitoId,
    cognitoToken: idToken,
    cognitoIdentityPoolId: Config.CognitoIdentityPoolId,
    deployRegion: Config.DeployRegion,
  });
}
const model = new TextractModel({
  ...awsClients,
  ...Config,
});

const render = () =>
  // biome-ignore lint/correctness/noRenderReturnValue: Only invalid in new react versions
  ReactDOM.render(
    <React.StrictMode>
      <App model={model} isSignedIn={idToken !== null} config={Config} />
    </React.StrictMode>,
    document.getElementById("root"),
  );

model.subscribe(render);
render();
