// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for the ImageDisplay component.

import React from "react";
import {render, screen } from '@testing-library/react';

import {ImageDisplay} from "../ImageDisplay";

describe('imageData props', () => {
  test('render with no image', () => {
    render(<ImageDisplay shownPolygons={[]}/>);
    expect(screen.getByRole('img')).toBeInTheDocument();
  });

  test('render with test image', () => {
    render(<ImageDisplay imageData={{base64Data: 'hi'}} shownPolygons={[]}/>);
    expect(screen.getByRole('img')).toBeInTheDocument();
  });

  test('render with polygons, no errors', () => {
    render(<ImageDisplay imageData={{base64Data: 'hi'}} shownPolygons={[
      {Geometry: {Polygon: [{X: 0, Y: 0}, {X: 1, Y: 1}, {X: 2, Y: 2}]}}
    ]}/>);
  });
});
