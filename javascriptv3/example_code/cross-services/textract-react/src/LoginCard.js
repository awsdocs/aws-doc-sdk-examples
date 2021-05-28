// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React from "react";

/**
 * Displays a panel that lets a user navigate to an Amazon Cognito sign in page.
 *
 * @param props: Properties that determine how the panel is displayed.
 *        props.loginUrl: The URL to the Amazon Cognito sign in page.
 * @returns {JSX.Element}
 */
export const LoginCard = (props) => {
  return (
    <div className="card mb-3">
      <div className="card-body">
        <h5 className="card-title">Sign in</h5>
        <h6 className="card-subtitle mb-2 text-muted">
          You must first sign in with Amazon Cognito to use this application.
        </h6>
        <a type="button" className="btn btn-primary" href={props.loginUrl}>
          Sign in
        </a>
      </div>
    </div>
  );
};
