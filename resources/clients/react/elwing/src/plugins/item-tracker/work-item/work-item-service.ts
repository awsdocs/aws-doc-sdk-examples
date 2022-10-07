// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { RestService } from "../rest-service";

/**
 * Sends REST requests to get work items, add new work items, modify work items,
 * and send an email report.
 *
 * The base URL of the REST service is stored in config.json. If necessary, update this
 * value to your endpoint.
 */

export type WorkItemStatus = "archived" | "active" | "";

export interface WorkItem {
  id: string;
  name: string;
  guide: string;
  description: string;
  status: WorkItemStatus;
}

export class WorkItemService extends RestService<WorkItem> {
  constructor(baseUrl?: string) {
    super("/items", baseUrl);
  }

  /**
   * Sends a PUT request to archive an active item.
   */
  async archiveItem(itemId: string) {
    return await fetch(`${this.url()}/${itemId}:archive`);
  }

  /**
   * Sends a POST request to email a report of work items.
   */
  mailItem = async (email: string) => {
    return await fetch(`${this.url()}:report`, {
      body: JSON.stringify({ email }),
    });
  };
}

export const BASE_URL = "http://localhost:3000";
export const workItemService = new WorkItemService();
