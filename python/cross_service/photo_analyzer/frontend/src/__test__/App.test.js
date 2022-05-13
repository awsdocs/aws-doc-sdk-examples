// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for App.js.

import { render, screen } from '@testing-library/react';
import App from '../App';
import $ from "jquery";

describe("app functions", () => {
  test('renders title', () => {
    $.get.mockImplementation(() => {
      return $.Deferred();
    });
    render(<App />);
    expect(screen.getByText(/Amazon Rekognition Photo Analyzer/i)).toBeInTheDocument();
  });
});