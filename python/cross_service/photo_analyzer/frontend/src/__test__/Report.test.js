// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for Report.js.

import {act, render, screen} from '@testing-library/react';
import $ from "jquery";
import {Report} from '../Report';
import userEvent from "@testing-library/user-event";
import {fireEvent} from "@testing-library/dom";

describe("report module", () => {
  test('render default', () => {
    act(() => {
      render(<Report/>)
    });
    expect(screen.getByText(/Analyze all photos/i).type).toBe('button');
  });

  test('render modal after click', async () => {
    $.get.mockImplementation((restUrl, fn) => {
      fn(['label-1', 'label-2']);
      return $.Deferred().resolve();
    });

    act(() => {
      render(<Report/>);
    });
    await userEvent.click(screen.getByRole('button', {name: /Analyze all photos/i}));
    expect(screen.getByText(/label-1/i).className.includes('list-group-item')).toBe(true);
    expect(screen.getByText(/label-2/i).className.includes('list-group-item')).toBe(true);
    expect(screen.getByText(/Send report/i)).toBeDisabled();
  });

  test('send report', async () => {
    const sender = 'sender@example.com';
    const recipient = 'recipient@example.com';
    const message = 'Test message';
    const labels = ['label-1', 'label-2'];
    $.get.mockImplementation((restUrl, fn) => {
      fn(labels);
      return $.Deferred().resolve();
    });

    $.post.mockImplementation((xhr) => {
      const sendData = JSON.parse(xhr.data);
      expect(sendData.sender).toBe(sender);
      expect(sendData.recipient).toBe(recipient);
      expect(sendData.message).toBe(message);
      expect(sendData.analysis_labels).toStrictEqual(labels);
      return $.Deferred().resolve();
    });

    act(() => {
      render(<Report/>);
    });
    const analyzeButton = screen.getByRole('button', {name: /Analyze all photos/i});
    await userEvent.click(analyzeButton);
    await fireEvent.change(screen.getByPlaceholderText(sender), {target: {value: sender}});
    await fireEvent.change(screen.getByPlaceholderText(recipient), {target: {value: recipient}});
    await fireEvent.change(screen.getByLabelText('Message'), {target: {value: message}})
    const sendReport = screen.getByText(/Send report/i);
    expect(sendReport).not.toBeDisabled();
    await userEvent.click(sendReport);
  });
});