// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for WorkItem.js.

import { act, render, screen } from '@testing-library/react';
import userEvent from "@testing-library/user-event";
import axios from "axios";
import configData from '../config.json'
import {WorkItem} from '../WorkItem';

jest.mock("axios");

describe("work item functions", () => {
  test('renders button', async () => {
    await act(async () => {render(<WorkItem />)});
    expect(screen.getByRole('button', {name: "Add item"})).toBeInTheDocument();
  });

  test('renders form on click', async () => {
    await act(async () => {render(<WorkItem />)});
    await act(async () => {
      await userEvent.click(screen.getByRole('button', {name: "Add item"}));
    });
    expect(screen.getByRole('textbox', {name: 'User'})).toBeInTheDocument();
    expect(screen.getByRole('textbox', {name: 'Guide'})).toBeInTheDocument();
    expect(screen.getByRole('textbox', {name: 'Description'})).toBeInTheDocument();
    expect(screen.getByRole('textbox', {name: 'Status'})).toBeInTheDocument();
    expect(screen.getAllByRole('button', {name: 'Close'}).length).toBe(2);
    expect(screen.getByRole('button', {name: 'Add'}).disabled).toBe(true);
  });

  test('enables Add when form is filled', async () => {
    await act(async () => {render(<WorkItem />)});
    await act(async () => {
      await userEvent.click(screen.getByRole('button', {name: "Add item"}));
    });
    const user = 'test-user';
    const guide = 'test-guide';
    const desc = 'test-desc';
    const status = 'test-status';
    await act(async () => {
      userEvent.type(screen.getByRole('textbox', {name: 'User'}), user);
      userEvent.type(screen.getByRole('textbox', {name: 'Guide'}), guide);
      userEvent.type(screen.getByRole('textbox', {name: 'Description'}), desc);
      userEvent.type(screen.getByRole('textbox', {name: 'Status'}), status);
    });
    const addButton = screen.getByRole('button', {name: 'Add'});
    expect(addButton.disabled).toBe(false);

    axios.post.mockResolvedValueOnce();
    await act(async () => {
      userEvent.click(addButton);
    })
    expect(axios.post).toHaveBeenCalledWith(`${configData.BASE_URL}/items`, {
      name: user, guide: guide, description: desc, status: status
    })

    expect(screen.getByRole('button', {name: 'Add item'}).disabled).toBe(false);
  });
});
