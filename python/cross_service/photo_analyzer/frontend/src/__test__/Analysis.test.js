// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for Analysis.js.

import {act, render, screen} from '@testing-library/react';
import $ from "jquery";
import {Analysis} from '../Analysis';

describe("analysis module", () => {
  test('render without photo', () => {
    act(() => {
      render(<Analysis/>)
    });
    expect(screen.getByText(/Photo analysis/i)).toBeInTheDocument();
    expect(screen.getByAltText("Nothing to show yet!")).toBeInTheDocument();
  });

  test('render with photo no labels', () => {
    let test_photo = {'name': 'test-photo'};
    const test_url = 'test-url';

    const jqdef = $.Deferred();
    $.get.mockImplementation((restUrl, fn) => {
      if (restUrl.endsWith(test_photo.name)) {
        fn({url: test_url});
      } else if (restUrl.endsWith('labels')) {
        fn(null);
      }
      return jqdef.resolve();
    });

    act(() => {
      render(<Analysis photo={test_photo}/>);
    });
    const img = screen.getByAltText(test_photo.name);
    expect(img).toBeInTheDocument();
    expect(img).toHaveAttribute('src', test_url);
  });

  test('render with photo and labels', () => {
    let test_photo = {'name': 'test-photo'}
    const test_url = 'test-url';

    $.get.mockImplementation((restUrl, fn) => {
      if (restUrl.endsWith(test_photo.name)) {
        fn({url: test_url});
      } else if (restUrl.endsWith('labels')) {
        fn([
          {Name: 'label-1', Instances: []},
          {Name: 'label-2', Instances: []},
          {Name: 'label-3', Instances: [{}, {}]},
          {Name: 'label-4', Instances: [{}]},
        ])
      }
      return $.Deferred().resolve();
    });

    act(() => {
      render(<Analysis photo={test_photo}/>);
    });
    expect(screen.getByText(/Items found/i)).toBeInTheDocument();
    expect(screen.getByText(/Select a boxed item/i)).toBeInTheDocument();
    expect(screen.getByText(/label-1/i).className.includes('list-group-item')).toBe(true);
    expect(screen.getByText(/label-2/i).className.includes('list-group-item')).toBe(true);
    let labels = screen.getAllByText(/label-3/i);
    expect(labels[0].className.includes('list-group-item')).toBe(true);
    expect(labels[1].type).toBe('button');
    labels = screen.getAllByText(/label-4/i);
    expect(labels[0].className.includes('list-group-item')).toBe(true);
    expect(labels[1].type).toBe('button');
  });

  test('render get errors', () => {
    const test_photo = {'name': 'test-photo'};
    const photo_error = "Test error from get photo.";
    const labels_error = "Test error from get labels.";

    $.get.mockImplementation((restUrl, fn) => {
      let error = null;
      if (restUrl.endsWith(test_photo.name)) {
        error = photo_error;
      } else if (restUrl.endsWith('labels')) {
        error = labels_error;
      }
      return $.Deferred().reject(error);
    });

    act(() => {
      render(<Analysis photo={test_photo}/>);
    });
    expect(screen.getByText(photo_error)).toBeInTheDocument();
    expect(screen.getByText(labels_error)).toBeInTheDocument();
  });

  test('render no label message', () => {
    const test_photo = {'name': 'test-photo'};

    $.get.mockImplementation((restUrl, fn) => {
      if (restUrl.endsWith('labels')) {
        fn([]);
      }
      return $.Deferred().resolve();
    });

    act(() => {
      render(<Analysis photo={test_photo}/>);
    });
    expect(screen.getByText(/No labels found/i)).toBeInTheDocument();
  });
});