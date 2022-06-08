// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for App.js.

import {render, screen} from '@testing-library/react';
import App from '../App';

describe("app functions", () => {
  test('renders title', () => {
    render(<App />);
    expect(screen.getByRole('heading', {name: /Amazon Aurora Serverless Item Tracker/i}))
      .toBeInTheDocument();
  });
});
