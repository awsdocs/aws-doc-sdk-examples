// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the ImageLoader component.

import React from "react";
import {render, screen } from '@testing-library/react';

import {ImageLoader} from "../ImageLoader";

describe('rendering', () => {
  test('no props', () => {
    render(<ImageLoader/>);
    expect(screen.getByRole("textbox", {name: /Bucket/i})).toHaveValue('');
    expect(screen.getByRole("textbox", {name: /Image/i})).toHaveValue('');
    expect(screen.getByRole('status')).toHaveClass("visually-hidden");
  });

  test('bucket and image props', () => {
    const bucket = 'test-bucket';
    const image = 'test-image';
    render(<ImageLoader imageBucket={bucket} imageKey={image}/>);
    expect(screen.getByRole("textbox", {name: /Bucket/i})).toHaveValue(bucket);
    expect(screen.getByRole("textbox", {name: /Image/i})).toHaveValue(image);
  });

  test('error message', () => {
    const testError = "test error"
    render(<ImageLoader modelError={testError}/>);
    expect(screen.getByText(testError)).toBeInTheDocument();
  });
});
