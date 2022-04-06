// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for PhotoList.js.

import {act, render, screen} from '@testing-library/react';
import userEvent from "@testing-library/user-event";
import $ from "jquery";
import {PhotoList} from '../PhotoList';
import {fireEvent} from "@testing-library/dom";

describe("photo list module", () => {
  test('render with no photos', () => {
    $.get.mockImplementation(() => {
      return $.Deferred().resolve();
    })

    act(() => {
      render(<PhotoList/>)
    });
    expect(screen.getByText(/Upload some photos/i)).toBeInTheDocument();
    expect(screen.getByText(/Upload a photo/i)).toBeInTheDocument();
  });

  test('render with photos', () => {
    let photos = [
      {'name': 'test-photo-1'},
      {'name': 'test-photo-2'},
      {'name': 'test-photo-3'},
    ];

    $.get.mockImplementation((restUrl, fn) => {
      fn(photos);
      return $.Deferred().resolve();
    });

    act(() => {
      render(<PhotoList photos={photos}/>);
    });
    expect(screen.getByText(/Select a photo/i)).toBeInTheDocument();
    photos.forEach((photo) => {
      expect(screen.getByText(photo.name).className.includes('list-group-item-action')).toBe(true);
    });
    expect(screen.getByLabelText('Upload a photo').type).toBe('file');
    const uploadButton = screen.getByText('Upload')
    expect(uploadButton.type).toBe('button');
    expect(uploadButton.disabled).toBe(true);
    expect(screen.getByText(/Analyze all photos/i).type).toBe('button');
  });

  test('upload file', async () => {
    const testImage = new File(
      [new Blob(['test-image'], {type: 'image/png'})],
      'test-image.png');

    $.get.mockImplementation(() => {
      return $.Deferred().resolve();
    });
    $.post.mockImplementation((xhr) => {
      xhr.success();
      return $.Deferred().resolve();
    });

    act(() => { render(<PhotoList/>) });
    await fireEvent.change(screen.getByLabelText('Upload a photo'), {target: {files: [testImage]}});
    await userEvent.click(screen.getByRole('button', {name: /Upload/i}));

    expect(screen.getByText(testImage.name).className.includes('list-group-item-action')).toBe(true);
  });

  test('render photos error', () => {
    const photosError = "Test photos error.";
    $.get.mockImplementation(() => {
      return $.Deferred().reject(photosError);
    });

    act(() => { render(<PhotoList/>) });

    expect(screen.getByText(photosError)).toBeInTheDocument();
  });

  test('render upload error', async () => {
    const uploadError = "Test upload error.";
    const testImage = new File(
      [new Blob(['test-image'], {type: 'image/png'})],
      'test-image.png');

    $.get.mockImplementation(() => {
      return $.Deferred().resolve();
    });
    $.post.mockImplementation((xhr) => {
      xhr.fail(uploadError);
      return $.Deferred().resolve();
    });

    act(() => { render(<PhotoList/>) });
    await fireEvent.change(screen.getByLabelText('Upload a photo'), {target: {files: [testImage]}});
    await userEvent.click(screen.getByRole('button', {name: /Upload/i}));

    expect(screen.getByText(uploadError)).toBeInTheDocument();
  });
});