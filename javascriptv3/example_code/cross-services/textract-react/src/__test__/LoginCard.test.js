// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the LoginCard component.

import React from "react";
import {render, screen } from '@testing-library/react';

import {LoginCard} from "../LoginCard";

describe('login URL props', () => {
  test('render with URL', () => {
    const testUrl = "not-a-real-url";
    render(<LoginCard loginUrl={testUrl}/>);
    const signinAnchor = screen.getByRole("link");
    expect(signinAnchor).toBeInTheDocument();
    expect(signinAnchor).toHaveAttribute('href', testUrl);
  });
});

