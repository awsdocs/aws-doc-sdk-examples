// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for WorkItems.js.

import { act, render, screen, within } from '@testing-library/react';
import userEvent from "@testing-library/user-event";
import axios from "axios";
import configData from '../config.json'
import {WorkItems} from '../WorkItems';

jest.mock("axios");

describe("work items functions", () => {
  test('renders select', async () => {
    await act(async () => {render(<WorkItems />)});
    expect(screen.getByRole('combobox', {name: "Status"})).toBeInTheDocument();
    expect(screen.getByRole('option', {name: "Active"})).toBeInTheDocument();
    expect(screen.getByRole('option', {name: "Archived"})).toBeInTheDocument();
  });

  test('renders report form', async () => {
    await act(async () => {render(<WorkItems />)});
    expect(screen.getByRole('textbox', {name: "Recipient's email"})).toBeInTheDocument();
    expect(screen.getByRole('button', {name: "Send report"})).toBeInTheDocument();
  });

  test('renders no items', async () => {
    axios.get.mockResolvedValueOnce({data: []});
    await act(async () => {render(<WorkItems />)});
    expect(axios.get).toHaveBeenCalledWith(`${configData.BASE_URL}/items/active`);
    expect(screen.getByRole('alert')).toHaveTextContent(/No work items/i);
  });

  test('renders error on get item error', async () => {
    const error = "Test error!";
    axios.get.mockRejectedValueOnce(new Error(error));
    await act(async () => {render(<WorkItems />)});
    expect(axios.get).toHaveBeenCalledWith(`${configData.BASE_URL}/items/active`);
    const alerts = screen.getAllByRole('alert');
    expect(alerts[0]).toHaveTextContent(error);
  });

  test('renders active and archive items', async () => {
    let items = [];
    for (let index = 1; index < 4; index++) {
      items.push({
        id: `id-${index}`,
        name: `user-${index}`,
        guide: `guide-${index}`,
        description: `desc-${index}`,
        status: `status-${index}`
      });
    }
    axios.get.mockResolvedValueOnce({data: items});
    await act(async () => {render(<WorkItems />)});
    expect(axios.get).toHaveBeenCalledWith(`${configData.BASE_URL}/items/active`);
    items.forEach(item => {
      expect(screen.getByText(item.id)).toBeInTheDocument();
      expect(screen.getByText(item.name)).toBeInTheDocument();
      expect(screen.getByText(item.guide)).toBeInTheDocument();
      expect(screen.getByText(item.description)).toBeInTheDocument();
      expect(screen.getByText(item.status)).toBeInTheDocument();
    });
    expect(screen.getAllByText('🗑').length).toBe(3);

    axios.get.mockResolvedValueOnce({data: items});
    await act(() => {
      userEvent.selectOptions(screen.getByRole('combobox', {name: "Status"}), "archive");
    })
    expect(axios.get).toHaveBeenCalledWith(`${configData.BASE_URL}/items/archive`);
    expect(screen.queryAllByText('🗑').length).toBe(0);
  });

  test('handles archive item click', async () => {
    const index = 1;
    const item = {
      id: `id-${index}`,
      name: `user-${index}`,
      guide: `guide-${index}`,
      description: `desc-${index}`,
      status: `status-${index}`
    };
    axios.get.mockResolvedValueOnce({data: [item]});
    axios.put.mockResolvedValueOnce();
    await act(async () => {render(<WorkItems />)});
    await act(() => {
      userEvent.click(screen.getByRole('button', {name: "🗑"}));
    })
    expect(axios.put).toHaveBeenCalledWith(`${configData.BASE_URL}/items/id-1`);
    expect(screen.queryAllByText('🗑').length).toBe(0);
  });

  test('handles error on archive item click', async () => {
    const index = 1;
    const item = {
      id: `id-${index}`,
      name: `user-${index}`,
      guide: `guide-${index}`,
      description: `desc-${index}`,
      status: `status-${index}`
    };
    const error = "Test error!";
    axios.get.mockResolvedValueOnce({data: [item]});
    axios.put.mockRejectedValueOnce(new Error(error));
    await act(async () => {render(<WorkItems />)});
    await act(() => {
      userEvent.click(screen.getByRole('button', {name: "🗑"}));
    })
    expect(axios.put).toHaveBeenCalledWith(`${configData.BASE_URL}/items/id-1`);
    const alerts = screen.getAllByRole('alert');
    expect(alerts[0]).toHaveTextContent(error);
  });

  test('handles send report click', async () => {
    const email = "test@example.com";
    axios.get.mockResolvedValueOnce({data: []});
    axios.post.mockResolvedValueOnce();
    await act(async () => {render(<WorkItems />)});
    await act(() => {
      userEvent.type(screen.getByRole('textbox', {name: "Recipient's email"}), email);
      userEvent.click(screen.getByRole('button', {name: "Send report"}));
    });
    expect(axios.post).toHaveBeenCalledWith(`${configData.BASE_URL}/report`, {email: email});
  });

  test('handles error on send report click', async () => {
    const email = "test@example.com";
    const error = "Test error!";
    axios.get.mockResolvedValueOnce({data: []});
    axios.post.mockRejectedValueOnce(new Error(error));
    await act(async () => {render(<WorkItems />)});
    await act(() => {
      userEvent.type(screen.getByRole('textbox', {name: "Recipient's email"}), email);
      userEvent.click(screen.getByRole('button', {name: "Send report"}));
    });
    expect(axios.post).toHaveBeenCalledWith(`${configData.BASE_URL}/report`, {email: email});
    const alerts = screen.getAllByRole('alert');
    expect(alerts[0]).toHaveTextContent(error);
  });

});
