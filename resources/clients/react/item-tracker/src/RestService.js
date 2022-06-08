// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * Sends REST requests to get work items, add new work items, modify work items,
 * and send an email report.
 *
 * The base URL of the REST service is stored in config.json. If necessary, update this
 * value to your endpoint.
 */

import axios from 'axios'
import configData from './config.json'

/**
 * Sends a POST request to add a new work item.
 *
 * @param item: The work item to add.
 * @returns {Promise<void>}
 */
export const addWorkItem = async (item) => {
  await axios.post(`${configData.BASE_URL}/items`, item);
};

/**
 * Sends a GET request to retrieve work items that are in the specified state.
 *
 * @param state: The state of work items to retrieve. Can be either 'active' or 'archive'.
 * @returns {Promise<AxiosResponse<any>>}: The list of work items that have the
 *                                         specified state.
 */
export const getWorkItems = async (state) => {
  return await axios.get(`${configData.BASE_URL}/items/${state}`);
};

/**
 * Sends a PUT request to archive an active item.
 *
 * @param itemId: The ID of the item to archive.
 * @returns {Promise<void>}
 */
export const archiveItem = async (itemId) => {
  await axios.put(`${configData.BASE_URL}/items/${itemId}`);
}

/**
 * Sends a POST request to email a report of work items.
 *
 * @param email: The report recipient's email address.
 * @returns {Promise<void>}
 */
export const mailItem = async (email) => {
  await axios.post(`${configData.BASE_URL}/report`, {email: email});
}
